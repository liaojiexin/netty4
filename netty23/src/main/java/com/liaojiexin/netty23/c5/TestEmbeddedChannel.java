package com.liaojiexin.netty23.c5;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * @ClassName TestEmbeddedChannel
 * @Description TODO    netty提供的测试方法，当有多个handler时候可以进行测试，而不用写复杂的业务代码
 * @Author liao
 * @Date 8:40 下午 2023/1/17
 **/
@Slf4j
public class TestEmbeddedChannel {
    public static void main(String[] args) {
        ChannelInboundHandlerAdapter h1 = new ChannelInboundHandlerAdapter(){    //添加入站handler
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                log.debug("handler-1入站");
                super.channelRead(ctx, msg);  //调用链，如果不加则没办法传递给下一个入站handler(无法传递给出站handler)
            }
        };
        ChannelInboundHandlerAdapter h2 = new ChannelInboundHandlerAdapter(){    //添加入站handler
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                log.debug("handler-2入站");
                super.channelRead(ctx, msg);  //调用链，如果不加则没办法传递给下一个入站handler(无法传递给出站handler)
            }
        };
        ChannelOutboundHandlerAdapter h3 = new ChannelOutboundHandlerAdapter(){    //添加入站handler
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                log.debug("handler-3出站");
                super.write(ctx, msg, promise);
            }
        };
        ChannelOutboundHandlerAdapter h4 = new ChannelOutboundHandlerAdapter(){    //添加入站handler
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                log.debug("handler-4出站");
                super.write(ctx, msg, promise);
            }
        };

        EmbeddedChannel embeddedChannel=new EmbeddedChannel(h1,h2,h3,h4);
        //模拟入站
        embeddedChannel.writeInbound(ByteBufAllocator.DEFAULT.buffer().writeBytes("入站".getBytes()));
        //模拟出站
        embeddedChannel.writeOutbound(ByteBufAllocator.DEFAULT.buffer().writeBytes("入站".getBytes()));

        /**
         * 测试结果
         * 20:49:28.967 [main] DEBUG com.liaojiexin.netty23.c5.TestEmbeddedChannel - handler-1入站
         * 20:49:28.967 [main] DEBUG com.liaojiexin.netty23.c5.TestEmbeddedChannel - handler-2入站
         * 20:49:28.968 [main] DEBUG com.liaojiexin.netty23.c5.TestEmbeddedChannel - handler-4出站
         * 20:49:28.968 [main] DEBUG com.liaojiexin.netty23.c5.TestEmbeddedChannel - handler-3出站
         */
    }
}
