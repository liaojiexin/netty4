package com.liaojiexin.netty23.c9;

import com.liaojiexin.netty23.c9.protocol.MessageCodec;
import com.liaojiexin.netty23.c9.protocol.MessageCodecSharable;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @ClassName TestSharable
 * @Description TODO @Sharable注解讲解
 * @Author liao
 * @Date 5:30 下午 2023/1/21
 **/
public class TestSharable {
    public static void main(String[] args) {
        NioEventLoopGroup boss=new NioEventLoopGroup();
        NioEventLoopGroup worker=new NioEventLoopGroup();
        //这里把线程安全的(无状态的)handler提取出来，这样多个EventLoop就可以复用
        LoggingHandler LOGGER_HANDLER=new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC=new MessageCodecSharable();
        try{
            ServerBootstrap serverBootstrap=new ServerBootstrap()
                    .channel(NioServerSocketChannel.class)
                    .group(boss,worker)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            /**
                             * 这里的LengthFieldBasedFrameDecoder解码器就是线程不安全的，不能能被多个EventLoop做线程复用
                             * 原因如下：EventLoop1和EventLoop2两个事件循环对象都要处理消息12345678
                             * 例如现在EventLoop1传递了第一部分bytebuf:1234
                             * EvnetLoop2传递了第一部分的bytebuf:1234
                             * 这个时候因为LengthFieldBasedFrameDecoder是有状态的，它一开始先记录了EventLoop1传递的1234
                             * 然后EventLoop1的第二部分还未来得及发送，EventLoop2的第一部分就发送了，这样这个解码器就拿了两个
                             * EventLoop的第一部分的bytebuf组合了起来，这样的结果就是错误的
                             */
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024,12,4,0,0));
                            ch.pipeline().addLast(LOGGER_HANDLER);
                            ch.pipeline().addLast(MESSAGE_CODEC);
                        }
                    });
            ChannelFuture channelFuture=serverBootstrap.bind(8080).sync();
            channelFuture.channel().closeFuture().sync();
        }catch (Exception exception){
            exception.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
