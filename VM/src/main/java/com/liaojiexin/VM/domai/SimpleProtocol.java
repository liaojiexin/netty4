package com.liaojiexin.VM.domai;

/**
 * @Author liaojiexin
 * @Description 自定义协议
 * @Date 2021/3/18 15:11
 * @Param
 * @return
 **/
public class SimpleProtocol {
    /**
     * 协议类型
     */
    private byte protocolType;
    /**
     * 消息体长度
     */
    private int bodyLength;
    /**
     * 消息内容
     */
    private byte[] body;

    public byte getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(byte protocolType) {
        this.protocolType = protocolType;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}