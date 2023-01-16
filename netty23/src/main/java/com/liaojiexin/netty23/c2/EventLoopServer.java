package com.liaojiexin.netty23.c2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * @ClassName EventLoopServer
 * @Description TODO    EventLoop处理io任务案例，服务端代码
 * @Author liao
 * @Date 4:12 下午 2023/1/16
 **/
@Slf4j
public class EventLoopServer {

    public static void main(String[] args) {
        new ServerBootstrap()   //启动器
                .group(new NioEventLoopGroup()) //事件循环组
                .channel(NioServerSocketChannel.class)  //选定channel
                .childHandler(
                    new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){   //自定义handler
                                @Override   //触发读事件，这里的msg没经过解码，所以是Bytebuf类型
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    ByteBuf buf=(ByteBuf) msg;
                                    //转字符串使用默认字符集Charset.defaultCharset()进行转化
                                    //注意这里实际开发过程中要自定义字符集，因为如果客户端和服务端是不同系统默认字符集可能会不一样就会乱码
                                    log.debug(buf.toString(Charset.defaultCharset()));
                                }
                            });
                        }
                    })
                .bind(8080);
    }
}
