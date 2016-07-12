package tss2.wiki.control.service;

import tss2.wiki.domain.LoginResult;

/**
 * Created by Administrator on 2016/7/7.
 */
public interface UserService {

    /**
     * 登录
     *
     * @param userName 用户名
     * @param password 密码
     * @return 登录信息。
     */
    LoginResult Login(String userName, String password);
}
