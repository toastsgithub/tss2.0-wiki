package com.wiki.dao;

import com.wiki.domain.User;

/**
 * Created by Administrator on 2016/7/7.
 */
public interface UserDao {
    public User find(String userName);
}
