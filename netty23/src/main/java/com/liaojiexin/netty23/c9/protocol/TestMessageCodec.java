package com.liaojiexin.netty23.c9.protocol;

import com.liaojiexin.netty23.c9.message.LoginRequestMessage;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @ClassName TestMessageCodec
 * @Description TODO
 * @Author liao
 * @Date 2:42 下午 2023/1/21
 **/
public class TestMessageCodec {

    public static void main(String[] args) {
        EmbeddedChannel embeddedChannel=new EmbeddedChannel(
                new LoggingHandler(LogLevel.DEBUG),
                new MessageCodec()  //添加自定义的编码解码器
        );

        LoginRequestMessage loginRequestMessage = new LoginRequestMessage("zhangsan", "123456", "张三");

        embeddedChannel.writeOutbound(loginRequestMessage); //测试编码器
        /**
         *测试结果如下，可以看到前面16个字节就是请求信息(魔数、版本号、序列化算法、指令类型、请求序号、正文长度)，从第17个字节开始就是内容且长度为258
         * 14:50:50.077 [main] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xembedded, L:embedded - R:embedded] WRITE: 274B
         *          +-------------------------------------------------+
         *          |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
         * +--------+-------------------------------------------------+----------------+
         * |00000000| 01 02 03 04 01 00 00 00 00 00 00 ff 00 00 01 02 |................|
         * |00000010| ac ed 00 05 73 72 00 35 63 6f 6d 2e 6c 69 61 6f |....sr.5com.liao|
         * |00000020| 6a 69 65 78 69 6e 2e 6e 65 74 74 79 32 33 2e 63 |jiexin.netty23.c|
         * |00000030| 39 2e 6d 65 73 73 61 67 65 2e 4c 6f 67 69 6e 52 |9.message.LoginR|
         * |00000040| 65 71 75 65 73 74 4d 65 73 73 61 67 65 e4 1c 31 |equestMessage..1|
         * |00000050| d0 70 58 0e 47 02 00 03 4c 00 08 6e 69 63 6b 6e |.pX.G...L..nickn|
         * |00000060| 61 6d 65 74 00 12 4c 6a 61 76 61 2f 6c 61 6e 67 |amet..Ljava/lang|
         * |00000070| 2f 53 74 72 69 6e 67 3b 4c 00 08 70 61 73 73 77 |/String;L..passw|
         * |00000080| 6f 72 64 71 00 7e 00 01 4c 00 08 75 73 65 72 6e |ordq.~..L..usern|
         * |00000090| 61 6d 65 71 00 7e 00 01 78 72 00 29 63 6f 6d 2e |ameq.~..xr.)com.|
         * |000000a0| 6c 69 61 6f 6a 69 65 78 69 6e 2e 6e 65 74 74 79 |liaojiexin.netty|
         * |000000b0| 32 33 2e 63 39 2e 6d 65 73 73 61 67 65 2e 4d 65 |23.c9.message.Me|
         * |000000c0| 73 73 61 67 65 29 a5 e9 0e b3 af 1b bf 02 00 02 |ssage)..........|
         * |000000d0| 49 00 0b 6d 65 73 73 61 67 65 54 79 70 65 49 00 |I..messageTypeI.|
         * |000000e0| 0a 73 65 71 75 65 6e 63 65 49 64 78 70 00 00 00 |.sequenceIdxp...|
         * |000000f0| 00 00 00 00 00 74 00 06 e5 bc a0 e4 b8 89 74 00 |.....t........t.|
         * |00000100| 06 31 32 33 34 35 36 74 00 08 7a 68 61 6e 67 73 |.123456t..zhangs|
         * |00000110| 61 6e                                           |an              |
         * +--------+-------------------------------------------------+----------------+
         * 14:50:50.078 [main] DEBUG io.netty.handler.logging.LoggingHandler - [id: 0xembedded, L:embedded - R:embedded] FLUSH
         */
    }
}
