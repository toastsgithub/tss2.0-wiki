package tss2.wiki.control.impl;

import tss2.wiki.dao.core.DAOBase;
import tss2.wiki.dao.User;
import tss2.wiki.dao.WikiEntry;
import tss2.wiki.domain.LoginResult;
import tss2.wiki.control.service.UserService;

/**
 * Created by Administrator on 2016/7/7.
 */
public class UserServiceImpl implements UserService{

    @Override
    public LoginResult Login(String userName, String password) {
        User userDAO = new User();
        DAOBase[] a = userDAO.query().where("username = '"+userName+"'");
        User temp = new User();
        LoginResult resultLogin = new LoginResult();
        if(a.length == 0){
            resultLogin.setIncluded(false);
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
        resultLogin.setUser(temp.username, temp.type);
        return resultLogin;
    }

    public LoginResult testFuzzySearch(String userName, String password) {
        User userDAO = new User();
        DAOBase[] a = userDAO.query().where("username like '%"+userName+"%'");
        User temp = new User();
        LoginResult resultLogin = new LoginResult();
        if(a.length == 0){
            resultLogin.setIncluded(false);
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
        resultLogin.setUser(temp.username, temp.type);
        return resultLogin;
    }

    public void testAdd() {
        WikiEntry wikiEntryDAO = new WikiEntry();
        wikiEntryDAO.setValue("title","软件工程与计算1");
        wikiEntryDAO.setValue("tags","软件工程 java");
        wikiEntryDAO.setValue("content","刘钦");
//        wikiEntryDAO.save();
        DAOBase[] a = wikiEntryDAO.query().where("title like '%软件%'");
        System.out.println(String.valueOf(a[0].get("title")));
    }

    public static void main(String[] args){
        UserServiceImpl userService = new UserServiceImpl();
//        ResultLogin a = userService.testFuzzySearch("123","123");
//        if(a==null){
//            System.out.println("该用户不存在");
//        }else if(a.isIncluded()) {
//            System.out.println(a.getUser().password);
//        }else {
//            System.out.println("密码错误");
//        }
        userService.testAdd();
    }
}
