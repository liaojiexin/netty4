package com.liaojiexin.netty23.c2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @ClassName HelloClient
 * @Description TODO    简单的helloworld案例，客户端代码
 * @Author liao
 * @Date 10:36 上午 2023/1/16
 **/
@Slf4j
public class EventLoopClient {
    public static void main(String[] args) throws InterruptedException {
        //1.启动类，启动客户端
        Channel channel = new Bootstrap()
                //2.添加EventLoop(其实这里可以不用添加，EventLoop内有选择器和多线程，选择器在服务器端的作用更加明显，这里其实就是一个线程去做发送信息的请求，用以前的nio方法去连接也可以)
                .group(new NioEventLoopGroup())
                //3.选择客户端channel实现
                .channel(NioSocketChannel.class)
                //4.添加处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override   //在连接建立后被调用，做一个初始化的操作
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder()); //编码，把字符串转化为ByteBuf
                    }
                })
                //5.连接服务器
                .connect(new InetSocketAddress("localhost", 8080))
                .sync()
                .channel();
        System.out.println(channel);
        //2.发送消息,这里加个断点然后用debug模式(注意debug的时候要吧断点类型设置为线程类型，如果为所有服务端那边也会停止住)
        //然后在debug启动这个main后，右键用评估表达式来进行多次发送(输入channel.writeAndFlush("hello world");即可发送信息)
        System.out.println();

        //也可以不用断点，使用下面语句直接发送
        channel.writeAndFlush("111");
        channel.writeAndFlush("222");
        /**
         * 发送后，服务器端那边的结果如下，可以看见不过发送几次，都是nioEventLoopGroup-2-2这个事件循环对象进行处理，
         * 也就验证了前面所说的，一旦某个eventloop负责某个channel，他就负责到底(绑定)，可以再起另一个客户端进行对比
         * 18:20:20.705 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c2.EventLoopServer - 111
         * 18:26:07.566 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c2.EventLoopServer - 111
         */

    }
}
