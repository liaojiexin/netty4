package com.liaojiexin.netty23.c8;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @ClassName LineBasedFrameDecoderServer
 * @Description TODO
 * @Author liao
 * @Date 10:03 下午 2023/1/20
 **/
public class LineBasedFrameDecoderServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss=new NioEventLoopGroup();
        NioEventLoopGroup worker=new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap=new ServerBootstrap();
            //调整系统的接收缓冲器(滑动窗口)，一般不用设置，现在的系统很智能会根据发送方和接收方环境定一个合适的值
            //serverBootstrap.option(ChannelOption.SO_RCVBUF,2);  //设置接收缓冲区，为了让半包想象更加明显
            //调整netty的接收缓冲区(bytebuf),这里最小只能为16(16的整数)
            serverBootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR,new AdaptiveRecvByteBufAllocator(16,16,16)); //效果同上，设置接收缓冲区，为了让半包想象更加明显
            serverBootstrap.group(boss,worker).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override   //初始化
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            //添加定行解码器，会根据换行符切分消息。【注意解码器要放到下面打印日志前面，要先解码才能打印】
                            ch.pipeline().addLast(new LineBasedFrameDecoder(20));
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));  //打印出服务器端收到的信息
                        }
                    });

            //做channel关闭操作
            ChannelFuture channelFuture=serverBootstrap.bind(8080).sync();
            channelFuture.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
