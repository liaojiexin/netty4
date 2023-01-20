package com.liaojiexin.netty23.c8;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @ClassName TestFixedLengthFrameDecoder
 * @Description TODO 定长解码器 服务器端
 * @Author liao
 * @Date 8:25 下午 2023/1/20
 **/
public class FixedLengthFrameDecoderServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss=new NioEventLoopGroup();
        NioEventLoopGroup worker=new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap=new ServerBootstrap();
            //调整系统的接收缓冲器(滑动窗口)，一般不用设置，现在的系统很智能会根据发送方和接收方环境定一个合适的值
            //serverBootstrap.option(ChannelOption.SO_RCVBUF,2);  //设置接收缓冲区，为了让半包想象更加明显
            //调整netty的接收缓冲区(bytebuf),这里最小只能为16(16的整数)
            serverBootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR,new AdaptiveRecvByteBufAllocator(16,16,16)); //效果同上，设置接收缓冲区，为了让半包想象更加明显
            serverBootstrap.group(boss,worker).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override   //初始化
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            //添加定长解码器，和客户端约定了长度为10为一个完整消息。【注意解码器要放到下面打印日志前面，要先解码才能打印】
                            ch.pipeline().addLast(new FixedLengthFrameDecoder(10));
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));  //打印出服务器端收到的信息
                        }
                    });

            //做channel关闭操作
            ChannelFuture channelFuture=serverBootstrap.bind(8080).sync();
            channelFuture.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    /**
     * 用了定长解码器后打印效果如下:
     * 21:21:14.903 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xab8021ae, L:/127.0.0.1:8080 - R:/127.0.0.1:61277] READ: 10B
     *          +-------------------------------------------------+
     *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
     * +--------+-------------------------------------------------+----------------+
     * |00000000| 30 30 30 30 5f 5f 5f 5f 5f 5f                   |0000______      |
     * +--------+-------------------------------------------------+----------------+
     * 21:21:14.903 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded inbound message PooledSlicedByteBuf(ridx: 0, widx: 10, cap: 10/10, unwrapped: PooledUnsafeDirectByteBuf(ridx: 10, widx: 16, cap: 16)) that reached at the tail of the pipeline. Please check your pipeline configuration.
     * 21:21:14.903 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded message pipeline : [FixedLengthFrameDecoder#0, LoggingHandler#0, DefaultChannelPipeline$TailContext#0]. Channel : [id: 0xab8021ae, L:/127.0.0.1:8080 - R:/127.0.0.1:61277].
     * 21:21:14.904 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xab8021ae, L:/127.0.0.1:8080 - R:/127.0.0.1:61277] READ COMPLETE
     * 21:21:14.904 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xab8021ae, L:/127.0.0.1:8080 - R:/127.0.0.1:61277] READ: 10B
     *          +-------------------------------------------------+
     *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
     * +--------+-------------------------------------------------+----------------+
     * |00000000| 31 31 31 5f 5f 5f 5f 5f 5f 5f                   |111_______      |
     * +--------+-------------------------------------------------+----------------+
     * 21:21:14.904 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded inbound message PooledSlicedByteBuf(ridx: 0, widx: 10, cap: 10/10, unwrapped: PooledUnsafeDirectByteBuf(ridx: 20, widx: 32, cap: 64)) that reached at the tail of the pipeline. Please check your pipeline configuration.
     * 21:21:14.904 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded message pipeline : [FixedLengthFrameDecoder#0, LoggingHandler#0, DefaultChannelPipeline$TailContext#0]. Channel : [id: 0xab8021ae, L:/127.0.0.1:8080 - R:/127.0.0.1:61277].
     * 21:21:14.904 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xab8021ae, L:/127.0.0.1:8080 - R:/127.0.0.1:61277] READ: 10B
     *          +-------------------------------------------------+
     *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
     * +--------+-------------------------------------------------+----------------+
     * |00000000| 32 32 32 32 32 32 32 32 32 5f                   |222222222_      |
     * +--------+-------------------------------------------------+----------------+
     * 21:21:14.904 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded inbound message PooledSlicedByteBuf(ridx: 0, widx: 10, cap: 10/10, unwrapped: PooledUnsafeDirectByteBuf(ridx: 30, widx: 32, cap: 64)) that reached at the tail of the pipeline. Please check your pipeline configuration.
     * 21:21:14.904 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded message pipeline : [FixedLengthFrameDecoder#0, LoggingHandler#0, DefaultChannelPipeline$TailContext#0]. Channel : [id: 0xab8021ae, L:/127.0.0.1:8080 - R:/127.0.0.1:61277].
     * 21:21:14.904 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xab8021ae, L:/127.0.0.1:8080 - R:/127.0.0.1:61277] READ COMPLETE
     * 21:21:14.904 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xab8021ae, L:/127.0.0.1:8080 - R:/127.0.0.1:61277] READ: 10B
     *          +-------------------------------------------------+
     *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
     * +--------+-------------------------------------------------+----------------+
     * |00000000| 33 33 33 33 33 33 5f 5f 5f 5f                   |333333____      |
     * +--------+-------------------------------------------------+----------------+
     * 21:21:14.904 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded inbound message PooledSlicedByteBuf(ridx: 0, widx: 10, cap: 10/10, unwrapped: PooledUnsafeDirectByteBuf(ridx: 40, widx: 48, cap: 64)) that reached at the tail of the pipeline. Please check your pipeline configuration.
     * 21:21:14.904 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded message pipeline : [FixedLengthFrameDecoder#0, LoggingHandler#0, DefaultChannelPipeline$TailContext#0]. Channel : [id: 0xab8021ae, L:/127.0.0.1:8080 - R:/127.0.0.1:61277].
     * 21:21:14.904 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xab8021ae, L:/127.0.0.1:8080 - R:/127.0.0.1:61277] READ COMPLETE
     * 21:21:14.904 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xab8021ae, L:/127.0.0.1:8080 - R:/127.0.0.1:61277] READ: 10B
     *          +-------------------------------------------------+
     *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
     * +--------+-------------------------------------------------+----------------+
     * |00000000| 34 34 34 34 34 34 34 34 34 5f                   |444444444_      |
     * +--------+-------------------------------------------------+----------------+
     * 21:21:14.905 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded inbound message PooledSlicedByteBuf(ridx: 0, widx: 10, cap: 10/10, unwrapped: PooledUnsafeDirectByteBuf(ridx: 10, widx: 24, cap: 64)) that reached at the tail of the pipeline. Please check your pipeline configuration.
     * 21:21:14.905 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded message pipeline : [FixedLengthFrameDecoder#0, LoggingHandler#0, DefaultChannelPipeline$TailContext#0]. Channel : [id: 0xab8021ae, L:/127.0.0.1:8080 - R:/127.0.0.1:61277].
     * 21:21:14.905 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xab8021ae, L:/127.0.0.1:8080 - R:/127.0.0.1:61277] READ: 10B
     *          +-------------------------------------------------+
     *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
     * +--------+-------------------------------------------------+----------------+
     * |00000000| 35 35 5f 5f 5f 5f 5f 5f 5f 5f                   |55________      |
     * +--------+-------------------------------------------------+----------------+
     * 21:21:14.905 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded inbound message PooledSlicedByteBuf(ridx: 0, widx: 10, cap: 10/10, unwrapped: PooledUnsafeDirectByteBuf(ridx: 20, widx: 24, cap: 64)) that reached at the tail of the pipeline. Please check your pipeline configuration.
     * 21:21:14.905 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded message pipeline : [FixedLengthFrameDecoder#0, LoggingHandler#0, DefaultChannelPipeline$TailContext#0]. Channel : [id: 0xab8021ae, L:/127.0.0.1:8080 - R:/127.0.0.1:61277].
     * 21:21:14.905 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xab8021ae, L:/127.0.0.1:8080 - R:/127.0.0.1:61277] READ COMPLETE
     * 21:21:14.905 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xab8021ae, L:/127.0.0.1:8080 - R:/127.0.0.1:61277] READ: 10B
     *          +-------------------------------------------------+
     *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
     * +--------+-------------------------------------------------+----------------+
     * |00000000| 36 36 36 36 5f 5f 5f 5f 5f 5f                   |6666______      |
     * +--------+-------------------------------------------------+----------------+
     * 21:21:14.905 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded inbound message PooledSlicedByteBuf(ridx: 0, widx: 10, cap: 10/10, unwrapped: PooledUnsafeDirectByteBuf(ridx: 30, widx: 40, cap: 64)) that reached at the tail of the pipeline. Please check your pipeline configuration.
     * 21:21:14.905 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded message pipeline : [FixedLengthFrameDecoder#0, LoggingHandler#0, DefaultChannelPipeline$TailContext#0]. Channel : [id: 0xab8021ae, L:/127.0.0.1:8080 - R:/127.0.0.1:61277].
     * 21:21:14.905 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xab8021ae, L:/127.0.0.1:8080 - R:/127.0.0.1:61277] READ: 10B
     *          +-------------------------------------------------+
     *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
     * +--------+-------------------------------------------------+----------------+
     * |00000000| 37 37 37 5f 5f 5f 5f 5f 5f 5f                   |777_______      |
     * +--------+-------------------------------------------------+----------------+
     * 21:21:14.905 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded inbound message PooledSlicedByteBuf(ridx: 0, widx: 10, cap: 10/10, unwrapped: PooledUnsafeDirectByteBuf(ridx: 40, widx: 40, cap: 64)) that reached at the tail of the pipeline. Please check your pipeline configuration.
     * 21:21:14.905 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded message pipeline : [FixedLengthFrameDecoder#0, LoggingHandler#0, DefaultChannelPipeline$TailContext#0]. Channel : [id: 0xab8021ae, L:/127.0.0.1:8080 - R:/127.0.0.1:61277].
     * 21:21:14.905 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xab8021ae, L:/127.0.0.1:8080 - R:/127.0.0.1:61277] READ COMPLETE
     * 21:21:14.905 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xab8021ae, L:/127.0.0.1:8080 - R:/127.0.0.1:61277] READ: 10B
     *          +-------------------------------------------------+
     *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
     * +--------+-------------------------------------------------+----------------+
     * |00000000| 38 38 38 38 38 38 38 38 5f 5f                   |88888888__      |
     * +--------+-------------------------------------------------+----------------+
     * 21:21:14.905 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded inbound message PooledSlicedByteBuf(ridx: 0, widx: 10, cap: 10/10, unwrapped: PooledUnsafeDirectByteBuf(ridx: 10, widx: 16, cap: 16)) that reached at the tail of the pipeline. Please check your pipeline configuration.
     * 21:21:14.905 [nioEventLoopGroup-3-1] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded message pipeline : [FixedLengthFrameDecoder#0, LoggingHandler#0, DefaultChannelPipeline$TailContext#0]. Channel : [id: 0xab8021ae, L:/127.0.0.1:8080 - R:/127.0.0.1:61277].
     * 21:21:14.905 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xab8021ae, L:/127.0.0.1:8080 - R:/127.0.0.1:61277] READ COMPLETE
     * 21:21:14.905 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xab8021ae, L:/127.0.0.1:8080 - R:/127.0.0.1:61277] READ: 10B
     *          +-------------------------------------------------+
     *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
     * +--------+-------------------------------------------------+----------------+
     * |00000000| 39 39 39 39 39 39 39 39 39 39                   |9999999999      |
     * +--------+-------------------------------------------------+----------------+
     */
}
