package com.liaojiexin.netty.netty_in_action.server;

import com.liaojiexin.netty.netty_in_action.handler.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * @ClassName: EchoServer
 * @Description: TODO
 * @version: 1.0
 * @author: liaojiexin
 * @date: 2021/2/24 17:17
 */
public class EchoServer {
    private final int port;

    public EchoServer(int port){
        this.port=port;
    }

    public static void main(String[] args) throws Exception {
        //设置端口值（如果端口参数格式不正确，则抛出一个NumberFormatException）
        if (args.length!=1){
            System.err.println("Usage:"+EchoServer.class.getSimpleName()+"<port>");
        }
        int port=Integer.parseInt(args[0]);
        new EchoServer(port).start();
    }

    public void start() throws Exception{
        final EchoServerHandler serverHandler=new EchoServerHandler();
        //1.创建EventLoopGroup
        EventLoopGroup group=new NioEventLoopGroup();
        try{
            //2.创建ServerBootstrap
            ServerBootstrap bootstrap=new ServerBootstrap();
            bootstrap.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(serverHandler);
                        }
                    });
            ChannelFuture channelFuture=bootstrap.bind().sync();
            channelFuture.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully().sync();
        }
    }
}
