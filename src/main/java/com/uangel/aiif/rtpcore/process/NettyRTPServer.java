/* Copyright 2018 (C) UANGEL CORPORATION <http://www.uangel.com> */

/**
 * Acs AMF
 *
 * @file NettyRTPServer.java
 * @author Tony Lim
 */

package com.uangel.aiif.rtpcore.process;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ThreadFactory;

import static org.slf4j.LoggerFactory.getLogger;

public class NettyRTPServer {
    private static final Logger logger = getLogger(NettyRTPServer.class);
    Bootstrap b;
    NioEventLoopGroup group;

    public NettyRTPServer() {
    }

    public NettyRTPServer run(int consumerCount, int bufferSize) {
        ThreadFactory threadFactory = new BasicThreadFactory.Builder()
                .namingPattern("RTPServer-NioEventLoop-%d")
                .daemon(true)
                .priority(Thread.MAX_PRIORITY)
                .build();
        group = new NioEventLoopGroup(consumerCount, threadFactory);
        try {

            b = new Bootstrap();
            b.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, false)
                    .option(ChannelOption.SO_SNDBUF, bufferSize)
                    .option(ChannelOption.SO_RCVBUF, bufferSize)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        public void initChannel(final NioDatagramChannel ch) throws Exception {
                            final ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new IncomingPacketHandler());
                        }
                    });

        } catch (Exception e) {
            logger.error("Exception", e);
        } finally {
            logger.info("In Server Finally");
        }
        return null;
    }

    public void close() {
        group.shutdownGracefully();
    }

    /**
     * netty server channel을 생성한다.
     *
     * @param ip   bind ip
     * @param port bind port
     * @return Channel
     */
    public Channel openChannel(String ip, int port) {
        InetAddress address;
        try {
            address = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            logger.warn("() () () () UnknownHostException is occured. ip={}", ip, e);
            return null;
        }

        try {
            return b.bind(address, port)
                    .sync()
                    .channel();
        } catch (Exception e) {
            logger.warn("() () () () channel Interrupted! socket={}:{}", ip, port, e);
        }
        return null;
    }

    public Channel openChannel(int port) {
        try {
            return b.bind(port)
                    .sync()
                    .channel();
        } catch (InterruptedException e) {
            logger.warn("() () () () channel Interrupted! port={}", port, e);
            return null;
        }
    }

    /**
     * netty server channel을 close 한다.
     *
     * @param ch 종료하려는 channel
     */
    public void closeChannel(Channel ch) {
        if (ch != null) {
            try {
                ch.close().sync();
            } catch (Exception e) {
                logger.warn("Channel Close Err", e);
            }
        }
    }

    public void closeChannel(int port) {
    }

}
