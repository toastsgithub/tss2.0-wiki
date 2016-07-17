package tss2.wiki.control;

import tss2.wiki.control.service.SessionService;
import tss2.wiki.domain.CommonResult;
import tss2.wiki.domain.LoginResult;
import tss2.wiki.control.impl.SessionServiceimpl;
import tss2.wiki.control.impl.UserServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tss2.wiki.domain.MessageResult;
import tss2.wiki.domain.UserResult;
import tss2.wiki.model.WikiMessage;
import tss2.wiki.model.WikiSession;
import tss2.wiki.model.WikiUser;
import tss2.wiki.util.StringUtil;

import javax.servlet.http.*;
import java.util.ArrayList;
import java.util.Map;

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
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        LoginResult lresult = userService.Login(username, password);
        if (lresult != null && lresult.isIncluded()) {
            Cookie cookie = new Cookie("sessionID", new SessionServiceimpl().register(username).getSessionID());
            cookie.setMaxAge(5 * 60);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        return lresult;
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody UserResult getUserInfo(HttpServletRequest request) {
        WikiSession session = sessionService.checkUser(request);
        if (!session.isValid()) {
            return new UserResult(0);
        }
        WikiUser user = new WikiUser(session.getUserID());
        return new UserResult(1, user.getUsername(), user.getType());
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public @ResponseBody CommonResult logout(HttpServletRequest request) {
        WikiSession session = sessionService.checkUser(request);
        if (!session.isValid()) {
            return new CommonResult(1, "Already Logged Out");
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie: cookies) {
                if (cookie.getName().equals("sessionID")) {
                    cookie.setMaxAge(0);
                }
            }
        }
        session.disable();
        return new CommonResult(0);
    }

    private UserServiceImpl userService = new UserServiceImpl();
    private SessionService sessionService = new SessionServiceimpl();
}
