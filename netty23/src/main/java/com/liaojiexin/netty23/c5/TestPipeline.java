package com.liaojiexin.netty23.c5;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * @ClassName TestPipeline
 * @Description TODO
 * @Author liao
 * @Date 6:00 下午 2023/1/17
 **/
@Slf4j
public class TestPipeline {
    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        //1.通过channel拿到pipeline
                        ChannelPipeline pipeline = ch.pipeline();
                        //2.添加处理器，注意其实内部在头和为已经加了两个handler，分别是head和tail;head->handler(自己加的)->tail
                        //添加入站，handler1、2、3
                        pipeline.addLast(
                            "handler-1",    //自定义handler名称
                            new ChannelInboundHandlerAdapter(){    //添加入站handler
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("handler-1入站");
                                ByteBuf byteBuf=(ByteBuf)msg;
                                String s=byteBuf.toString(Charset.defaultCharset());
                                super.channelRead(ctx, s);  //调用链，如果不加则没办法传递给下一个入站handler(无法传递给出站handler)
                                //ctx.fireChannelRead(s) //效果等同于super.channelRead(ctx, s);
                            }
                        });
                        pipeline.addLast(
                                "handler-2",    //自定义handler名称
                                new ChannelInboundHandlerAdapter(){    //添加入站handler
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        log.debug("handler-2入站");
                                        Student student = new Student(msg.toString());
                                        super.channelRead(ctx, student);
                                    }
                                });
                        /*pipeline.addLast(
                                "handler-4",    //自定义handler名称
                                new ChannelOutboundHandlerAdapter(){    //添加出站handler
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                        log.debug("handler-4出站");
                                        super.write(ctx, msg, promise);
                                    }
                                });*/
                        pipeline.addLast(
                                "handler-3",    //自定义handler名称
                                new ChannelInboundHandlerAdapter(){    //添加入站handler
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        log.debug("handler-3入站,结果:{}",msg);
                                        //这里没必要super.channelRead(ctx, student);这行代码了，因为下面为出站handler，没办法传递
                                        //下面这一行数据要添加，因为如果不添加，channel中没有数据的话，下面的出站是不会打印出信息的。
                                        ch.writeAndFlush(ctx.alloc().buffer().writeBytes("server...".getBytes()));
                                        /*这一行代码和上面的区别是ctx是从当前handler所在的位置进行往前(逆时)找出站handler。而现在handler的顺序是
                                         head->handler1->handler2->handler3->handler4->handler5->handler6->tail
                                        所以handler3前面没有出站handler，所以不会打印出内容。除非在handler-3前面加入出站代码。如上面注释掉的handler-4代码*/
                                        //ctx.writeAndFlush(ctx.alloc().buffer().writeBytes("server...".getBytes()));
                                    }
                                });
                        //添加出站，handler4、5、6
                        pipeline.addLast(
                                "handler-4",    //自定义handler名称
                                new ChannelOutboundHandlerAdapter(){    //添加出站handler
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                        log.debug("handler-4出站");
                                        super.write(ctx, msg, promise);
                                    }
                                });
                        pipeline.addLast(
                                "handler-5",    //自定义handler名称
                                new ChannelOutboundHandlerAdapter(){    //添加出站handler
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                        log.debug("handler-5出站");
                                        super.write(ctx, msg, promise);
                                    }
                                });
                        pipeline.addLast(
                                "handler-6",    //自定义handler名称
                                new ChannelOutboundHandlerAdapter(){    //添加出站handler
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                        log.debug("handler-6出站");
                                        super.write(ctx, msg, promise);
                                    }
                                });
                    }
                })
                .bind(8080);

        /**
         * 这里可以用c3.ChannelFutureCloseClient类来做客户端测试
         * 测试结果如下：可以看到入站是按找1、2、3的顺序打印出来的，而出站则是6、5、4，所以可知入站顺序是顺时，而出站则是逆时
         * 18:19:57.029 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c5.TestPipeline - handler-1入站
         * 18:19:57.029 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c5.TestPipeline - handler-2入站
         * 18:19:57.029 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c5.TestPipeline - handler-3入站
         * 18:19:57.029 [nioEventLoopGroup-2-2] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded inbound message PooledUnsafeDirectByteBuf(ridx: 0, widx: 1, cap: 2048) that reached at the tail of the pipeline. Please check your pipeline configuration.
         * 18:19:57.036 [nioEventLoopGroup-2-2] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded message pipeline : [handler-1, handler-2, handler-3, handler-4, handler-5, handler-6, DefaultChannelPipeline$TailContext#0]. Channel : [id: 0x84565aae, L:/127.0.0.1:8080 - R:/127.0.0.1:57718].
         * 18:19:57.037 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c5.TestPipeline - handler-6出站
         * 18:19:57.037 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c5.TestPipeline - handler-5出站
         * 18:19:57.037 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c5.TestPipeline - handler-4出站
         */


        /**
         * 测试入站处理器，在客户端那边输入张三，服务器端结果如下:
         *20:06:08.068 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c5.TestPipeline - handler-1入站
         * 20:06:08.069 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c5.TestPipeline - handler-2入站
         * 20:06:08.069 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c5.TestPipeline - handler-3入站,结果:TestPipeline.Student(name=张三)
         * 20:06:08.069 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c5.TestPipeline - handler-6出站
         * 20:06:08.069 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c5.TestPipeline - handler-5出站
         * 20:06:08.069 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c5.TestPipeline - handler-4出站
         *
         */


        /**
         * 测试出站处理器，在客户端输入张三，按情况分下面2种结果
         *
         * 如果handler-3代码中用的是ch.writeAndFlush(ctx.alloc().buffer().writeBytes("server...".getBytes()));
         *20:28:38.111 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c5.TestPipeline - handler-1入站
         * 20:28:38.112 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c5.TestPipeline - handler-2入站
         * 20:28:38.113 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c5.TestPipeline - handler-3入站,结果:TestPipeline.Student(name=张三)
         * 20:28:38.113 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c5.TestPipeline - handler-6出站
         * 20:28:38.113 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c5.TestPipeline - handler-5出站
         * 20:28:38.113 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c5.TestPipeline - handler-4出站
         *
         * 如果handler-3中用的是ctx.writeAndFlush(ctx.alloc().buffer().writeBytes("server...".getBytes()));且前面没有其他出站handler则
         * 20:29:32.572 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c5.TestPipeline - handler-1入站
         * 20:29:32.573 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c5.TestPipeline - handler-2入站
         * 20:29:32.574 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c5.TestPipeline - handler-3入站,结果:TestPipeline.Student(name=张三)
         * 如果handler-3前面有出站handler，例如handler-4则
         * 20:31:48.161 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c5.TestPipeline - handler-1入站
         * 20:31:48.162 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c5.TestPipeline - handler-2入站
         * 20:31:48.163 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c5.TestPipeline - handler-3入站,结果:TestPipeline.Student(name=张三)
         * 20:31:48.163 [nioEventLoopGroup-2-2] DEBUG com.liaojiexin.netty23.c5.TestPipeline - handler-4出站
         *
         */
    }

    @Data
    @AllArgsConstructor //构造函数
    static class Student{
        private String name;
    }

}