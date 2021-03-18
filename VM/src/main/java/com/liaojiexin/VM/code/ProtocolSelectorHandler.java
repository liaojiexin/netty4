package com.liaojiexin.VM.code;


import com.liaojiexin.VM.handler.CutsomServerHandler;
import com.liaojiexin.VM.handler.MyHttpServerHandler;
import com.liaojiexin.VM.handler.MyTextWebSocketFrameHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * 多协议选择解码器
 * @author Administrator
 *
 */
public class ProtocolSelectorHandler extends ByteToMessageDecoder {

    /**
     * websocket定义请求行前缀
     */
    private static final String WEBSOCKET_LINE_PREFIX = "GET /ws";
    /**
     * websocket的uri
     */
    private static final String WEBSOCKET_PREFIX = "/ws";
    /**
     * 检查10个字节，没有空格就是自定义协议
     */
    private static final int SPACE_LENGTH = 10;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("before :" + ctx.pipeline().toString());
        if (isWebSocketUrl(in)) {
            System.out.println("addWebSocketHandlers");

            addWebSocketHandlers(ctx.pipeline());
        } else if (isCustomProcotol(in)) {
            System.out.println("addTCPProtocolHandlers");

            addTCPProtocolHandlers(ctx.pipeline());
        } else {
            System.out.println("addHTTPHandlers");
            addHTTPHandlers(ctx.pipeline());
        }
        ctx.pipeline().remove(this);
        System.out.println("after :" + ctx.pipeline().toString());
    }

    /**
     * 是否有websocket请求行前缀
     *
     * @param byteBuf
     * @return
     */
    private boolean isWebSocketUrl(ByteBuf byteBuf) {
        if (byteBuf.readableBytes() < WEBSOCKET_LINE_PREFIX.length()) {
            return false;
        }
        byteBuf.markReaderIndex();
        byte[] content = new byte[WEBSOCKET_LINE_PREFIX.length()];
        byteBuf.readBytes(content);
        byteBuf.resetReaderIndex();
        String s = new String(content, CharsetUtil.UTF_8);
        return s.equals(WEBSOCKET_LINE_PREFIX);
    }

    /**
     * 是否是自定义是有协议
     * @param byteBuf
     * @return
     */
    private boolean isCustomProcotol(ByteBuf byteBuf) {
        byteBuf.markReaderIndex();
        byte[] content = new byte[SPACE_LENGTH];
        byteBuf.readBytes(content);
        byteBuf.resetReaderIndex();
        String s = new String(content, CharsetUtil.UTF_8);
        return s.indexOf(" ") == -1;
    }

    /**
     * 动态添加WebSocket处理器
     * @param pipeline
     */
    private void addWebSocketHandlers(ChannelPipeline pipeline) {
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(8192));
        pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PREFIX));
        pipeline.addLast(new MyTextWebSocketFrameHandler());
    }
    /**
     * 动态添加TCP私有协议处理器
     * @param pipeline
     */
    private void addTCPProtocolHandlers(ChannelPipeline pipeline) {
        pipeline.addLast(new CustomDecoder(1024, 1, 4));//这里1代表长度属性是从索引1位置开始的，4代表有4个字节的长度
        pipeline.addLast(new CutsomServerHandler());
    }


    /**
     * 动态添加HTTP处理器
     * @param pipeline
     */
    private void addHTTPHandlers(ChannelPipeline pipeline) {
        pipeline.addLast(new HttpServerCodec());
        //在Http中有一些数据流的传输，那么数据流有大有小，如果说有一些相应的大数据流处理的话，需要在此添加
        //ChunkedWriteHandler：为一些大数据流添加支持
        pipeline.addLast(new ChunkedWriteHandler());
        //UdineHttpMessage进行处理，也就是会用到request以及response
        //HttpObjectAggregator：聚合器，聚合了FullHTTPRequest、FullHTTPResponse。。。，当你不想去管一些HttpMessage的时候，直接把这个handler丢到管道中，让Netty自行处理即可
        pipeline.addLast(new HttpObjectAggregator(2048*64));
//        pipeline.addLast(new MyHttpServerHandler());

    }
}