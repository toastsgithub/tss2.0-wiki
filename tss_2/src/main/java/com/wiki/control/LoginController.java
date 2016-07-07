package com.wiki.control;

import com.wiki.domain.ResultLogin;
import com.wiki.service.impl.UserServiceImpl;
import com.wiki.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by 羊驼 on 2016/7/7.
 */
@Controller
public class LoginController {
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public @ResponseBody ResultLogin login(@RequestParam(value = "username", required = false) String username,
                      @RequestParam(value = "password", required = false) String password) {
        UserService userService = new UserServiceImpl();
        return userService.Login(username, password);
    }

    @RequestMapping(value = "/hello")
    public String login(Model model) {
        model.addAttribute("greeting", "Hello Spring MVC");
        return "helloworld";
    }
}
