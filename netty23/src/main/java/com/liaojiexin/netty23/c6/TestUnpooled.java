package com.liaojiexin.netty23.c6;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

/**
 * @ClassName TestUnpooled
 * @Description TODO
 * @Author liao
 * @Date 12:04 下午 2023/1/20
 **/
public class TestUnpooled {
    public static void main(String[] args) {
        ByteBuf buf1= ByteBufAllocator.DEFAULT.buffer(5);
        buf1.writeBytes(new byte[]{1,2,3,4,5});
        ByteBuf buf2=ByteBufAllocator.DEFAULT.buffer(5);
        buf2.writeBytes(new byte[]{6,7,8,9,10});

        //当包装ByteBuf个数超过一个时，底层使用了CompositeByteBuf
        ByteBuf buf3= Unpooled.wrappedBuffer(buf1,buf2);
        System.out.println(ByteBufUtil.prettyHexDump(buf3));
        /**
         *          +-------------------------------------------------+
         *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
         * +--------+-------------------------------------------------+----------------+
         * |00000000| 01 02 03 04 05 06 07 08 09 0a                   |..........      |
         * +--------+-------------------------------------------------+----------------+
         */

        //也可以用来包装普通字节数组，底层也是零拷贝，用同个物理内存。(不会有真正的拷贝)
        ByteBuf buf4=Unpooled.wrappedBuffer(new byte[]{1,2,3},new byte[]{4,5,6});
        System.out.println(buf4.getClass());
        System.out.println(ByteBufUtil.prettyHexDump(buf4));
        /**
         * class io.netty.buffer.CompositeByteBuf
         *          +-------------------------------------------------+
         *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
         * +--------+-------------------------------------------------+----------------+
         * |00000000| 01 02 03 04 05 06                               |......          |
         * +--------+-------------------------------------------------+----------------+
         */
    }
}
