package com.liaojiexin.netty23.c9;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.nio.charset.Charset;

/**
 * @ClassName RedisClient
 * @Description TODO redis客户端，按照redis协议给redis服务器端发送消息
 * @Author liao
 * @Date 10:18 上午 2023/1/21
 **/
public class RedisClient {

    /**
     * 首先要说明一下redis协议，如下(每个一行到下一行要加上回车换行\r\n)
     * set name zhangsan
     * *3   表示有命令有多少个元素
     * $3   表示下面的元素长度
     * set
     * $4
     * name
     * $8
     * zhangsan
     *
     * @param args
     */
    public static void main(String[] args) {
        final byte[] LINE=new byte[]{'\r','\n'};
        NioEventLoopGroup work=new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap().group(work).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                @Override   //首次连接就操作事件,向redis服务器端发送
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ByteBuf buf=ctx.alloc().buffer();
                                    buf.writeBytes("*3".getBytes());
                                    buf.writeBytes(LINE);
                                    buf.writeBytes("$3".getBytes());
                                    buf.writeBytes(LINE);
                                    buf.writeBytes("set".getBytes());
                                    buf.writeBytes(LINE);
                                    buf.writeBytes("$4".getBytes());
                                    buf.writeBytes(LINE);
                                    buf.writeBytes("name".getBytes());
                                    buf.writeBytes(LINE);
                                    buf.writeBytes("$8".getBytes());
                                    buf.writeBytes(LINE);
                                    buf.writeBytes("zhangsan".getBytes());
                                    buf.writeBytes(LINE);
                                    ctx.writeAndFlush(buf);
                                }

                                @Override   //获取redis服务器返回的结果
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    ByteBuf buf=(ByteBuf) msg;
                                    System.out.println(buf.toString(Charset.defaultCharset()));
                                }
                            });
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect("localhost", 6379).sync();
            channelFuture.channel().closeFuture().sync();   //关闭channel
        }catch (Exception exception){
            exception.printStackTrace();
        }finally {
            work.shutdownGracefully();
        }
    }

    /**打印结果如下，另外本地redis上面也多了一个key为name，value为zhangsan的值
     * 10:37:17.884 [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x37c7e011, L:/127.0.0.1:55231 - R:localhost/127.0.0.1:6379] WRITE: 37B
     *          +-------------------------------------------------+
     *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
     * +--------+-------------------------------------------------+----------------+
     * |00000000| 2a 33 0d 0a 24 33 0d 0a 73 65 74 0d 0a 24 34 0d |*3..$3..set..$4.|
     * |00000010| 0a 6e 61 6d 65 0d 0a 24 38 0d 0a 7a 68 61 6e 67 |.name..$8..zhang|
     * |00000020| 73 61 6e 0d 0a                                  |san..           |
     * +--------+-------------------------------------------------+----------------+
     * 10:37:17.884 [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x37c7e011, L:/127.0.0.1:55231 - R:localhost/127.0.0.1:6379] FLUSH
     * 10:37:17.890 [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x37c7e011, L:/127.0.0.1:55231 - R:localhost/127.0.0.1:6379] READ: 5B
     *          +-------------------------------------------------+
     *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
     * +--------+-------------------------------------------------+----------------+
     * |00000000| 2b 4f 4b 0d 0a                                  |+OK..           |
     * +--------+-------------------------------------------------+----------------+
     * +OK
     *
     * 10:37:17.890 [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x37c7e011, L:/127.0.0.1:55231 - R:localhost/127.0.0.1:6379] READ COMPLETE
     */

}
