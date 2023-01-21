package com.liaojiexin.netty23.c9.message;

import lombok.Data;
import lombok.ToString;

/**
 * @ClassName LoginRequestMessage
 * @Description TODO
 * @Author liao
 * @Date 2:43 下午 2023/1/21
 **/
@Data
@ToString(callSuper = true)
public class LoginRequestMessage extends Message{
    @Override
    public int getMessageType() {
        return LoginRequestMessage;
    }

    private String username;    //账号
    private String password;    //密码
    private String nickname;    //昵称

    public LoginRequestMessage() {
    }

    public LoginRequestMessage(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }
}
