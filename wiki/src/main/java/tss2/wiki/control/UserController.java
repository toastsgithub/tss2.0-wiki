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

    private UserServiceImpl userService = new UserServiceImpl();
    private SessionService sessionService = new SessionServiceimpl();

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

    @RequestMapping(value = "/message", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody MessageResult getUserMessage(HttpServletRequest request) {
        // TODO
        return null;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public void logout(HttpServletRequest request) {
        WikiSession session = sessionService.checkUser(request);
        if (!session.isValid()) {
            return;
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
    }

    @RequestMapping(value = "/message/{messageID}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody MessageResult readMessage(HttpServletRequest request, @PathVariable String messageID) {
        WikiSession session = sessionService.checkUser(request);
        if (!session.isValid()) {
            return new MessageResult(1);
        }
        WikiUser user = session.getUser();
        WikiMessage message = user.getMessageDetail(messageID);
        if (message == null) {
            return new MessageResult(1);
        }
        return new MessageResult(0, message.getFromUser(), message.getTitle(), message.getDetail());
    }

    /**
     * RequestBody:
     * {
     *     to: ['user1', 'user2', '#1', ...],
     *     title: 'message title',
     *     detail: 'message content'
     * }
     */
    @RequestMapping(value = "/message", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody CommonResult sendMessage(HttpServletRequest request, @RequestBody Map map) {
        WikiSession session = sessionService.checkUser(request);
        if (!session.isValid()) {
            return new CommonResult(1, "Authentication Failed");
        }
        WikiUser user = session.getUser();
        String fromUser = user.getUsername();
        String title = map.get("title").toString();
        String detail = map.get("detail").toString();
        String toUser = StringUtil.concatArray("/", (ArrayList) map.get("toUser"));
        WikiMessage message = new WikiMessage(fromUser, toUser, title, detail);
        message.send();
        return new CommonResult(0);
    }
}
