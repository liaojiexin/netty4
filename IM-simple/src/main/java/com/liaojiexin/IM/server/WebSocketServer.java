package com.liaojiexin.IM.server;

import com.liaojiexin.IM.handler.ChatHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @ClassName: WebSocketServer
 * @Description: TODO   https://blog.csdn.net/qq_26975307/article/details/85051833?spm=1001.2014.3001.5502
 * @version: 1.0
 * @author: liaojiexin
 * @date: 2021/3/16 13:45
 */
public class WebSocketServer {

    public static void main(String[] args) throws Exception{
        EventLoopGroup bossGroup =new NioEventLoopGroup();
        EventLoopGroup workGroup =new NioEventLoopGroup();

        try{
            ServerBootstrap serverBootstrap=new ServerBootstrap();
            serverBootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //从Channel中获取对应的pipeline
                            ChannelPipeline channelPipeline = ch.pipeline();
                            //添加相应的助手类与处理器
                            /**
                             * WebSokect基于Http，所以要有相应的Http编解码器，HttpServerCodec()
                             */
                            channelPipeline.addLast(new HttpServerCodec());

                            //在Http中有一些数据流的传输，那么数据流有大有小，如果说有一些相应的大数据流处理的话，需要在此添加
                            //ChunkedWriteHandler：为一些大数据流添加支持
                            channelPipeline.addLast(new ChunkedWriteHandler());

                            //UdineHttpMessage进行处理，也就是会用到request以及response
                            //HttpObjectAggregator：聚合器，聚合了FullHTTPRequest、FullHTTPResponse。。。，当你不想去管一些HttpMessage的时候，直接把这个handler丢到管道中，让Netty自行处理即可
                            channelPipeline.addLast(new HttpObjectAggregator(2048*64));

                            //================华丽的分割线：以上是用于支持Http协议================
                            //================华丽的分割线：以下是用于支持WebSoket==================

                            // /ws：一开始建立连接的时候会使用到，可自定义
                            //WebSocketServerProtocolHandler：给客户端指定访问的路由（/ws），是服务器端处理的协议，当前的处理器处理一些繁重的复杂的东西，运行在一个WebSocket服务端
                            //另外也会管理一些握手的动作：handshaking(close，ping，pong) ping + pong = 心跳，对于WebSocket来讲，是以frames进行传输的，不同的数据类型对应的frames也不同
                            channelPipeline.addLast(new WebSocketServerProtocolHandler("/ws"));

                            //添加自动handler，读取客户端消息并进行处理，处理完毕之后将相应信息传输给对应客户端
                            channelPipeline.addLast(new ChatHandler());
                        }
                    });
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
