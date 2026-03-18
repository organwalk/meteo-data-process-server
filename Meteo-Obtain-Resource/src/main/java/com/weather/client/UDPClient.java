package com.weather.client;

import com.weather.config.UdpProperties;
import com.weather.handler.UDPClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class UDPClient {
    private final UdpProperties properties;
    private final UDPClientHandler udpClientHandler;

    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    private final Bootstrap bootstrap = new Bootstrap();
    private Channel channel;
    private InetSocketAddress serverAddress;

    @PostConstruct
    public void init() {
        this.serverAddress = new InetSocketAddress(properties.getRemoteHost(), properties.getRemotePort());
        this.channel = bootstrap.group(eventLoopGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(655350))
                .handler(new ChannelInitializer<DatagramChannel>() {
                    @Override
                    protected void initChannel(DatagramChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(udpClientHandler);
                    }
                })
                .bind(0)
                .syncUninterruptibly()
                .channel();
    }

    public void send(String message) throws InterruptedException {
        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(data), serverAddress)).sync();
    }

    @PreDestroy
    public void shutdown() {
        eventLoopGroup.shutdownGracefully();
    }
}
