package com.liaojiexin.netty23.c6;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

/**
 * @ClassName ByteBufWrite
 * @Description TODO
 * @Author liao
 * @Date 10:14 下午 2023/1/17
 **/
public class ByteBufWrite {

    public static void main(String[] args) {
        //创建bytebuf,默认为256字节，会动态扩容
        ByteBuf byteBuf= ByteBufAllocator.DEFAULT.buffer(10);

        //1、写入4个字节
        byteBuf.writeBytes(new byte[]{1,2,3,4});
        log(byteBuf);
        /**
         * read index:0 write index:4 capacity:10
         *          +-------------------------------------------------+
         *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
         * +--------+-------------------------------------------------+----------------+
         * |00000000| 01 02 03 04                                     |....            |
         * +--------+-------------------------------------------------+----------------+
         */

        //2、在写入一个int整数，也是4个字节
        byteBuf.writeInt(5);
        log(byteBuf);
        /**
         *  read index:0 write index:8 capacity:10
         *          +-------------------------------------------------+
         *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
         * +--------+-------------------------------------------------+----------------+
         * |00000000| 01 02 03 04 00 00 00 05                         |........        |
         * +--------+-------------------------------------------------+----------------+
         */
        //还有一系列set开头的方法，也可以写入数据，但是不会改变写指针的位置

        //3、扩容，再写入一个整数6，因为原容量为10，所以不够就会引发扩容
        /**
         * 扩容规则是
         * 1、如何写入后数据大小未超过 512，则选择下一个 16 的整数倍，例如写入后大小为 12， 则扩容后 capacity 是16
         * 2、如果写入后数据大小超过 512，则选择下一个2^n，例如写入后大小为 513，则扩容后 capacity 是2^10=1024 (2^9=512 已经不够了）
         * 3、扩容不能超过 max capacity 会报错
         */
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
