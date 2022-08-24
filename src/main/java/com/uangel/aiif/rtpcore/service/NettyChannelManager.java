package com.uangel.aiif.rtpcore.service;

import com.uangel.aiif.config.AiifConfig;
import com.uangel.aiif.rtpcore.process.NettyRTPServer;
import com.uangel.aiif.service.AppInstance;
import com.uangel.aiif.session.model.CallInfo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.UnresolvedAddressException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Getter
@Setter
@Slf4j
public class NettyChannelManager {
    private static final AppInstance appInstance = AppInstance.getInstance();

    // Key = Local Port, Value = Netty Channel
    private static final Map<Integer, RtpChannelInfo> serverChannelMap = new ConcurrentHashMap<>();
    private final Queue<Integer> remainingPort = new ConcurrentLinkedQueue<>();
    private AiifConfig userConfig;
    private NettyRTPServer rtpServer = null;
    private InetSocketAddress redunAddr;


    private static final NettyChannelManager INSTANCE = new NettyChannelManager();

    private NettyChannelManager() {

    }

    public static NettyChannelManager getInstance() {
        return INSTANCE;
    }

    /**
     * 포트 할당
     *
     * @return 포트바인딩된 RtpChannelInfo
     */
    public RtpChannelInfo allocPort(CallInfo callInfo) {
        Integer port = remainingPort.poll();
        if (port == null) {
            log.warn("Can not Allocate Port {}", port);
            return null;
        }
        Channel channel = rtpServer.openChannel(port);
        serverChannelMap.put(port, new RtpChannelInfo(channel, callInfo, port));
        return serverChannelMap.get(port);
    }

    /**
     * 포트 할당 해제
     *
     * @param port 할당 해제 할 포트
     */
    public void deallocPort(int port) {
        log.debug("Dealloc Port {}", port);
        if (serverChannelMap.containsKey(port) && !remainingPort.contains(port)) {
            remainingPort.add(port);
            Channel ch = serverChannelMap.get(port).getChannel();
            serverChannelMap.get(port).setOnClosing(true);
            rtpServer.closeChannel(ch);
            serverChannelMap.remove(port);
        }
    }

    public List<RtpChannelInfo> getRtpChannels() {
        synchronized (serverChannelMap) {
            return new ArrayList<>(serverChannelMap.values());
        }
    }

    public void openRtpServer() {
        userConfig = appInstance.getConfig();
        if (rtpServer != null) rtpServer.close();
        rtpServer = new NettyRTPServer();
        rtpServer.run(userConfig.getRmqConsumerCount(), userConfig.getUdpRcvBufferSize());
        // RTP Server/Client Init
        remainingPort.clear();
        for (int port = userConfig.getLocalUdpPortMin(); port <= userConfig.getLocalUdpPortMax(); port++) {
            try {
                remainingPort.add(port);
            } catch (Exception e) {
                log.error("() () () () Exception to RTP port resource in Queue", e);
            }
        }
        log.info("() () () () Ready to RTP port resource in Queue. (port range: {} - {})", userConfig.getLocalUdpPortMin(), userConfig.getLocalUdpPortMax());
    }


    public void closeRtpServer() {
        if (rtpServer != null) {
            rtpServer.close();
        }
    }


    public CallInfo getCallByPort(int port) {
        return Optional.ofNullable(serverChannelMap.get(port))
                .map(RtpChannelInfo::getCallInfo)
                .orElse(null);
    }

    /**
     * @param port Channel을 찾을 포트 (Key)
     * @return 포트를 선점하고 있는 Channel. 선점중인 Channel이 없을 경우 null
     */
    public Channel getChannelByPort(int port) {
        return serverChannelMap.get(port).getChannel();
    }

    public void sendMsg(int fromPort, InetSocketAddress toAddr, byte[] data) {
        sendMsg(fromPort, toAddr, Unpooled.copiedBuffer(data));
    }

    public void sendMsg(int fromPort, InetSocketAddress toAddr, ByteBuf buf) {
        Channel ch = Optional.ofNullable(serverChannelMap.get(fromPort))
                .map(RtpChannelInfo::getChannel).orElse(null);
        try {
            if (toAddr == null) throw new NullPointerException("To Address is null");
            if (toAddr.isUnresolved()) {
                String ip = toAddr.getHostName().replace("/", "");
                byte[] bytes = InetAddress.getByName(ip).getAddress();
                toAddr = new InetSocketAddress(InetAddress.getByAddress(bytes), toAddr.getPort());
            }
            if (ch == null) {
                log.trace("Netty Channel is null");
                return;
            }
            if (!Optional.ofNullable(serverChannelMap.get(fromPort)).map(RtpChannelInfo::isOnClosing).orElse(false)) {
                ch.writeAndFlush(new DatagramPacket(buf, toAddr));
            }
        } catch (UnresolvedAddressException e) {
            log.warn("() () () () UnresolvedAddressException Exception", e);
        } catch (Exception e) {
            log.warn("() () () () Unexpected Exception Occurs while Send UDP Message", e);
        }
    }
}
