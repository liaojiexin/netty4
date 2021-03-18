package com.liaojiexin.VM.server;

import com.liaojiexin.VM.handler.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @ClassName: WebSocketServer
 * @Description: TODO
 * @version: 1.0
 * @author: liaojiexin
 * @date: 2021/3/16 13:45
 */
public class WebSocketServer {

    public static void main(String[] args) throws Exception{
        //定义线程组
        EventLoopGroup bossGroup =new NioEventLoopGroup();
        EventLoopGroup workGroup =new NioEventLoopGroup();

        try{
            ServerBootstrap serverBootstrap=new ServerBootstrap();
            serverBootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    //针对subGroup做的子处理器，childHandler针对WebSokect的初始化器
                    .childHandler(new WebSocketinitializer());
            //绑定端口并以同步方式进行使用
            ChannelFuture channelFuture = serverBootstrap.bind(10086).sync();

            //针对channelFuture，进行相应的监听
            channelFuture.channel().closeFuture().sync();


        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

}
