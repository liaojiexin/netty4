package com.liaojiexin.netty23.c3;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
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
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture=new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override   //在连接建立后被调用，做一个初始化的操作
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));  //netty提供的打印日志，加入这一行代码就会打印出提供好的日志
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


        /**
         * 在上面加入ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));  //netty提供的打印日志，加入这一行代码就会打印出提供好的日志
         * 然后在客户端输入q，这样就会关闭channel，然后打印出下面的内容，可以看到先执行了处理关闭channel后的操作，再关闭了channel，而且两个操作是
         * 不一样的线程做处理的，所以写在new Thread里面的log.debug("处理关闭channel后的操作");也是不对的。
         *
         * 10:59:30.813 [input] DEBUG com.liaojiexin.netty23.c3.ChannelFutureCloseClient - 处理关闭channel后的操作
         * 10:59:30.815 [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x9a660589, L:/127.0.0.1:51661 - R:localhost/127.0.0.1:8080] CLOSE
         */

        //正确处理关闭后操作
        //利用channel获取CloseFuture对象，这个对象的作用就是来处理关闭后的操作。有两种方式:1.同步处理关闭；2.异步处理关闭
        ChannelFuture closeFuture=channel.closeFuture();

        //1.同步处理关闭,即当前线程(主线程)处理关闭后的操作
        /*log.debug("等待关闭中....");
        closeFuture.sync(); //这里主线程会阻塞住直到，channel关闭
        log.debug("处理关闭channel后的操作");*/


        //2.异步处理关闭,即让关闭channel的线程去处理关闭后的操作
        closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                log.debug("处理关闭channel后的操作");
                /**
                 * 如果不加入下面的EventLoopGroup关闭线程的代码，会发现，即使关闭了channel，这个程序还在运行。
                 * 这是因为EventLoopGroup(事件循环组)里面还有其他线程在运行
                 */
                group.shutdownGracefully(); //优雅的关闭EventLoopGroup里面的线程(优雅的意思就是会把没发送的信息发送掉等等一些操作后)
                //group.shutdown(); //强制关闭EventLoopGroup里面的线程，可能会造成数据丢失等问题。
            }
        });

    }
}
