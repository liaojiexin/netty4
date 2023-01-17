package com.liaojiexin.netty23.c6;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

/**
 * @ClassName TestByteBuf
 * @Description TODO
 * @Author liao
 * @Date 9:14 下午 2023/1/17
 **/
public class TestByteBuf {

    public static void main(String[] args) {
        //创建bytebuf,默认为256字节，会动态扩容
        ByteBuf byteBuf= ByteBufAllocator.DEFAULT.buffer();
        //这里打印出class io.netty.buffer.PooledUnsafeDirectByteBuf表示这是池化(Pooled)和使用直接内存(Direct)
        System.out.println(byteBuf.getClass());
        ByteBuf byteBuf1=ByteBufAllocator.DEFAULT.heapBuffer();
        //这里打印出class io.netty.buffer.PooledUnsafeHeapByteBuf表示这是池化(Pooled)和使用堆内存(Heap)
        System.out.println(byteBuf1.getClass());
        log(byteBuf);
        StringBuffer stringBuffer=new StringBuffer();
        for (int i=0;i<300;i++){
            stringBuffer.append("a");
        }
        byteBuf.writeBytes(stringBuffer.toString().getBytes());
        log(byteBuf);

        /**
         * 打印结果如下
         * PooledUnsafeDirectByteBuf(ridx: 0, widx: 0, cap: 256)
         * PooledUnsafeDirectByteBuf(ridx: 0, widx: 300, cap: 512)
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
                .append("read index:").append(buf.readerIndex())
                .append("write index:").append(buf.writerIndex())
                .append("capacity").append(buf.capacity())
                .append(NEWLINE);
        appendPrettyHexDump(stringBuilder,buf);
        System.out.println(stringBuilder.toString());
    }
}
