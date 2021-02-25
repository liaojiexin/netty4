package com.liaojiexin.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * @ClassName: EchoClientHandler
 * @Description: TODO
 * @version: 1.0
 * @author: liaojiexin
 * @date: 2021/2/25 9:20
 */
@ChannelHandler.Sharable //标记该类的实例可以被多个Channel共享
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {     //channelActive()方法将在一个连接建立时被调用。
        //当被通知Channel是活跃的时候，发送一条消息
        ctx.writeAndFlush(Unpooled.copiedBuffer("Nett rocks!", CharsetUtil.UTF_8));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf in) throws Exception {   //channelRead0()方法当客户端接收到数据时会调用这个方法。*服务器发送的消息可能会被分块接收
        //记录已接收消息的转储
        System.out.println(
                "Client received: " + in.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {  //异常拦截
        //记录 Throwable，关闭 Channel
        cause.printStackTrace();
        ctx.close();
    }
}
