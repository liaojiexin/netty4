package com.liaojiexin.netty23.c7;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * @ClassName EchoClient
 * @Description TODO
 * @Author liao
 * @Date 3:10 下午 2023/1/20
 **/
public class EchoClient {

    public static void main(String[] args) throws InterruptedException {
        new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override   //使用channelActive事件，它会在建立连接后触发
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                //建议使用ctx.alloc方法创建Bytebuf
                                ByteBuf buf=ctx.alloc().buffer(10);

                                //首次建立连接，发送信息
                                buf.writeBytes("hello".getBytes());
                                ctx.writeAndFlush(buf);
                            }

                            @Override   //接收服务器端发送过来的消息
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf=(ByteBuf) msg;
                                //打印结果:服务端已经接收到消息:hello
                                System.out.println(buf.toString(Charset.defaultCharset()));

                                //思考释放需要释放ByteBuf
                            }
                        });
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080));
    }
}
