package com.liaojiexin.VM.domai;

import java.util.List;

/**
 * @ClassName: Meeting
 * @Description: TODO
 * @version: 1.0
 * @author: liaojiexin
 * @date: 2021/3/16 16:06
 */
public class Meeting {

    //会议id，随机生成一个会议id
    private Integer mid;

    //会议参与人，把会议参与的人加入
    private List<User> users;

    public Meeting(Integer mid, List<User> users) {
        this.mid = mid;
        this.users = users;
    }

    public Integer getMid() {
        return mid;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
