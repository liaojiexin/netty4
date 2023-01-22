package com.liaojiexin.netty23.c10.server.handler;

import com.liaojiexin.netty23.c10.message.ChatRequestMessage;
import com.liaojiexin.netty23.c10.message.ChatResponseMessage;
import com.liaojiexin.netty23.c10.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @ClassName ChatRequestMessageHandler
 * @Description TODO    处理发送单条信息
 * @Author liao
 * @Date 9:24 下午 2023/1/22
 **/
@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String from = msg.getFrom();    //发送人
        String to = msg.getTo();    //接受人
        String content = msg.getContent();  //内容
        Channel channel = SessionFactory.getSession().getChannel(to);
        if (channel!=null){ //获取到接收人的channel表示当前接受人在线
            channel.writeAndFlush(new ChatResponseMessage(from,content));
        }else { //不在线
            ctx.writeAndFlush(new ChatResponseMessage(false,"当前用户不在线"));
        }
    }
}
