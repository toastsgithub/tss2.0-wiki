package tss2.wiki.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tss2.wiki.control.impl.SessionServiceimpl;
import tss2.wiki.control.impl.UserServiceImpl;
import tss2.wiki.control.service.SessionService;
import tss2.wiki.domain.CommonResult;
import tss2.wiki.domain.MessageListResult;
import tss2.wiki.domain.MessageResult;
import tss2.wiki.model.WikiAdministrator;
import tss2.wiki.model.WikiMessage;
import tss2.wiki.model.WikiSession;
import tss2.wiki.model.WikiUser;
import tss2.wiki.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by coral on 16-7-17.
 */
@Controller
@RequestMapping(value = "/message")
public class MessageController {

    /**
     * 获取所有消息列表
     * @param request
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody MessageListResult getUserMessage(HttpServletRequest request) {
        WikiSession session = sessionService.checkUser(request);
        if (!session.isValid()) {
            return new MessageListResult(1, "Authentication Failed");
        }
        WikiUser user = session.getUser();
        ArrayList<WikiMessage> messages = user.getMessageList();
        MessageListResult result = new MessageListResult(0);
        for (WikiMessage message: messages) {
            result.addMessage(message.getMessageID(), message.getFromUser(), message.getTitle(), message.getDetail(), message.getTimestamp(), message.getIsread());
        }
        return result;
    }

    @RequestMapping(value = "/{messageID}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody MessageResult readMessage(HttpServletRequest request, @PathVariable String messageID) {
        WikiSession session = sessionService.checkUser(request);
        if (!session.isValid()) {
            return new MessageResult(1);
        }
        WikiUser user = session.getUser();
        WikiMessage message = user.getMessageDetail(user, messageID);
        if (message == null) {
            return new MessageResult(1);
        }
        message.setIsread(user.getUsername());
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
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody CommonResult sendMessage(HttpServletRequest request, @RequestBody Map map) {
        WikiSession session = sessionService.checkUser(request);
        if (!session.isValid()) {
            return new CommonResult(1, "Authentication Failed");
        }
        WikiUser user = session.getUser();
        String title = map.get("title").toString();
        String detail = map.get("detail").toString();
        String toUser = StringUtil.concatArray("/", (ArrayList) map.get("toUser"));
        user.sendMessage(toUser.split("/"), title, detail);
        return new CommonResult(0);
    }

    @RequestMapping(value = "/wiki", method = RequestMethod.GET)
    public @ResponseBody void requestAddition(HttpServletRequest request, @RequestParam(value = "title", required = true) String title) {
        String username;
        WikiSession session = sessionService.checkUser(request);
        if (!session.isValid()) {
            username = "匿名用户";
        }
        WikiUser user = session.getUser();
        username = " 用户" + user.getUsername() + " ";
        WikiMessage message = new WikiMessage(user.getUsername(), "#1", username + "请求增加条目<em>" + title + "</em>",
                String.format("%s请求增加条目“%s”，<a href=\"/html/New_entry_editor.html?title=%s\">点击这里</a>增加这个条目。" +
                        "或者<a>点击这里</a>通知这个用户词条已经被创建。", username, title, title));
        message.send();
    }

    @RequestMapping(value = "send", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody CommonResult send(HttpServletRequest request, @RequestParam(value = "to") String to,@RequestParam(value = "message") String message,@RequestParam(value = "id") long id) {
        WikiSession session = sessionService.checkUser(request);
        if (!session.isValid()) {
            return new CommonResult(1, "Authentication Failed");
        }
        WikiUser user = session.getUser();
        WikiAdministrator wikiAdministrator = new WikiAdministrator();
        wikiAdministrator.reject(user.getUsername(),id);

        WikiMessage wikiMessage = new WikiMessage(user.getUsername(),to,"管理员"+user.getUsername()+"对您修改的回复",message);
        wikiMessage.send();
        return new  CommonResult(0);
    }


    private UserServiceImpl userService = new UserServiceImpl();
    private SessionService sessionService = new SessionServiceimpl();
}
