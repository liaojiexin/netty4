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
 * @ClassName FixedLengthFrameDecoderClient
 * @Description TODO
 * @Author liao
 * @Date 8:26 下午 2023/1/20
 **/
public class FixedLengthFrameDecoderClient {

    public static void main(String[] args) {
//        fill10Bytes('1',5);
        send();
    }

    /**
     * 随机字节生成长度10以内的字节组
     * @param c 选定的字节
     * @param len   长度
     * @return
     */
    public static byte[] fill10Bytes(char c,int len){
        StringBuilder result = new StringBuilder();
        byte[] bytes = new byte[len];
        for (int i=0;i<10;i++){
            if (i<len){
                result.append(c);
            }else {
                result.append('_');
            }
        }
        System.out.println(result);
        return result.toString().getBytes();
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
                                        byte[] bytes = fill10Bytes(c, random.nextInt(10) + 1);
                                        c++;
                                        buf.writeBytes(bytes);
                                    }
                                    /**
                                     * 0000______
                                     * 111_______
                                     * 222222222_
                                     * 333333____
                                     * 444444444_
                                     * 55________
                                     * 6666______
                                     * 777_______
                                     * 88888888__
                                     * 9999999999
                                     */
                                    ctx.writeAndFlush(buf);
                                    /**
                                     * buf发送内容日志打印如下
                                     * 21:21:14.847 [nioEventLoopGroup-2-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xb013e7aa, L:/127.0.0.1:61277 - R:localhost/127.0.0.1:8080] WRITE: 100B
                                     *          +-------------------------------------------------+
                                     *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
                                     * +--------+-------------------------------------------------+----------------+
                                     * |00000000| 30 30 30 30 5f 5f 5f 5f 5f 5f 31 31 31 5f 5f 5f |0000______111___|
                                     * |00000010| 5f 5f 5f 5f 32 32 32 32 32 32 32 32 32 5f 33 33 |____222222222_33|
                                     * |00000020| 33 33 33 33 5f 5f 5f 5f 34 34 34 34 34 34 34 34 |3333____44444444|
                                     * |00000030| 34 5f 35 35 5f 5f 5f 5f 5f 5f 5f 5f 36 36 36 36 |4_55________6666|
                                     * |00000040| 5f 5f 5f 5f 5f 5f 37 37 37 5f 5f 5f 5f 5f 5f 5f |______777_______|
                                     * |00000050| 38 38 38 38 38 38 38 38 5f 5f 39 39 39 39 39 39 |88888888__999999|
                                     * |00000060| 39 39 39 39                                     |9999            |
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
