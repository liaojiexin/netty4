package com.liaojiexin.netty23.c8;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @ClassName TestLengthFieldDecoder
 * @Description TODO
 * @Author liao
 * @Date 11:13 下午 2023/1/20
 **/
public class TestLengthFieldDecoder {
    public static void main(String[] args) {
        EmbeddedChannel channel=new EmbeddedChannel(
                //new LengthFieldBasedFrameDecoder(1024,0,4,0,0),
                /**
                 * 发送消息体里面只有消息长度和消息内容，两个信息,打印如下(没有剥离消息长度信息):
                 * 23:18:52.648 [main] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xembedded, L:embedded - R:embedded] READ: 15B
                 *          +-------------------------------------------------+
                 *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
                 * +--------+-------------------------------------------------+----------------+
                 * |00000000| 00 00 00 0b 48 65 6c 6c 6f 2c 57 6f 72 6c 64    |....Hello,World |
                 * +--------+-------------------------------------------------+----------------+
                 * 23:18:52.648 [main] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xembedded, L:embedded - R:embedded] READ: 7B
                 *          +-------------------------------------------------+
                 *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
                 * +--------+-------------------------------------------------+----------------+
                 * |00000000| 00 00 00 03 48 69 21                            |....Hi!         |
                 * +--------+-------------------------------------------------+----------------+
                 */
                //new LengthFieldBasedFrameDecoder(1024,0,4,0,4),
                /**
                 * 发送消息体里面只有消息长度和消息内容，两个信息,打印如下(剥离消息长度信息):
                 * 23:21:07.343 [main] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xembedded, L:embedded - R:embedded] READ: 11B
                 *          +-------------------------------------------------+
                 *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
                 * +--------+-------------------------------------------------+----------------+
                 * |00000000| 48 65 6c 6c 6f 2c 57 6f 72 6c 64                |Hello,World     |
                 * +--------+-------------------------------------------------+----------------+
                 * 23:21:07.343 [main] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xembedded, L:embedded - R:embedded] READ: 3B
                 *          +-------------------------------------------------+
                 *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
                 * +--------+-------------------------------------------------+----------------+
                 * |00000000| 48 69 21                                        |Hi!             |
                 * +--------+-------------------------------------------------+----------------+
                 */
                new LengthFieldBasedFrameDecoder(1024,0,4,1,0),
                /**
                 * 发送消息体里面只有消息长度，版本号和消息内容，两个信息,打印如下(没有剥离消息长度信息):
                 * 23:22:48.446 [main] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xembedded, L:embedded - R:embedded] READ: 16B
                 *          +-------------------------------------------------+
                 *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
                 * +--------+-------------------------------------------------+----------------+
                 * |00000000| 00 00 00 0b 01 48 65 6c 6c 6f 2c 57 6f 72 6c 64 |.....Hello,World|
                 * +--------+-------------------------------------------------+----------------+
                 * 23:22:48.446 [main] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xembedded, L:embedded - R:embedded] READ: 8B
                 *          +-------------------------------------------------+
                 *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
                 * +--------+-------------------------------------------------+----------------+
                 * |00000000| 00 00 00 03 01 48 69 21                         |.....Hi!        |
                 * +--------+-------------------------------------------------+----------------+
                 */
                new LoggingHandler(LogLevel.DEBUG)
        );

        //4个字节来记录内容长度
        ByteBuf buf= ByteBufAllocator.DEFAULT.buffer();
        send(buf,"Hello,World");
        send(buf,"Hi!");
        channel.writeInbound(buf);
    }

    /**
     * 发送消息
     * @param buf
     * @param s 消息内容
     */
    private static void send(ByteBuf buf,String s){
        byte[] bytes=s.getBytes();  //实际内容
        int length =bytes.length;   //实际内容长度
        buf.writeInt(length);
        buf.writeByte(1);   //增加了头信息，版本号
        buf.writeBytes(bytes);
    }
}
