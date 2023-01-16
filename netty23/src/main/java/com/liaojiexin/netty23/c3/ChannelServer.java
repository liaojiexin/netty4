package com.liaojiexin.netty23.c3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * @ClassName ChannelServer
 * @Description TODO
 * @Author liao
 * @Date 10:08 下午 2023/1/16
 **/
@Slf4j
public class ChannelServer {
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
