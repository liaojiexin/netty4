package com.liaojiexin.netty23.c10.client;

import com.liaojiexin.netty23.c10.message.*;
import com.liaojiexin.netty23.c10.protocol.MessageCodecSharable;
import com.liaojiexin.netty23.c10.protocol.ProcotolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        //添加信号量，做为登录线程的阻塞
        CountDownLatch WAIT_FOR_LOGIN=new CountDownLatch(1);
        AtomicBoolean LOGIN=new AtomicBoolean(false);   //登录是否成功标记
        AtomicBoolean EXIT = new AtomicBoolean(false);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
//                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);

                    // 用来判断是不是 读空闲时间过长，或 写空闲时间过长
                    // 3s 内如果没有向服务器写数据，会触发一个 IdleState#WRITER_IDLE 事件
                    ch.pipeline().addLast(new IdleStateHandler(0, 3, 0));
                    // ChannelDuplexHandler 可以同时作为入站和出站处理器
                    ch.pipeline().addLast(new ChannelDuplexHandler() {
                        // 用来触发特殊事件
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
                            IdleStateEvent event = (IdleStateEvent) evt;
                            // 触发了写空闲事件
                            if (event.state() == IdleState.WRITER_IDLE) {
                                log.debug("3s 没有写数据了，发送一个心跳包");
                                ctx.writeAndFlush(new PingMessage());
                            }
                        }
                    });

                    //入站处理
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                        @Override   //获取登录后的信息
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.debug("登录结果:{}",msg);
                            if (msg instanceof LoginResponseMessage){
                                LoginResponseMessage loginResponseMessage= (LoginResponseMessage) msg;
                                if (loginResponseMessage.isSuccess()){  //如果登录成功
                                    LOGIN.set(true);
                                }
                                //唤醒登录功能的线程
                                WAIT_FOR_LOGIN.countDown();
                            }
                        }

                        @Override   //首次与服务器端建立连接成功后操作事件，处理登录功能
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            //登录处理，这里要新建一个线程专门来做登录逻辑，因为登录是阻塞操作，如果不自己弄一个线程
                            //用的就是NioEvnetLoopGroup提供的线程，一旦有多个用户(数量大于EventLoopGroup可以提供的线程数)同时做登录，就会对其他事件造成影响
                            new Thread(()->{
                                Scanner scanner=new Scanner(System.in);
                                System.out.println("请输入用户名:");
                                String username = scanner.nextLine();
                                if(EXIT.get()){
                                    return;
                                }
                                System.out.println("请输入密码:");
                                String password = scanner.nextLine();
                                if(EXIT.get()){
                                    return;
                                }
                                //构造消息对象
                                LoginRequestMessage loginRequestMessage=new LoginRequestMessage(username,password);
                                ctx.writeAndFlush(loginRequestMessage);

                                System.out.println("等待后续操作...");
                                try {
                                    WAIT_FOR_LOGIN.await(); //这里阻塞直到登录结果返回
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (!LOGIN.get()){  //登录失败
                                    log.debug("登录失败");
                                    ctx.channel().close();  //关闭channel
                                    return;
                                }
                                while (true){   //如果登录成功
                                    System.out.println("==================================");
                                    System.out.println("send [username] [content]");
                                    System.out.println("gsend [group name] [content]");
                                    System.out.println("gcreate [group name] [m1,m2,m3...]");
                                    System.out.println("gmembers [group name]");
                                    System.out.println("gjoin [group name]");
                                    System.out.println("gquit [group name]");
                                    System.out.println("quit");
                                    System.out.println("==================================");
                                    System.out.println("请输入操作命令:");
                                    String command=scanner.nextLine();
                                    String[] s = command.split(" ");
                                    switch (s[0]){
                                        case "send":    //发送消息
                                            ctx.writeAndFlush(new ChatRequestMessage(username, s[1], s[2]));
                                            break;
                                        case "gsend":   //发送聊天室消息
                                            ctx.writeAndFlush(new GroupChatRequestMessage(username, s[1], s[2]));
                                            break;
                                        case "gcreate": //创建聊天室
                                            Set<String> set = new HashSet<>(Arrays.asList(s[2].split(",")));
                                            set.add(username); // 加入自己
                                            ctx.writeAndFlush(new GroupCreateRequestMessage(s[1], set));
                                            break;
                                        case "gmembers":    //获取到聊天室成员
                                            ctx.writeAndFlush(new GroupMembersRequestMessage(s[1]));
                                            break;
                                        case "gjoin":   //加入聊天室
                                            ctx.writeAndFlush(new GroupJoinRequestMessage(username, s[1]));
                                            break;
                                        case "gquit":   //退出聊天室
                                            ctx.writeAndFlush(new GroupQuitRequestMessage(username, s[1]));
                                            break;
                                        case "quit":    //断开聊天
                                            ctx.channel().close();
                                            return;
                                    }
                                }
                            },"System.in").start();
                        }

                        // 在连接断开时触发
                        @Override
                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                            log.debug("连接已经断开，按任意键退出..");
                            EXIT.set(true);
                        }

                        // 在出现异常时触发
                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            log.debug("连接已经断开，按任意键退出..{}", cause.getMessage());
                            EXIT.set(true);
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
