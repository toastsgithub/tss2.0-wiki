package com.wiki.domain;

/**
 * Created by Administrator on 2016/7/7.
 */
public class User {
    private String useName;
    private String password;
    private int type;

    public String getPassword() {
        return password;
    }

    public String getUseName() {
        return useName;
    }

    public int getType() {
        return type;
    }

    public void setUseName(String useName) {
        this.useName = useName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setType(int type) {
        this.type = type;
    }
}
