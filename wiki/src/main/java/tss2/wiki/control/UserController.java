package tss2.wiki.control;

import tss2.wiki.domain.LoginResult;
import tss2.wiki.service.impl.UserServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by 羊驼 on 2016/7/7.
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public @ResponseBody LoginResult login(@RequestParam(value = "username", required = false) String username,
                      @RequestParam(value = "password", required = false) String password) {
        UserServiceImpl userService = new UserServiceImpl();
        return userService.Login(username, password);
    }

    @RequestMapping(value = "/login.do", method = RequestMethod.GET)
    public String login() {
        return "/html/Home.html";
    }
}
