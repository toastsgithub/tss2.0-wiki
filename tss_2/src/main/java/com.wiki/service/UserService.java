package com.wiki.service;

import com.wiki.domain.ResultLogin;

/**
 * Created by Administrator on 2016/7/7.
 */
public interface UserService {
    public ResultLogin Login(String userName, String password);
}
