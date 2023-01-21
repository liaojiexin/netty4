package com.liaojiexin.netty23.c10.server.session;

public abstract class GroupSessionFactory {

    private static GroupSession session = new GroupSessionMemoryImpl();

    /**
     * 获取组服务
     * @return
     */
    public static GroupSession getGroupSession() {
        return session;
    }
}
