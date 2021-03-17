package com.liaojiexin.VM.dao.impl;

import com.liaojiexin.VM.dao.UserDao;
import com.liaojiexin.VM.domai.User;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: UserDaoImpl
 * @Description: TODO
 * @version: 1.0
 * @author: liaojiexin
 * @date: 2021/3/17 15:06
 */
public class UserDaoImpl implements UserDao {

    List users=new ArrayList<>();

    //省略数据库连接，直接用假数据
    @Override
    public List<User> getAllUser(){
        User user1=new User(1,"a","aaa");
        User user2=new User(2,"b","bbb");
        User user3=new User(3,"c","ccc");
        User user4=new User(4,"d","ddd");
        User user5=new User(5,"e","eee");

        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);
        users.add(user5);

        return users;
    }

}
