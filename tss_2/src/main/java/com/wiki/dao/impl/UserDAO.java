package com.wiki.dao.impl;

import com.wiki.dao.DAOBase;

/**
 * Created by 羊驼 on 2016/7/7.
 */
public class UserDAO extends DAOBase {
    public String username;
    public String password;
    public int type;

    public static UserDAO query() {
        return new UserDAO();
    }
}
