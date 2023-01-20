package com.liaojiexin.netty23.c6;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

/**
 * @ClassName TestComposite
 * @Description TODO
 * @Author liao
 * @Date 11:30 下午 2023/1/19
 **/
public class TestComposite {
    public static void main(String[] args) {
        ByteBuf buf1= ByteBufAllocator.DEFAULT.buffer();
        buf1.writeBytes(new byte[]{1,2,3,4,5});
        ByteBuf buf2= ByteBufAllocator.DEFAULT.buffer();
        buf2.writeBytes(new byte[]{6,7,8,9,10});
        log(buf1);
        log(buf2);

        //这种方法虽然也是可以让buf1和buf2的数据插入到buf中，但是两边是不同的物理地址
        /*ByteBuf buf=ByteBufAllocator.DEFAULT.buffer();
        buf.writeBytes(buf1).writeBytes(buf2);
        log(buf);*/

        CompositeByteBuf buf=ByteBufAllocator.DEFAULT.compositeBuffer();
        //这里要注意默认的addComponents方法不会调整写指针，所以要设置第一个参数为true
        buf.addComponents(true,buf1,buf2);
        buf1.retain();
        log(buf);
        /**
         *  read index:0 write index:10 capacity:10
         *          +-------------------------------------------------+
         *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
         * +--------+-------------------------------------------------+----------------+
         * |00000000| 01 02 03 04 05 06 07 08 09 0a                   |..........      |
         * +--------+-------------------------------------------------+----------------+
         */

        buf1.setByte(0,'a');
        log(buf1);
        log(buf);
        /**
         * buf1内容变化，相同物理地址的buf也跟随变化:
         *  read index:0 write index:5 capacity:256
         *          +-------------------------------------------------+
         *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
         * +--------+-------------------------------------------------+----------------+
         * |00000000| 61 02 03 04 05                                  |a....           |
         * +--------+-------------------------------------------------+----------------+
         *  read index:0 write index:10 capacity:10
         *          +-------------------------------------------------+
         *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
         * +--------+-------------------------------------------------+----------------+
         * |00000000| 61 02 03 04 05 06 07 08 09 0a                   |a.........      |
         * +--------+-------------------------------------------------+----------------+
         */

        buf1.release();
        log(buf);
        /**
         * 上面buf1做了内存释放，会导致合并的buf也收到影响
         * Exception in thread "main" io.netty.util.IllegalReferenceCountException: refCnt: 0
         * 正常操作合并后的Bytebuf,原Bytebuf要调用调用retain方法，给计数器加1，然后合并后的Bytebuf用完后自己做内存释放
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
