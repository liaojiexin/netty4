package com.liaojiexin.netty23.c10.server.handler;

import com.liaojiexin.netty23.c10.message.LoginRequestMessage;
import com.liaojiexin.netty23.c10.message.LoginResponseMessage;
import com.liaojiexin.netty23.c10.server.serivce.UserServiceFactory;
import com.liaojiexin.netty23.c10.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @ClassName LoginRequestMessageHandler
 * @Description TODO //这里用SimpleChannelInboundHandler专门来处理LoginRequestMessage
 * @Author liao
 * @Date 9:21 下午 2023/1/22
 **/
@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override   //处理登录操作
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String username = msg.getUsername();
        String password = msg.getPassword();
        boolean login = UserServiceFactory.getUserService().login(username, password);
        LoginResponseMessage loginResponseMessage;
        if (login) {
            SessionFactory.getSession().bind(ctx.channel(), username);  //把登录后的用户存储起来
            loginResponseMessage = new LoginResponseMessage(true, "登录成功");
        } else {
            loginResponseMessage = new LoginResponseMessage(false, "登录失败，用户名或密码错误");
        }
        ctx.writeAndFlush(loginResponseMessage);    //把消息传送出站，反向走->MESSAGE_CODEC->LOGGING_HANDLER
    }
}
