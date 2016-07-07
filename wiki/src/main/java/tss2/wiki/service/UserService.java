package tss2.wiki.service;

import tss2.wiki.domain.ResultLogin;

/**
 * Created by Administrator on 2016/7/7.
 */
public interface UserService {
    public ResultLogin Login(String userName, String password);
}
