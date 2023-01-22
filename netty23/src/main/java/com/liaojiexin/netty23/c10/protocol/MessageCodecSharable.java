package com.liaojiexin.netty23.c10.protocol;

import com.liaojiexin.netty23.c10.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * @ClassName MessageCodecSharable
 * @Description TODO
 * @Author liao
 * @Date 5:58 下午 2023/1/21
 **/
@Slf4j
@ChannelHandler.Sharable //这里可以用
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {

    /**
     * 自定义协议的几个要素:
     * - 魔数:用来在第一时间判定是否是无效数据包,客户端和服务端协商好魔数，这样接收到消息后可以先检验是否是约定好的魔数如果不是就直接丢弃掉消息。
     * - 版本号:可以支持协议的升级，例如:Http1、Http1.1、Http2等。
     * - 序列化算法:消息正文到底采用哪种序列化反序列化方式(即编码解码器)，可以由此扩展，例如：json、 protobuf、hessian、 jdk
     * - 指令类型:指明消息类型，是登录、注册、单聊、群聊…跟业务相关等一些类型
     * - 请求序号:为了双工通信，提供异步能力,这和前面所说过的滑动窗口有关，因为一次性发送多个请求，所以请求和响应要带上序号才能互相对应。
     * - 正文长度:主要是为了解决黏包半包问题,当然还有其他用处，例如前面说了让浏览器知道消息长度防止一直请求等。
     * - 消息正文
     */

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        ByteBuf out=ctx.alloc().buffer();
        //1.4个字节长度的魔数,客户端和服务器端约定好
        out.writeBytes(new byte[]{1,2,3,4});
        //2.1个字节长度的版本
        out.writeByte(1);
        //3.1个字节长度的序列化方法。自己定义成0表示用jdk，1表示用json
        out.writeByte(0);   //这里暂时用jdk
        //4.1个字节长度表示指令类型
        out.writeByte(msg.getMessageType());
        //5.4个字节长度表示请求序号
        out.writeInt(msg.getSequenceId());
        //无意义，上面加起来全部15个字节，一般设置为2的整数倍，所以我们再加上一个字节做为填充
        out.writeByte(0xff);
        //6.获取内容的字节数组，序列化
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        ObjectOutputStream oos=new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] bytes=bos.toByteArray();
        //7.4个字节长度表示内容长度
        out.writeInt(bytes.length);
        //8.写入内容
        out.writeBytes(bytes);
        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //获取魔数
        int magicNum=in.readInt();
        //获取版本号
        byte version = in.readByte();
        //获取序列化方法
        byte serializerType = in.readByte();
        //获取指令类型
        byte messageType = in.readByte();
        //获取请求序号
        int sequenceId=in.readInt();
        //去掉无意义的填充字节
        in.readByte();
        //获取内容长度
        int length=in.readInt();
        //获取内容，反序列化
        byte[] bytes=new byte[length];
        in.readBytes(bytes,0,length);//读取in到bytes字节数组里面，从0开始读length长度
        ByteArrayInputStream bis=new ByteArrayInputStream(bytes);
        ObjectInputStream ois=new ObjectInputStream(bis);
        Message message = (Message) ois.readObject();
        //最后记得要把解码后的信息填到out这样才能传递给下个handler
        out.add(message);

        //打印日志
        log.debug("获取到解码后的信息[魔数:{},版本号:{},序列化方法:{},指令类型:{},请求序号:{},内容长度:{},内容:{}]",
                magicNum,version,serializerType,messageType,sequenceId,length,message);

    }
}
