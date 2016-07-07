package tss2.wiki.dao.impl;

import tss2.wiki.dao.DAOBase;

/**
 * Created by 羊驼 on 2016/7/7.
 */
public class User extends DAOBase {
    public String username;
    public String password;
    public int type;

    public static User query() {
        return new User();
    }
}
