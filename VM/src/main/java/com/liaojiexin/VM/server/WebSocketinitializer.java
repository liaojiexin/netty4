package com.liaojiexin.VM.server;

import com.liaojiexin.VM.code.ProtocolSelectorHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @ClassName: WebSocketinitializer
 * @Description: TODO
 * @version: 1.0
 * @author: liaojiexin
 * @date: 2021/3/18 11:30
 */
public class WebSocketinitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //从Channel中获取对应的pipeline
        ChannelPipeline channelPipeline = ch.pipeline();
        //多协议解码器，进行协议判断在做相对应解码
        channelPipeline.addLast(new ProtocolSelectorHandler());

    }


}
