package com.liaojiexin.netty23.c9;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import static io.netty.handler.codec.stomp.StompHeaders.CONTENT_LENGTH;

/**
 * @ClassName TestHttp
 * @Description TODO Http协议案例
 * @Author liao
 * @Date 11:19 上午 2023/1/21
 **/
@Slf4j
public class TestHttp {

    public static void main(String[] args) {
        NioEventLoopGroup boss=new NioEventLoopGroup();
        NioEventLoopGroup worker=new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap=new ServerBootstrap().group(boss,worker).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override   //初始化
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));  //打印日志到控制台
                            ch.pipeline().addLast(new HttpServerCodec());   //添加http协议的编码解码器
                            /**
                             * SimpleChannelInboundHandler方法可以直接用泛型来限制要处理的消息类型
                             *  例如下面ChannelInboundHandlerAdapter方法里面获取到的了两个类型，直接用
                             *  SimpleChannelInboundHandler来处理其中HttpRequest类型，即请求头
                             */
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<HttpRequest>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
                                    //获取请求路径,打印结果 请求路径:/index.html
                                    log.debug("请求路径:{}",msg.uri());
                                    //返回响应,第一个参数是返回此 HttpMessage 的协议版本，第二个参数表示http响应状态码
                                    DefaultFullHttpResponse response=new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
                                    byte[] bytes="<h1>Hello,World!</h1>".getBytes();
                                    response.content().writeBytes(bytes);
                                    response.headers().setInt(CONTENT_LENGTH,bytes.length); //请求头要加上消息内容长度，不然浏览器不知道消息多长就会一直请求不会结束
                                    //写会响应,返回给浏览器
                                    ctx.writeAndFlush(response);
                                    /**
                                     * 12:41:36.166 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0x14b28767, L:/0:0:0:0:0:0:0:1:8080 - R:/0:0:0:0:0:0:0:1:50839] WRITE: 60B
                                     *          +-------------------------------------------------+
                                     *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
                                     * +--------+-------------------------------------------------+----------------+
                                     * |00000000| 48 54 54 50 2f 31 2e 31 20 32 30 30 20 4f 4b 0d |HTTP/1.1 200 OK.|
                                     * |00000010| 0a 63 6f 6e 74 65 6e 74 2d 6c 65 6e 67 74 68 3a |.content-length:|
                                     * |00000020| 20 32 31 0d 0a 0d 0a 3c 68 31 3e 48 65 6c 6c 6f | 21....<h1>Hello|
                                     * |00000030| 2c 57 6f 72 6c 64 21 3c 2f 68 31 3e             |,World!</h1>    |
                                     * +--------+-------------------------------------------------+----------------+
                                     */

                                    //另外浏览器问题还会请求一个/favicon.ico请求，在实际的开发中需要对这个进行处理
                                }
                            });

                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){   //处理解码后获取到的信息
                                @Override   //读事件
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    log.debug("获取到的客户端信息发送过来的信息:{}",msg.getClass());

                                    /**
                                     * 打印结果如下，可以看到获取到两个信息。DefaultHttpRequest为请求头和请求行，LastHttpContent$1为请求体，
                                     * 这是因为new HttpServerCodec()编码解码器会自动把http请求拆分成两部分
                                     * 11:32:42.065 [nioEventLoopGroup-3-1] DEBUG com.liaojiexin.netty23.c9.TestHttp - 获取到的客户端信息发送过来的信息:class io.netty.handler.codec.http.DefaultHttpRequest
                                     * 11:32:42.065 [nioEventLoopGroup-3-1] DEBUG com.liaojiexin.netty23.c9.TestHttp - 获取到的客户端信息发送过来的信息:class io.netty.handler.codec.http.LastHttpContent$1
                                     */

                                    if (msg instanceof HttpRequest){    //请求行、请求头
                                        //..逻辑处理
                                    }else if(msg instanceof HttpContent){   //请求体
                                        //..逻辑处理
                                    }

                                    //下面为new LoggingHandler(LogLevel.DEBUG)打印出来的
//                                    11:32:42.049 [nioEventLoopGroup-3-1] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.maxCapacityPerThread: 4096
//                                    11:32:42.049 [nioEventLoopGroup-3-1] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.maxSharedCapacityFactor: 2
//                                    11:32:42.049 [nioEventLoopGroup-3-1] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.linkCapacity: 16
//                                    11:32:42.049 [nioEventLoopGroup-3-1] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.ratio: 8
//                                    11:32:42.049 [nioEventLoopGroup-3-1] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.delayedQueue.ratio: 8
//                                    11:32:42.054 [nioEventLoopGroup-3-1] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xd848a6a0, L:/0:0:0:0:0:0:0:1:8080 - R:/0:0:0:0:0:0:0:1:62246] READ: 804B
//                                            +-------------------------------------------------+
//                                            |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
//                                    +--------+-------------------------------------------------+----------------+
//                                    |00000000| 47 45 54 20 2f 69 6e 64 65 78 2e 68 74 6d 6c 20 |GET /index.html |
//                                    |00000010| 48 54 54 50 2f 31 2e 31 0d 0a 48 6f 73 74 3a 20 |HTTP/1.1..Host: |
//                                    |00000020| 6c 6f 63 61 6c 68 6f 73 74 3a 38 30 38 30 0d 0a |localhost:8080..|
//                                    |00000030| 43 6f 6e 6e 65 63 74 69 6f 6e 3a 20 6b 65 65 70 |Connection: keep|
//                                    |00000040| 2d 61 6c 69 76 65 0d 0a 73 65 63 2d 63 68 2d 75 |-alive..sec-ch-u|
//                                    |00000050| 61 3a 20 22 4e 6f 74 3f 41 5f 42 72 61 6e 64 22 |a: "Not?A_Brand"|
//                                    |00000060| 3b 76 3d 22 38 22 2c 20 22 43 68 72 6f 6d 69 75 |;v="8", "Chromiu|
//                                    |00000070| 6d 22 3b 76 3d 22 31 30 38 22 2c 20 22 47 6f 6f |m";v="108", "Goo|
//                                    |00000080| 67 6c 65 20 43 68 72 6f 6d 65 22 3b 76 3d 22 31 |gle Chrome";v="1|
//                                    |00000090| 30 38 22 0d 0a 73 65 63 2d 63 68 2d 75 61 2d 6d |08"..sec-ch-ua-m|
//                                    |000000a0| 6f 62 69 6c 65 3a 20 3f 30 0d 0a 73 65 63 2d 63 |obile: ?0..sec-c|
//                                    |000000b0| 68 2d 75 61 2d 70 6c 61 74 66 6f 72 6d 3a 20 22 |h-ua-platform: "|
//                                    |000000c0| 6d 61 63 4f 53 22 0d 0a 55 70 67 72 61 64 65 2d |macOS"..Upgrade-|
//                                    |000000d0| 49 6e 73 65 63 75 72 65 2d 52 65 71 75 65 73 74 |Insecure-Request|
//                                    |000000e0| 73 3a 20 31 0d 0a 55 73 65 72 2d 41 67 65 6e 74 |s: 1..User-Agent|
//                                    |000000f0| 3a 20 4d 6f 7a 69 6c 6c 61 2f 35 2e 30 20 28 4d |: Mozilla/5.0 (M|
//                                    |00000100| 61 63 69 6e 74 6f 73 68 3b 20 49 6e 74 65 6c 20 |acintosh; Intel |
//                                    |00000110| 4d 61 63 20 4f 53 20 58 20 31 30 5f 31 35 5f 37 |Mac OS X 10_15_7|
//                                    |00000120| 29 20 41 70 70 6c 65 57 65 62 4b 69 74 2f 35 33 |) AppleWebKit/53|
//                                    |00000130| 37 2e 33 36 20 28 4b 48 54 4d 4c 2c 20 6c 69 6b |7.36 (KHTML, lik|
//                                    |00000140| 65 20 47 65 63 6b 6f 29 20 43 68 72 6f 6d 65 2f |e Gecko) Chrome/|
//                                    |00000150| 31 30 38 2e 30 2e 30 2e 30 20 53 61 66 61 72 69 |108.0.0.0 Safari|
//                                    |00000160| 2f 35 33 37 2e 33 36 0d 0a 41 63 63 65 70 74 3a |/537.36..Accept:|
//                                    |00000170| 20 74 65 78 74 2f 68 74 6d 6c 2c 61 70 70 6c 69 | text/html,appli|
//                                    |00000180| 63 61 74 69 6f 6e 2f 78 68 74 6d 6c 2b 78 6d 6c |cation/xhtml+xml|
//                                    |00000190| 2c 61 70 70 6c 69 63 61 74 69 6f 6e 2f 78 6d 6c |,application/xml|
//                                    |000001a0| 3b 71 3d 30 2e 39 2c 69 6d 61 67 65 2f 61 76 69 |;q=0.9,image/avi|
//                                    |000001b0| 66 2c 69 6d 61 67 65 2f 77 65 62 70 2c 69 6d 61 |f,image/webp,ima|
//                                    |000001c0| 67 65 2f 61 70 6e 67 2c 2a 2f 2a 3b 71 3d 30 2e |ge/apng,*/*;q=0.|
//                                    |000001d0| 38 2c 61 70 70 6c 69 63 61 74 69 6f 6e 2f 73 69 |8,application/si|
//                                    |000001e0| 67 6e 65 64 2d 65 78 63 68 61 6e 67 65 3b 76 3d |gned-exchange;v=|
//                                    |000001f0| 62 33 3b 71 3d 30 2e 39 0d 0a 53 65 63 2d 46 65 |b3;q=0.9..Sec-Fe|
//                                    |00000200| 74 63 68 2d 53 69 74 65 3a 20 6e 6f 6e 65 0d 0a |tch-Site: none..|
//                                    |00000210| 53 65 63 2d 46 65 74 63 68 2d 4d 6f 64 65 3a 20 |Sec-Fetch-Mode: |
//                                    |00000220| 6e 61 76 69 67 61 74 65 0d 0a 53 65 63 2d 46 65 |navigate..Sec-Fe|
//                                    |00000230| 74 63 68 2d 55 73 65 72 3a 20 3f 31 0d 0a 53 65 |tch-User: ?1..Se|
//                                    |00000240| 63 2d 46 65 74 63 68 2d 44 65 73 74 3a 20 64 6f |c-Fetch-Dest: do|
//                                    |00000250| 63 75 6d 65 6e 74 0d 0a 41 63 63 65 70 74 2d 45 |cument..Accept-E|
//                                    |00000260| 6e 63 6f 64 69 6e 67 3a 20 67 7a 69 70 2c 20 64 |ncoding: gzip, d|
//                                    |00000270| 65 66 6c 61 74 65 2c 20 62 72 0d 0a 41 63 63 65 |eflate, br..Acce|
//                                    |00000280| 70 74 2d 4c 61 6e 67 75 61 67 65 3a 20 7a 68 2d |pt-Language: zh-|
//                                    |00000290| 43 4e 2c 7a 68 3b 71 3d 30 2e 39 2c 65 6e 2d 55 |CN,zh;q=0.9,en-U|
//                                    |000002a0| 53 3b 71 3d 30 2e 38 2c 65 6e 3b 71 3d 30 2e 37 |S;q=0.8,en;q=0.7|
//                                    |000002b0| 0d 0a 43 6f 6f 6b 69 65 3a 20 44 43 49 5f 43 4c |..Cookie: DCI_CL|
//                                    |000002c0| 49 45 4e 54 5f 49 44 3d 66 63 63 64 66 65 66 66 |IENT_ID=fccdfeff|
//                                    |000002d0| 2d 63 32 39 61 2d 34 32 34 33 2d 39 30 64 31 2d |-c29a-4243-90d1-|
//                                    |000002e0| 64 61 63 31 30 30 32 34 62 38 37 66 3b 20 48 6d |dac10024b87f; Hm|
//                                    |000002f0| 5f 6c 76 74 5f 65 39 65 31 31 34 64 39 35 38 65 |_lvt_e9e114d958e|
//                                    |00000300| 61 32 36 33 64 65 34 36 65 30 38 30 35 36 33 65 |a263de46e080563e|
//                                    |00000310| 32 35 34 63 34 3d 31 36 36 32 32 35 38 34 39 32 |254c4=1662258492|
//                                    |00000320| 0d 0a 0d 0a                                     |....            |
//                                    +--------+-------------------------------------------------+----------------+


                                }
                            });
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(8080).sync();
            channelFuture.channel().closeFuture().sync();
        }catch (Exception exception){
            exception.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
