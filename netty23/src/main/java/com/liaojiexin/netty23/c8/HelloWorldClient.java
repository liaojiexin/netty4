package com.liaojiexin.netty23.c8;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @ClassName HelloWorldClient
 * @Description TODO
 * @Author liao
 * @Date 4:43 下午 2023/1/20
 **/
public class HelloWorldClient {
    public static void main(String[] args) {
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
