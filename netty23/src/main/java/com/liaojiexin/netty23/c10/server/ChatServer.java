package com.liaojiexin.netty23.c10.server;

import com.liaojiexin.netty23.c10.message.LoginRequestMessage;
import com.liaojiexin.netty23.c10.message.LoginResponseMessage;
import com.liaojiexin.netty23.c10.protocol.MessageCodecSharable;
import com.liaojiexin.netty23.c10.protocol.ProcotolFrameDecoder;
import com.liaojiexin.netty23.c10.server.serivce.UserServiceFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    //这里用SimpleChannelInboundHandler专门来处理LoginRequestMessage
                    ch.pipeline().addLast(new SimpleChannelInboundHandler<LoginRequestMessage>(){
                        @Override   //处理登录操作
                        protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
                            String username=msg.getUsername();
                            String password=msg.getPassword();
                            boolean login = UserServiceFactory.getUserService().login(username, password);
                            LoginResponseMessage loginResponseMessage;
                            if (login){
                                loginResponseMessage=new LoginResponseMessage(true,"登录成功");
                            }else{
                                loginResponseMessage=new LoginResponseMessage(false,"登录失败，用户名或密码错误");
                            }
                            ctx.writeAndFlush(loginResponseMessage);    //把消息传送出站，反向走->MESSAGE_CODEC->LOGGING_HANDLER
                        }
                    });
                }
            });
            Channel channel = serverBootstrap.bind(8080).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
