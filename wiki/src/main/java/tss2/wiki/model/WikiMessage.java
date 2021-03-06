package tss2.wiki.model;

import tss2.wiki.dao.User;
import tss2.wiki.dao.core.DAOBase;
import tss2.wiki.dao.Message;
import tss2.wiki.dao.User2Message;
import tss2.wiki.util.StringUtil;

import java.util.ArrayList;

/**
 * 消息。负责消息的分发。
 *
 * Created by coral on 16-7-12.
 */
public class WikiMessage {

    private int isread = 0;

    private WikiMessage() { }

    public static WikiMessage getMessage(String messageID) {
        DAOBase[] ms = Message.query().where("messageID = '" + messageID + "'");
        if (ms.length == 0) return null;
        else {
            WikiMessage result = new WikiMessage();
            result.dao = (Message) ms[0];
            return result;
        }
    }

    public WikiMessage(Message message) {
        dao = message;
    }

    public WikiMessage(String fromUser, String toUser, String title, String detail) {
        dao = new Message();
        dao.messageID = StringUtil.generateTokener(16);
        dao.fromUser = fromUser;
        dao.toUser = toUser;
        dao.title = title;
        dao.detail = detail;
        dao.save();
    }

    public void setIsread(int isread) {
        this.isread = isread;
    }

    public int getIsread() {
        return isread;
    }

    public int getIsread(String toUser) {
        DAOBase[] ums = User2Message.query().where(String.format("messageID = '%s' and toUser = '%s'", getMessageID(), toUser));
        for (DAOBase um: ums) {
            return ((User2Message) um).isread;
        }
        return 0;
    }

    public static void main(String[] args) {
        WikiMessage message = new WikiMessage("123", "#1", "123", "123123123");
        message.send();
    }

    public void send() {
        if (dao.sent == 1) return;
        for (String to : dao.toUser.split("[/]")) {
            if (to.startsWith("#")) {
                DAOBase[] users = User.query().where("");
                for (DAOBase user: users) {
                    if (("#" + ((User) user).type).equals(to)) {
                        User2Message um = new User2Message();
                        um.fromUser = dao.fromUser;
                        um.messageID = dao.messageID;
                        um.toUser = ((User) user).username;
                        um.isread = 0;
                        um.save();
                    }
                }
                continue;
            }
            User2Message um = new User2Message();
            um.fromUser = dao.fromUser;
            um.messageID = dao.messageID;
            um.toUser = to;
            um.isread = 0;
            um.save();
        }
        DAOBase[] ms = Message.query().where("messageID = '" + dao.messageID + "'");

        Message m = (Message) ms[0];
        m.sent = 1;
        m.save();
    }

    public void setIsread(String toUser) {
        DAOBase[] ums = User2Message.query().where(String.format("messageID = '%s' and toUser = '%s'", getMessageID(), toUser));
        for (DAOBase um: ums) {
            ((User2Message) um).isread = 1;
            um.save();
        }
        isread = 1;
    }

    public String[] getToUsers() {
        return dao.toUser.split("[/]");
    }

    public String getTimestamp() {
        return dao.timestamp;
    }

    public String getTitle() {
        return dao.title;
    }

    public String getMessageID() {
        return dao.messageID;
    }

    public String getFromUser() {
        return dao.fromUser;
    }

    public void setTitle(String title) {
        dao.title = title;
        dao.save();
    }

    public void setFromUser(String fromUser) {
        dao.fromUser = fromUser;
        dao.save();
    }

    public void setDetail(String detail) {
        dao.detail = detail;
        dao.save();
    }

    public String getDetail() {
        return dao.detail;
    }

    private Message dao;
}
