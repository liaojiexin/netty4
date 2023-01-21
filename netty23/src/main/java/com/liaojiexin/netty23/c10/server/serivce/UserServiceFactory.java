package com.liaojiexin.netty23.c10.server.serivce;

public abstract class UserServiceFactory {

    private static UserService userService = new UserServiceMemoryImpl();

    /**
     * 获取用户服务
     * @return
     */
    public static UserService getUserService() {
        return userService;
    }
}
