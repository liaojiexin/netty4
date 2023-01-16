package com.liaojiexin.netty23.c1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

/**
 * @ClassName HelloClient
 * @Description TODO    简单的helloworld案例，客户端代码
 * @Author liao
 * @Date 10:36 上午 2023/1/16
 **/
public class HelloClient{
    public static void main(String[] args) throws InterruptedException {
        //1.启动类，启动客户端
        new Bootstrap()
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
                .connect(new InetSocketAddress("localhost",8080))
                .sync()
                .channel()
                //6.向服务器发送数据
                .writeAndFlush("hello world");
    }
}
