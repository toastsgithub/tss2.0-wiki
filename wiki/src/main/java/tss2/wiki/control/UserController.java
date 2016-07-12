package tss2.wiki.control;

import tss2.wiki.control.service.SessionService;
import tss2.wiki.domain.LoginResult;
import tss2.wiki.control.impl.SessionServiceimpl;
import tss2.wiki.control.impl.UserServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tss2.wiki.domain.UserResult;
import tss2.wiki.model.WikiSession;
import tss2.wiki.model.WikiUser;

import javax.servlet.http.*;

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
            Cookie cookie = new Cookie("sessionID", new SessionServiceimpl().register(username).getSessionID());
            cookie.setMaxAge(SessionServiceimpl.DEFAULT_SESSION_LIFETIME * 60);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        return lresult;
    }

    @RequestMapping(value = "/login.do", method = RequestMethod.GET)
    public String login() {
        return "/html/Home.html";
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
    public @ResponseBody
    UserResult getUserInfo(HttpServletRequest request) {
        SessionService ss = new SessionServiceimpl();
        WikiSession session = ss.checkUser(request);
        if (session == null) {
            return new UserResult(0);
        }
        WikiUser user = new WikiUser(session.getUserID());
        return new UserResult(1, user.getUsername(), user.getType());
    }
}
