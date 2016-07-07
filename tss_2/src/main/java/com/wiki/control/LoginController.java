package com.wiki.control;

import com.wiki.domain.ResultLogin;
import com.wiki.service.impl.UserServiceImpl;
import com.wiki.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by 羊驼 on 2016/7/7.
 */
@Controller
@RequestMapping("/mvc")
public class LoginController {
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ResultLogin login(@RequestParam(value = "username", required = false) String username,
                        @RequestParam(value = "password", required = false) String password) {
        UserService userService = new UserServiceImpl();
        return new UserServiceImpl().Login(username, password);
    }
}
