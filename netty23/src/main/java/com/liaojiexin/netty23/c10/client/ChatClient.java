package com.liaojiexin.netty23.c10.client;

import com.liaojiexin.netty23.c10.message.LoginRequestMessage;
import com.liaojiexin.netty23.c10.protocol.MessageCodecSharable;
import com.liaojiexin.netty23.c10.protocol.ProcotolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Scanner;

@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    //入站处理
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                        @Override   //获取登录后的信息
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.debug("登录结果:{}",msg);
                        }

                        @Override   //首次与服务器端建立连接成功后操作事件，处理登录功能
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            //登录处理，这里要新建一个线程专门来做登录逻辑，因为登录是阻塞操作，如果不自己弄一个线程
                            //用的就是NioEvnetLoopGroup提供的线程，一旦有多个用户(数量大于EventLoopGroup可以提供的线程数)同时做登录，就会对其他事件造成影响
                            new Thread(()->{
                                Scanner scanner=new Scanner(System.in);
                                System.out.println("请输入用户名:");
                                String username = scanner.nextLine();
                                System.out.println("请输入密码:");
                                String password = scanner.nextLine();
                                //构造消息对象
                                LoginRequestMessage loginRequestMessage=new LoginRequestMessage(username,password);
                                ctx.writeAndFlush(loginRequestMessage);

                                System.out.println("等待后续操作...");
                                try {
                                    System.in.read();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            },"System.in").start();
                        }
                    });
                }
            });
            Channel channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
