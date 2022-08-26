package com.uangel.aiif.rtpcore.process;

import com.uangel.aiif.rtpcore.base.RtpHeader;
import com.uangel.aiif.rtpcore.service.NettyChannelManager;
import com.uangel.aiif.service.AppInstance;
import com.uangel.aiif.session.model.CallInfo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.util.concurrent.ExecutorService;

public class IncomingPacketHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private static final AppInstance appInstance = AppInstance.getInstance();
    private static final NettyChannelManager nettyChannelManager = NettyChannelManager.getInstance();

    private int localPort;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        String[] bits = ctx.channel().localAddress().toString().split(":");
        this.localPort = Integer.parseInt(bits[bits.length - 1]);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
        ByteBuf buf = msg.content();
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        RtpHeader rtpHeader;
        try {
            rtpHeader = new RtpHeader(data, 0);
        } catch (Exception e) {
            // Ignore Wrong Packet.
            return;
        }

        CallInfo callInfo = nettyChannelManager.getCallByPort(localPort);
        if(!callInfo.getSttConverter().isRunning()) return;

        byte[] rtpPayload = new byte[rtpHeader.getPayloadLength()];
        System.arraycopy(rtpHeader.getData(), rtpHeader.getPayloadPosition(), rtpPayload, 0, rtpHeader.getPayloadLength());
        callInfo.getSttConverter().inputData(rtpPayload);
    }
}