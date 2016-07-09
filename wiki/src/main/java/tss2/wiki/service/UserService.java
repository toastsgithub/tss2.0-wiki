package tss2.wiki.service;

import tss2.wiki.domain.LoginResult;

/**
 * Created by Administrator on 2016/7/7.
 */
public interface UserService {
    public LoginResult Login(String userName, String password);
}
