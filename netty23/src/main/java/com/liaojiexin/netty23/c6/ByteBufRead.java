package com.liaojiexin.netty23.c6;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

/**
 * @ClassName ByteBufRead
 * @Description TODO
 * @Author liao
 * @Date 10:26 下午 2023/1/17
 **/
public class ByteBufRead{

    public static void main(String[] args) {
        //创建bytebuf,默认为256字节，会动态扩容
        ByteBuf byteBuf= ByteBufAllocator.DEFAULT.buffer(10);

        //1、写入4个字节，和2个整数(每个4字节)
        byteBuf.writeBytes(new byte[]{1,2,3,4});
        byteBuf.writeInt(5);
        byteBuf.writeInt(6);
        log(byteBuf);
        /**
         *  read index:0 write index:12 capacity:16
         *          +-------------------------------------------------+
         *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
         * +--------+-------------------------------------------------+----------------+
         * |00000000| 01 02 03 04 00 00 00 05 00 00 00 06             |............    |
         * +--------+-------------------------------------------------+----------------+
         */

        //2、读取4个字节
        System.out.println(byteBuf.readByte()); //1
        System.out.println(byteBuf.readByte()); //2
        System.out.println(byteBuf.readByte()); //3
        System.out.println(byteBuf.readByte()); //4
        log(byteBuf);
        /**
         *  read index:4 write index:12 capacity:16
         *          +-------------------------------------------------+
         *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
         * +--------+-------------------------------------------------+----------------+
         * |00000000| 00 00 00 05 00 00 00 06                         |........        |
         * +--------+-------------------------------------------------+----------------+
         */

        //3、重复读取int整数5
        //可以在read前面先做个标记mark
        byteBuf.markReaderIndex();
        System.out.println(byteBuf.readInt());  //5
        log(byteBuf);
        /**
         *read index:8 write index:12 capacity:16
         *          +-------------------------------------------------+
         *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
         * +--------+-------------------------------------------------+----------------+
         * |00000000| 00 00 00 06                                     |....            |
         * +--------+-------------------------------------------------+----------------+
         */
        //重复读调用reset就会回到之前mark标记的位置
        byteBuf.resetReaderIndex();
        log(byteBuf);
        /**
         *  read index:4 write index:12 capacity:16
         *          +-------------------------------------------------+
         *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
         * +--------+-------------------------------------------------+----------------+
         * |00000000| 00 00 00 05 00 00 00 06                         |........        |
         * +--------+-------------------------------------------------+----------------+
         */

        //还有一系列get开头的方法，这些方法也可以获取到值，但是不会改变读指针的位置
    }

    /**
     * 自定义日志
     * @param buf
     */
    private static void log(ByteBuf buf){
        int length=buf.readableBytes();
        int rows=length/16+(length%15==0?0:1)+4;
        StringBuilder stringBuilder=new StringBuilder(rows*80*2)
                .append(" read index:").append(buf.readerIndex())
                .append(" write index:").append(buf.writerIndex())
                .append(" capacity:").append(buf.capacity())
                .append(NEWLINE);
        appendPrettyHexDump(stringBuilder,buf);
        System.out.println(stringBuilder.toString());
    }
}
