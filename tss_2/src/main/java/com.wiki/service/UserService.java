package com.wiki.service;

import com.wiki.domain.User;

/**
 * Created by Administrator on 2016/7/7.
 */
public interface UserService {
    public User Login(String userName,String password);
}
