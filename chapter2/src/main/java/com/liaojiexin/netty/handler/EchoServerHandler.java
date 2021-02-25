package com.liaojiexin.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @ClassName: EchoServerHandler
 * @Description: TODO
 * @version: 1.0 实现ChannelInboundHandler接口，用来定义响应入站事件的方法，简单的应用程序只需要用到少量的这些方法，
 * 所以继承ChannelInboundHandlerAdapter也足够
 * @author: liaojiexin
 * @date: 2021/2/24 17:18
 */
@ChannelHandler.Sharable    //标示一个ChannelHandler可以被多个Channel安全地共享
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    /**
     *channelRead()和channelReadComplete() 方法的区别是什么？
     * 答案：https://segmentfault.com/q/1010000018753423
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in=(ByteBuf) msg;
        System.out.println("Server received:"+in.toString(CharsetUtil.UTF_8));  //将消息记录到控制台
        ctx.write(in);  //将接收到的消息写给发送者，而不刷新出站信息
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);  //将挂起的信息刷新到远程节点，并且关闭该Channel
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();    //打印异常栈跟踪
        ctx.close();    //关闭该Channel
    }
}
