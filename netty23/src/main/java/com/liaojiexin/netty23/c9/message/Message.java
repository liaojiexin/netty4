package com.liaojiexin.netty23.c9.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName Message
 * @Description TODO 消息抽象类
 * @Author liao
 * @Date 1:29 下午 2023/1/21
 **/
public abstract class Message implements Serializable {

    //获取类型
    public static Class<?> getMessageClass(int messageType){
        return messageClasses.get(messageType);
    }

    private int sequenceId; //请求序号

    private int messageType;    //指令类型

    public abstract int getMessageType();   //抽象方法获取到指令类型

    public int getSequenceId() {
        return sequenceId;
    }

    public static final int LoginRequestMessage=0;  //登录请求
    public static final int LoginResponseMessage=1; //登录响应
    public static final int ChatRequestMessage=2;   //发送消息请求
    public static final int ChatResponseMessage=3;  //发送消息响应
    public static final int GroupCreateRequestMessage=4;    //创建聊天室请求
    public static final int GroupCreateResponseMessage=5;   //创建聊天室响应
    public static final int GroupJoinRequestMessage=6;  //加入聊天室请求
    public static final int GroupJoinResponseMessage=7; //加入聊天室响应
    public static final int GroupQuitRequestMessage=8;  //退出聊天室请求
    public static final int GroupQuitResponseMessage=9; //退出聊天是响应
    public static final int GroupChatRequestMessage =10;    //聊天室聊天请求
    public static final int GroupChatResponseMessage=11;    //聊天室聊天响应
    public static final int GroupMembersRequestMessage=12;  //添加聊天室成员请求
    public static final int GroupMembersResponseMessage=13; //添加聊天室成员响应
    private  static final Map<Integer,Class<?>> messageClasses=new HashMap<>();

    static{}
}
