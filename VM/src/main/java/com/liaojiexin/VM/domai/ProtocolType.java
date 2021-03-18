package com.liaojiexin.VM.domai;

/**
 * @ClassName: MessageTyep
 * @Description: TODO
 * @version: 1.0    定义协议类:UDP、websocket等
 * @author: liaojiexin
 * @date: 2021/3/18 11:11
 */
public enum  ProtocolType {

    Udp(0),WebSocket(1);

    int i;


    ProtocolType(int i) {
        this.i=i;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }
}
