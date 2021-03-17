package com.liaojiexin.VM.code;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * 解码器
 * @author Administrator
 *
 */
public class MsgPckDecode extends MessageToMessageDecoder<ByteBuf>{

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {

    }
}