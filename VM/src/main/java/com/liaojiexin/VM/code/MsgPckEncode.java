package com.liaojiexin.VM.code;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码器
 * @author Administrator
 *
 */
public class MsgPckEncode extends MessageToByteEncoder<Object>{


    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {

    }
}
