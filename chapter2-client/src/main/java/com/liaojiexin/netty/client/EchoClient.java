package com.liaojiexin.netty.client;

import com.liaojiexin.netty.handler.EchoClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @ClassName: EchoClient
 * @Description: TODO
 * @version: 1.0
 * @author: liaojiexin
 * @date: 2021/2/25 10:00
 */
public class EchoClient {
    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception{
        EventLoopGroup group =new NioEventLoopGroup();
        try {
            //创建Bootstrap
            Bootstrap bootstrap=new Bootstrap();
            //指定EventLoopGroup以处理客户端事件；需要适用NIO的实现
            bootstrap.group(group)
                    //适用于NIO传输Channel类型
                    .channel(NioSocketChannel.class)
                    //设置服务器的InetSocketAddress
                    .remoteAddress(new InetSocketAddress(host,port))
                    //在创建Channel时，想ChannelPipeline中添加一个EchoClientHanler实例
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            //连接到远程节点，阻塞等待知道连接完成
            ChannelFuture future=bootstrap.connect().sync();
            //阻塞，知道Channel关闭
            future.channel().closeFuture().sync();
        }finally {
            //关闭线程池并且释放所有的资源
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length!=2){
            System.err.println("Usage:"+EchoClient.class.getSimpleName()+"<host><port>");
            return;
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        new EchoClient(host, port).start();
    }
}
