package com.wiki.control;

import com.wiki.domain.ResultLogin;
import com.wiki.service.Impl.UserServiceImpl;
import com.wiki.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by 羊驼 on 2016/7/7.
 */
@Controller
@RequestMapping("/login")
public class LoginController {
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(@RequestParam(value = "username", required = false) String username,
                             @RequestParam(value = "password", required = false) String password) {
        UserService userService = new UserServiceImpl();
        return "123";
    }
}
