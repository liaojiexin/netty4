package com.liaojiexin.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in=(ByteBuf) msg;
        System.out.println("Server received:"+in.toString(CharsetUtil.UTF_8));  //将消息记录到控制台
        ctx.write(in);  //将接收到的消息写给发送者，而不刷新出站信息
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        Channel channel = new NioServerSocketChannel();
        ByteBuf buf =Unpooled.copiedBuffer("your data",CharsetUtil.UTF_8);
        ChannelFuture cf =channel.writeAndFlush(buf);
        cf.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    System.out.println("Write successful");
                } else {
                    System.err.println("Write error");
                    channelFuture.cause().printStackTrace();
                }
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();    //打印异常栈跟踪
        ctx.close();    //关闭该Channel
    }
}
