package com.liaojiexin.netty.server;

import com.liaojiexin.netty.handler.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class Server extends ChannelInboundHandlerAdapter {
    private final int port;

    public Server(int port){
        this.port=port;
    }

    public void start() throws InterruptedException {
        EventLoopGroup boss =new NioEventLoopGroup();
        EventLoopGroup worker=new NioEventLoopGroup();
        try{
            ServerBootstrap bootstrap=new ServerBootstrap();
            bootstrap.group(boss,worker)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ServerHandler());
                        }
                    });
            //6.异步绑定服务器，调用sync方法阻塞等待知道绑定完成
            ChannelFuture channelFuture=bootstrap.bind().sync();
            //7.获取Channel的CloseFuture，并且阻塞当前线程知道它完成
            channelFuture.channel().closeFuture().sync();
        }finally {
            boss.shutdownGracefully().sync();
            worker.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception{
        //设置端口值（如果端口参数格式不正确，则抛出一个NumberFormatException）
        if (args.length!=1){
            System.err.println("Usage:"+Server.class.getSimpleName()+"<port>");
        }
        int port=Integer.parseInt(args[0]);
        new Server(port).start();       //调用服务器start方法
    }
}
