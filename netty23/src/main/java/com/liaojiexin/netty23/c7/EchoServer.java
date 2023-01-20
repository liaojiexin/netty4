package com.liaojiexin.netty23.c7;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.Charset;

/**
 * @ClassName EchoServer
 * @Description TODO 双向通信服务端
 * @Author liao
 * @Date 2:43 下午 2023/1/20
 **/
public class EchoServer {
    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf=(ByteBuf) msg;
                                System.out.println(buf.toString(Charset.defaultCharset()));//打印出信息，使用默认字符编码
                                //思考是否需要释放Bytebuf

                                //如何回应客户端，代码如下：
                                //建议使用ctx.alloc()来创建Bytebuf
                                ByteBuf response=ctx.alloc().buffer();
                                response.writeBytes(("服务端已经接收到消息:"+buf.toString(Charset.defaultCharset())).getBytes());
                                ctx.writeAndFlush(response);
                                //思考是否需要释放Bytebuf
                            }
                        });
                    }
                })
                .bind(8080);
    }
}
