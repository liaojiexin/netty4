package com.liaojiexin.netty23.c11;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestConnectionTimeout {
    public static void main(String[] args) {

        /**
         * 这里对Netty的参数配置方式说明一下:
         * 1、客户端上通过 .option() 方法配置参数 给 SocketChannel 配置参数
         *
         * 2、服务端上
         * new ServerBootstrap().option() 是给 ServerSocketChannel 配置参数
         * new ServerBootstrap().childOption() 则是 给 SocketChannel 配置参数
         */

        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    //配置客户端连接超时时间
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                    .channel(NioSocketChannel.class)
                    .handler(new LoggingHandler());
            ChannelFuture future = bootstrap.connect("127.0.0.1", 8080);
            future.sync().channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("timeout");
        } finally {
            group.shutdownGracefully();
        }
    }
}