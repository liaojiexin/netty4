package com.liaojiexin.netty23.c1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @ClassName HelloServer
 * @Description TODO 简单的helloworld案例，服务端代码
 * @Author liao
 * @Date 9:42 上午 2023/1/16
 **/
public class HelloServer {

    //这里需要先学习Nio，netty用的就是nio的思想，这里分为boss和worker,其中boss负责监听是否有连接进来(监听事件)，worker负责处理读写处理(读写事件)
    public static void main(String[] args) {
        //1.启动器，负责组装netty组件，启动服务器
        new ServerBootstrap()
                //2.类似java nio中多线程BossEventLoop、WorkEventLoop,里面包含了selector选择器和thread线程，一个线程配合一个选择器，来判断事件的发生
                .group(new NioEventLoopGroup())
                //3.选择服务器的ServerSocketChannel实现
                .channel(NioServerSocketChannel.class)  //这里可以选择Nio Bio 等
                //4.做事件处理的一些分工合作，告诉worker(child)要做什么事情（读写事件），决定了worker(child)能执行哪些操作(handler)
                .childHandler(
                    //5.channel代表和客户端进行数据读写的通道 Initializer初始化，他本身也是一个特殊的handler，他的职责是添加别的handler
                    new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            //6.添加具体的handler
                            ch.pipeline().addLast(new StringDecoder()); //解码，将传输过来的ByteBuf转换为字符串
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){   //自定义handler
                                @Override   //触发读事件
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    System.out.println(msg);    //打印上一步转换好的字符串
                                }
                            });
                        }
                    })
                //7.绑定监听的端口
                .bind(8080);
    }
}
