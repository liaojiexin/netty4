package com.liaojiexin.netty23.c8;

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

import java.util.Random;

/**
 * @ClassName LineBasedFrameDecoderClient
 * @Description TODO
 * @Author liao
 * @Date 9:58 下午 2023/1/20
 **/
public class LineBasedFrameDecoderClient {

    public static void main(String[] args) {
//        makeString('1',10);
        send();
    }

    public static StringBuilder makeString(char c,int len){
        StringBuilder stringBuilder=new StringBuilder();
        for (int i=0;i<len;i++){
            stringBuilder.append(c);
        }
        stringBuilder.append("\n"); //添加换行符
        return stringBuilder;
    }

    private static void send(){
        NioEventLoopGroup worker=new NioEventLoopGroup();
        try{
            Bootstrap bootstrap=new Bootstrap();
            bootstrap.group(worker).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));//打印出debug信息
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                @Override   //首次连接成功就会触发事件
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ByteBuf buf=ctx.alloc().buffer();
                                    char c='0';
                                    Random random=new Random();
                                    for (int i=0;i<10;i++){ //循环10次随机生成长度10以内的字节数组
                                        StringBuilder stringBuilder= makeString(c, random.nextInt(10) + 1);
                                        c++;
                                        buf.writeBytes(stringBuilder.toString().getBytes());
                                    }
                                    ctx.writeAndFlush(buf);
                                    /**
                                     * 22:04:17.205 [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x717c315f, L:/127.0.0.1:52130 - R:localhost/127.0.0.1:8080] WRITE: 84B
                                     *          +-------------------------------------------------+
                                     *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
                                     * +--------+-------------------------------------------------+----------------+
                                     * |00000000| 30 30 30 30 30 30 30 0a 31 31 31 31 31 31 31 31 |0000000.11111111|
                                     * |00000010| 31 31 0a 32 32 0a 33 33 33 33 33 33 33 33 33 0a |11.22.333333333.|
                                     * |00000020| 34 34 0a 35 35 35 35 35 35 35 35 35 0a 36 36 36 |44.555555555.666|
                                     * |00000030| 36 36 36 36 36 36 36 0a 37 37 37 37 37 0a 38 38 |6666666.77777.88|
                                     * |00000040| 38 38 38 38 38 38 38 38 0a 39 39 39 39 39 39 39 |88888888.9999999|
                                     * |00000050| 39 39 39 0a                                     |999.            |
                                     * +--------+-------------------------------------------------+----------------+
                                     */
                                }
                            });
                        }
                    });
            ChannelFuture channelFuture=bootstrap.connect("localhost",8080).sync();
            channelFuture.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            worker.shutdownGracefully();
        }
    }
}
