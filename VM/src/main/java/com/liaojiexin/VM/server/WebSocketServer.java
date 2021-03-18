package com.liaojiexin.VM.server;

import com.liaojiexin.VM.handler.ServerHandler;
import com.liaojiexin.VM.handler.UdpHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @ClassName: WebSocketServer
 * @Description: TODO
 * @version: 1.0      服务端引导类
 * @author: liaojiexin
 * @date: 2021/3/16 13:45
 */
public class WebSocketServer {

    public static void main(String[] args) throws Exception{
        //定义线程组
        EventLoopGroup bossGroup =new NioEventLoopGroup();
        EventLoopGroup workGroup =new NioEventLoopGroup();

        try{
            //多协议，TCP、WebSocket等
            ServerBootstrap serverBootstrap=new ServerBootstrap();
            serverBootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    //针对subGroup做的子处理器，childHandler针对WebSokect的初始化器
                    .childHandler(new WebSocketinitializer());
            //绑定端口并以同步方式进行使用
            ChannelFuture channelFuture = serverBootstrap.bind(10086).sync();

            //针对channelFuture，进行相应的监听
            channelFuture.channel().closeFuture().sync();

            //UDP
            Bootstrap bootstrap = new Bootstrap();//udp不能使用ServerBootstrap
            bootstrap.group(workGroup);
            bootstrap.channel(NioDatagramChannel.class);//设置UDP通道
            bootstrap.option(ChannelOption.SO_BROADCAST, true);// 支持广播
            bootstrap.option(ChannelOption.SO_BACKLOG, 128);
            bootstrap.option(ChannelOption.SO_RCVBUF, 1024 * 1024);// 设置UDP读缓冲区为1M
            bootstrap.option(ChannelOption.SO_SNDBUF, 1024 * 1024);// 设置UDP写缓冲区为1M
            bootstrap.handler(new UdpHandler());


        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

}
