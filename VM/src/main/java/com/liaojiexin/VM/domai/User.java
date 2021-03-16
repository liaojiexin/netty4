package com.liaojiexin.VM.domai;

/**
 * @ClassName: User
 * @Description: TODO
 * @version: 1.0    用户
 * @author: liaojiexin
 * @date: 2021/3/16 16:04
 */
public class User {
    //用户id
    private Integer userId;

    //用户名
    private String username;

    //用户密码
    private String password;

    public User(Integer userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
