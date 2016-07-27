package tss2.wiki.model;

import tss2.wiki.control.impl.SessionServiceimpl;
import tss2.wiki.control.service.SessionService;
import tss2.wiki.dao.Message;
import tss2.wiki.dao.User2Message;
import tss2.wiki.dao.core.DAOBase;
import tss2.wiki.dao.User;
import tss2.wiki.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * wiki用户类。
 * 注意权限区分不在这里！
 *
 * Created by 羊驼 on 2016/7/10.
 */
public class WikiUser {

    public static final int USER_ADMIN = 1;
    public static final int USER_STANDARD = 0;

    private WikiUser() { }

    public WikiUser(String username) {
        DAOBase[] users = User.query().where("username = '" + username + "'");
        if (users.length == 0) {
            dao = new User();
        }
        dao = (User) users[0];
    }

    public WikiSession getSession() {
        SessionService ss = new SessionServiceimpl();
        return ss.checkUser(getUsername());
    }

    public ArrayList<WikiMessage> loadMessages() {
        DAOBase[] ums = User2Message.query().where("toUser = '" + getUsername() + "'");
        ArrayList<WikiMessage> result = new ArrayList<>();
        for (DAOBase um: ums) {
            WikiMessage message = WikiMessage.getMessage(um.get("messageID").toString());
            if (message != null) result.add(message);
        }
        return result;
    }

    public String getUsername() {
        return dao.username;
    }

    public boolean checkPassword(String password) {
        return dao.password.equals(password);
    }

    public int getType() {
        return dao.type;
    }

    public void sendMessage(String user, String title, String detail) {
        String[] users = new String[1];
        users[0] = user;
        sendMessage(users, title, detail);
    }

    public void sendMessage(String[] users, String title, String detail) {
        String toUser = StringUtil.concatArray("/", users);
        WikiMessage message = new WikiMessage(getUsername(), toUser, title, detail);
        message.send();
    }

    public ArrayList<WikiMessage> getMessageList() {
        DAOBase[] ums = User2Message.query().where("toUser = '" + getUsername() + "'");
        ArrayList<WikiMessage> result = new ArrayList<>();
        DAOBase[] ms = Message.query().where("");
        for (DAOBase dao: ms) {
            for (DAOBase um: ums) {
                if (dao.get("messageID").toString().equals(um.get("messageID").toString())) {
                    result.add(new WikiMessage((Message) dao));
                }
            }
        }
        return result;
    }

    public WikiMessage getMessageDetail(WikiUser reader, String messageID) {
        WikiMessage message = WikiMessage.getMessage(messageID);
        if (message == null) return null;
        ArrayList<String> arrTo = new ArrayList<>();
        Collections.addAll(arrTo, message.getToUsers());
        if (arrTo.contains(reader.getUsername()) || arrTo.contains("#" + reader.getType()))
            return message;
        else return null;
    }

    private User dao;

    public static void main(String[] args) {
        WikiUser user = new WikiUser("dzm14");
        user.sendMessage("12", "测试消息", "恭喜您看到了这个测试消息");
    }
}
