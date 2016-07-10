package tss2.wiki.control;

import tss2.wiki.control.service.SessionService;
import tss2.wiki.domain.LoginResult;
import tss2.wiki.control.impl.SessionServiceimpl;
import tss2.wiki.control.impl.UserServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户模块控制程序。
 *
 * Created by 羊驼 on 2016/7/7.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public @ResponseBody LoginResult login(HttpServletRequest request, HttpServletResponse response) {
        UserServiceImpl userService = new UserServiceImpl();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        LoginResult lresult = userService.Login(username, password);
        if (lresult != null && lresult.isIncluded()) {
            Cookie cookie = new Cookie(username, new SessionServiceimpl().register(username).getSessionID());
            cookie.setMaxAge(SessionServiceimpl.DEFAULT_SESSION_LIFETIME * 60);
            response.addCookie(cookie);
        }
        return lresult;
    }

    @RequestMapping(value = "/login.do", method = RequestMethod.GET)
    public String login() {
        return "/html/Home.html";
    }
}
