package com.liaojiexin.VM.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.DatagramPacket;

/**
 * @ClassName: UdpHandler
 * @Description: TODO
 * @version: 1.0    UDP处理类
 * @author: liaojiexin
 * @date: 2021/3/18 15:56
 */
public class UdpHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {

    }
}
