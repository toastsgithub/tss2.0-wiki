package tss2.wiki.model;

import tss2.wiki.control.impl.SessionServiceimpl;
import tss2.wiki.control.service.SessionService;
import tss2.wiki.dao.DAOBase;
import tss2.wiki.dao.Message;
import tss2.wiki.dao.User;

import java.util.ArrayList;

/**
 * Created by 羊驼 on 2016/7/10.
 */
public class WikiUser {

    private WikiUser() {
        messageList = new ArrayList<>();
    }

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
        int unreadMessage = 0;
        DAOBase[] messages = Message.query().where("");
        for (DAOBase message: messages) {
            WikiMessage wikiMessage = new WikiMessage((Message) message);
            for (String toUser: wikiMessage.getToUserList()) {
                if (toUser.equals(getUsername()) &&
                        getType() == 1 && toUser.equals("#1") &&
                        getType() == 0 && toUser.equals("#0")) {
                    messageList.add(wikiMessage);
                    if (!wikiMessage.isRead()) {
                        ++unreadMessage;
                    }
                }
            }
        }
        return messageList;
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

    private User dao;
    private ArrayList<WikiMessage> messageList;
}
