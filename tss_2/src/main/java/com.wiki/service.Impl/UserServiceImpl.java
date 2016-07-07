package com.wiki.service.Impl;

import com.wiki.dao.Impl.UserDaoImpl;
import com.wiki.dao.UserDao;
import com.wiki.domain.User;
import com.wiki.service.UserService;

/**
 * Created by Administrator on 2016/7/7.
 */
public class UserServiceImpl implements UserService{
    UserDao user;
    public User Login(String userName, String password) {
        user = new UserDaoImpl();
        User temp = user.find(userName);
        if(temp == null){
            return null;
        }
        if(temp.getPassword()==password){
            return temp;
        }
        return null;
    }
}
