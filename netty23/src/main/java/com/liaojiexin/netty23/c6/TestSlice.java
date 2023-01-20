package com.liaojiexin.netty23.c6;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

/**
 * @ClassName TestSlice
 * @Description TODO Slice方法使用
 * @Author liao
 * @Date 10:26 下午 2023/1/19
 **/
public class TestSlice {
    public static void main(String[] args) {
        ByteBuf buf= ByteBufAllocator.DEFAULT.buffer(10);
        buf.writeBytes(new byte[]{'a','b','c','d','e','f','g','h','i','j'});
        log(buf);
        /**
         *  read index:0 write index:10 capacity10
         *          +-------------------------------------------------+
         *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
         * +--------+-------------------------------------------------+----------------+
         * |00000000| 61 62 63 64 65 66 67 68 69 6a                   |abcdefghij      |
         * +--------+-------------------------------------------------+----------------+
         */

        //在切片过程中，没有发生数据复制
        //slice第一个参数表示从第几个下表开始(下表从0开始)，第二个参数表示要多少个
        ByteBuf buf1=buf.slice(0,5);    //从0开始，往后5个
        buf1.retain();  //计数加一，以防止原Bytebuf内存释放对切片造成影响
        ByteBuf buf2=buf.slice(5,5);
        buf2.retain();
        log(buf1);
        log(buf2);
        /**
         * 可以看到buf已经被切为buf1和buf2
         * read index:0 write index:5 capacity:5
         *          +-------------------------------------------------+
         *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
         * +--------+-------------------------------------------------+----------------+
         * |00000000| 61 62 63 64 65                                  |abcde           |
         * +--------+-------------------------------------------------+----------------+
         *  read index:0 write index:5 capacity:5
         *          +-------------------------------------------------+
         *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
         * +--------+-------------------------------------------------+----------------+
         * |00000000| 66 67 68 69 6a                                  |fghij           |
         * +--------+-------------------------------------------------+----------------+
         */

        System.out.println("========================");
        buf1.setByte(0,'b');
        log(buf);
        log(buf1);
        /**
         * 可以看到切出来的Bytebuf和原来的Bytebuf用的是同个物理内存地址
         * read index:0 write index:10 capacity:10
         *          +-------------------------------------------------+
         *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
         * +--------+-------------------------------------------------+----------------+
         * |00000000| 62 62 63 64 65 66 67 68 69 6a                   |bbcdefghij      |
         * +--------+-------------------------------------------------+----------------+
         *  read index:0 write index:5 capacity:5
         *          +-------------------------------------------------+
         *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
         * +--------+-------------------------------------------------+----------------+
         * |00000000| 62 62 63 64 65                                  |bbcde           |
         * +--------+-------------------------------------------------+----------------+
         */

        //这里要注意slice有两个地方
        //1、slice方法切片出来的ByteBuf无法扩容，即已经是最大容量了，因为扩容的话会影响到其他切片
        //buf1.writeByte('x');
        /**
         * 上面这行代码会报一下异常，因为已经是最大容量了，无法再扩容
         * Exception in thread "main" java.lang.IndexOutOfBoundsException: writerIndex(5) + minWritableBytes(1) exceeds maxCapacity(5):
         * UnpooledSlicedByteBuf(ridx: 0, widx: 5, cap: 5/5, unwrapped: PooledUnsafeDirectByteBuf(ridx: 0, widx: 10, cap: 10))
         */
        //2、原Bytebuf内存释放(release)后会影响到切片的切片
        buf.release();  //内存释放
        log(buf1);
        /**
         * 上面这行代码会报一下异常,因为原Bytebuf和切片的Bytebuf用的是同个物理内存，这里处理办法是在切片后，因当给切片的
         * Bytebuf调用retain()方法，给计数加1，这样原Bytebuf调用release后计数还是不为0(即内存还是有被引用)，所以不会被释放。
         * 然后切片Bytebuf用完后自己再做内存释放
         * Exception in thread "main" io.netty.util.IllegalReferenceCountException: refCnt: 0
         */

        //生成切片后记得加上retain()方法(见上方代码)，防止原Bytebuf释放对切片Bytebuf的影响，同时切片Bytebuf用完后要记得释放
        buf1.release();
        buf2.release();
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
