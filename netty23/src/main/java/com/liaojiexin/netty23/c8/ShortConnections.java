package com.liaojiexin.netty23.c8;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @ClassName ShortConnections
 * @Description TODO 短连接处理黏包问题，客户端服务代码，服务器端用HelloWorldServer类
 * @Author liao
 * @Date 6:28 下午 2023/1/20
 **/
public class ShortConnections {
    public static void main(String[] args) {
        for (int i=0;i<3;i++){
            send();
        }
        System.out.println("发送完成");
    }

    /**
     * 这里就不会产生黏包现象，服务器端分开获取到信息
     * 18:33:23.394 [nioEventLoopGroup-3-6] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x119e8a1a, L:/127.0.0.1:8080 - R:/127.0.0.1:59288] READ: 17B
     *          +-------------------------------------------------+
     *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
     * +--------+-------------------------------------------------+----------------+
     * |00000000| 01 02 03 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f 10 |................|
     * |00000010| 11                                              |.               |
     * +--------+-------------------------------------------------+----------------+
     */

    public static void send(){
        NioEventLoopGroup eventExecutors=new NioEventLoopGroup();
        try{
            Bootstrap bootstrap=new Bootstrap();
            bootstrap.group(new NioEventLoopGroup()).channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                        @Override   //首次建立连接成功就会做操作
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            for (int i=0;i<3;i++){  //连续发送5次信息给服务器端
                                ByteBuf buf=ctx.alloc().buffer(17);
                                buf.writeBytes(new byte[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17});
                                ctx.writeAndFlush(buf);
                                ctx.channel().close();//每发送一次就关闭channel，服务器端获取到-1就表示获取到一个完整的信息了
                            }
                        }
                    });
                }
            });
            ChannelFuture channelFuture = bootstrap.connect("localhost", 8080).sync();
            channelFuture.channel().closeFuture().sync();
        }catch (Exception exception){
            exception.printStackTrace();
        }finally {
            eventExecutors.shutdownGracefully();
        }
    }
}
