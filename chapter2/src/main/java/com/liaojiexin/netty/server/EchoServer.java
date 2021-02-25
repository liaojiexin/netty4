package com.liaojiexin.netty.server;

import com.liaojiexin.netty.handler.EchoServerHandler;
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
        new EchoServer(port).start();       //调用服务器start方法
    }

    public void start() throws Exception{
        final EchoServerHandler serverHandler=new EchoServerHandler();
        //1.创建EventLoopGroup
        EventLoopGroup group=new NioEventLoopGroup();
        try{
            //2.创建ServerBootstrap
            ServerBootstrap bootstrap=new ServerBootstrap();
            bootstrap.group(group)
                    //3.指定所使用的NIO传输Channel
                    .channel(NioServerSocketChannel.class)
                    //4.使用指定的端口设置套接字地址
                    .localAddress(new InetSocketAddress(port))
                    //5.添加一个EchoServerHandler到子Channel的ChannelPipeline
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //EchoServerHandler被标注为@Shareable，所以我们可以总是使用同样的实例
                            ch.pipeline().addLast(serverHandler);
                        }
                    });
            //6.异步绑定服务器，调用sync方法阻塞等待知道绑定完成
            ChannelFuture channelFuture=bootstrap.bind().sync();
            //7.获取Channel的CloseFuture，并且阻塞当前线程知道它完成
            channelFuture.channel().closeFuture().sync();
        }finally {
            //8.关闭EventLoopGroup，释放所有的资源
            group.shutdownGracefully().sync();
        }
    }
}
