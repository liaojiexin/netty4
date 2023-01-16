package com.liaojiexin.netty23.c2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
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
        /**
         *EventLoop分工细化2,假设某个Channel的handler要处理的过程比较复杂耗时很长，
         *这个时候负责处理这个Channel的EventLoop就会一直在处理这个Channel，
         * 而其他和这个EventLoop绑定的Channel刚好要处理事件的时候就会一直被阻塞直EventLoop处理完上一个Channel的事件
         *
         * 解决方法:我们专门弄一个EventLoop来处理这种耗时很长的事件，这样就可以防止影响到其他Channel的事件处理
         */
        EventLoopGroup group=new DefaultEventLoop();    //这里也可以用NioEventLoopGroup

        new ServerBootstrap()   //启动器
//                .group(new NioEventLoopGroup()) //事件循环组
                //EventLoop分工细化1。设定成两个EventLoopGroup，
                // 第一个EventLoopGroup为boss，负责ServerSocketChannel上的accept事件
                // (这里NioEventLoopGroup构造函数参数可以给1，表示只有1个线程，但是也可以用不用因为下面的channel(NioServerSocketChannel.class)表示只有一个Channel。所以EventLoop也只会创建一个线程)
                // 第二个EventLoopGroup为worker,负责SockerChannel上的Read/Write事件，这里为了演示设定2个线程数
                .group(new NioEventLoopGroup(),new NioEventLoopGroup(2)) //事件循环组
                .channel(NioServerSocketChannel.class)  //选定channel
                .childHandler(
                    new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                "handle-1", //这里自定义了handle的名称为了和下面的另一个handler区分出来
                                new ChannelInboundHandlerAdapter(){   //自定义handler
                                    @Override   //触发读事件，这里的msg没经过解码，所以是Bytebuf类型
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        ByteBuf buf=(ByteBuf) msg;
                                        //转字符串使用默认字符集Charset.defaultCharset()进行转化
                                        //注意这里实际开发过程中要自定义字符集，因为如果客户端和服务端是不同系统默认字符集可能会不一样就会乱码
                                        log.debug(buf.toString(Charset.defaultCharset()));
                                        ctx.fireChannelRead(msg);   //将消息传递给下一个handler处理,即handler-2
                                    }
                            }).addLast(
                                /**
                                 * 1、这里用addLast(EventExecutorGroup group, String name, ChannelHandler handler);
                                 *      这个方法来指定用那个EventLoopGroup来处理，第二参数设定handler名称
                                 * 2、这里加了两个handler是为了后面演示后来区分出区别，然后注意这里加的第二个handler之间要有联系，
                                 *      而不是用ch.pipeline后面再加一个新的handler，而且上一个handler里面要加上ctx.fireChannelRead(msg);这样才能传递下来
                                 */
                                group,"handler-2",  //自定义EventLoopGroup和handler名称
                                new ChannelInboundHandlerAdapter(){   //自定义handler
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
