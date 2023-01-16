package com.liaojiexin.netty23.c3;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @ClassName ChannelClient
 * @Description TODO
 * @Author liao
 * @Date 10:08 下午 2023/1/16
 **/
@Slf4j
public class ChannelFutureClient {
    public static void main(String[] args) throws InterruptedException {
        //2.带有Future、Promise的类型都是和异步方法配套使用的，用来处理结果
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override   //在连接建立后被调用，做一个初始化的操作
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder()); //编码，把字符串转化为ByteBuf
                    }
                })
                //1.连接服务器
                //这里是一个异步非阻塞的方法，虽然由main主线程调用，但是底层真正执行连接操作的是用NioEventLoop的nio线程去操作
                .connect(new InetSocketAddress("localhost", 8080));

        /**
         *下面如果不执行sync()方法同步等待channel的话，虽然运行没有报错，但是下面的消息发送不出去
         *这是因为这里不加sync的话就会没有阻塞到主线程，会直接向下执行下去，
         *但是上面的connect方法又是一个异步阻塞方法，连接也是需要时间的，可能nio线程还没连接上，
         * 这样下面的channel拿到的是还未建立连接的对象
         */
        //2.1 使用sync方法来同步等待处理结果
        /*channelFuture.sync();   //阻塞住当前线程(主线程)，直到上面connect方法调用的nio线程建立连接完毕
        Channel channel = channelFuture.channel();
        log.debug("{}",channel);
        *//**
         * 如果channel建立连接的话上面的日志会打印出chaneel里面的连接信息，
         * 如下：22:32:40.944 [main] DEBUG com.liaojiexin.netty23.c3.ChannelClient - [id: 0x2055e510, L:/127.0.0.1:53975 - R:localhost/127.0.0.1:8080]
         * 但是如果把channelFuture.sync();注释掉的话，就没有看到连接信息
         * 如下：22:33:21.805 [main] DEBUG com.liaojiexin.netty23.c3.ChannelClient - [id: 0xd299f650]
         *//*
        channel.writeAndFlush("hello world");*/

        //2.2 使用 addListener(回调对象)方法异步处理方法，【注意这里和上面的2.1选择其中一种方法即可处理结果，而不是都要写】
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            //在nio线程连接建立好后，会调用operationComplete
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                Channel channel = channelFuture.channel();
                log.debug("{}",channel);
                //日志打印结果如下，可以看到线程不是主线程了，而是nioEventLoopGroup-2-1，同时后面也带上连接信息[id: 0x05bf079f, L:/127.0.0.1:57122 - R:localhost/127.0.0.1:8080]
                // 22:50:37.908 [nioEventLoopGroup-2-1] DEBUG com.liaojiexin.netty23.c3.ChannelClient - [id: 0x05bf079f, L:/127.0.0.1:57122 - R:localhost/127.0.0.1:8080]
                channel.writeAndFlush("hello world");
            }
        });

    }
}
