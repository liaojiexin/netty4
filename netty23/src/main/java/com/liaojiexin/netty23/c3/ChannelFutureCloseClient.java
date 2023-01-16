package com.liaojiexin.netty23.c3;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * @ClassName ChannelFutureCloseClient
 * @Description TODO
 * @Author liao
 * @Date 11:06 下午 2023/1/16
 **/
@Slf4j
public class ChannelFutureCloseClient {

    //需求:在客户端控制台输入值，然后发送给服务端，输入的值为"q"则表示退出发送，然后关闭通道，但是想要在关闭channel通道后做一些善后工作。
    public static void main(String[] args) throws InterruptedException {
        ChannelFuture channelFuture=new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override   //在连接建立后被调用，做一个初始化的操作
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder()); //编码，把字符串转化为ByteBuf
                    }
                })
                .connect(new InetSocketAddress("localhost",8080));

        Channel channel=channelFuture.sync().channel();
        new Thread(()->{
            Scanner scanner=new Scanner(System.in);
            while (true){
                String line =scanner.nextLine();
                if (line.equals("q")){  //如果输入结果为q则退出
                    channel.close();    //关闭连接
                    //写在这里也是错误的，因为上面的channel.close()是一个异步操作，所以可能下面的操作已经执行了，上面的channel才关闭
                    //log.debug("处理关闭channel后的操作");
                    break;
                }
                channel.writeAndFlush(line);
            }
        },"input").start();
        //写在这里是错误的，因为这里是主线程，而上面的关闭操作是另一个线程，所以没办法在关闭channel以后才做处理
        //log.debug("处理关闭channel后的操作");
    }
}
