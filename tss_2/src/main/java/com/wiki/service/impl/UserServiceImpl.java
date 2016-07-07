package com.wiki.service.impl;

import com.wiki.dao.DAOBase;
import com.wiki.dao.impl.User;
import com.wiki.domain.ResultLogin;
import com.wiki.service.UserService;

/**
 * Created by Administrator on 2016/7/7.
 */
public class UserServiceImpl implements UserService{

    @Override
    public ResultLogin Login(String userName, String password) {
        User userDAO = new User();
        DAOBase[] a = userDAO.query().where("username = '"+userName+"'");
        User temp = new User();
        ResultLogin resultLogin = new ResultLogin();
        if(a.length == 0){
            return null;
        }
        temp.username = (String.valueOf(a[0].get("username")));
        temp.password = (String.valueOf(a[0].get("password")));
        temp.type = (Integer.parseInt(String.valueOf(a[0].get("type"))));
        if(password.equals(temp.password)){
            resultLogin.setIncluded(true);
        }else{
            resultLogin.setIncluded(false);
        }
        resultLogin.setUser(temp);
        return resultLogin;
    }

    public static void main(String[] args){
        UserService userService = new UserServiceImpl();
        ResultLogin a = userService.Login("123","123");
        if(a==null){
            System.out.println("该用户不存在");
        }else if(a.isIncluded()) {
            System.out.println(a.getUser().password);
        }else {
            System.out.println("密码错误");
        }

    }
}
