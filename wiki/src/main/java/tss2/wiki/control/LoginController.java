package tss2.wiki.control;

import tss2.wiki.domain.ResultLogin;
import tss2.wiki.service.impl.UserServiceImpl;
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
@RequestMapping("/user")
public class LoginController {
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public @ResponseBody ResultLogin login(@RequestParam(value = "username", required = false) String username,
                      @RequestParam(value = "password", required = false) String password) {
        UserServiceImpl userService = new UserServiceImpl();
        return userService.Login(username, password);
    }
}
