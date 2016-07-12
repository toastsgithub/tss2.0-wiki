package tss2.wiki.model;

import tss2.wiki.dao.DAOBase;
import tss2.wiki.dao.Message;
import tss2.wiki.domain.ResultMessage;
import tss2.wiki.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Wiki信息。
 *
 * Created by coral on 16-7-12.
 */
public class WikiMessage {

    /**
     * search a message accoring to a messageID
     *
     * @param messageID
     */
    public WikiMessage(String messageID) {
        DAOBase[] messages = Message.query().where("messageID = '" + messageID + "'");
        if (messages.length == 0) {
            dao = new Message();
            dao.messageID = "";
        }
        if (messages.length > 1) {
            for (int i = 0; i < messages.length - 1; i++) {
                messages[i].setValue("messageID", StringUtil.generateTokener(16));
                messages[i].save();
            }
        }
    }

    public String getMessageID() {
        return dao.messageID;
    }

    public String getFromUser() {
        return dao.fromUser;
    }

    public List<String> getToUserList() {
        ArrayList<String> userList = new ArrayList<>();
        String[] users = dao.toUser.split("/");
        for (String user: users) {
            if (user.equals("")) continue;
            userList.add(user);
        }
        return userList;
    }

    public String getTitle() {
        return dao.title;
    }

    public String getDetail() {
        return dao.detail;
    }

    public ResultMessage setContent(String fromUser, String[] toUser, String title, String detail) {
        dao.fromUser = fromUser;
        dao.toUser = StringUtil.concatArray("/", toUser);
        dao.title = title;
        dao.detail = detail;
        dao.read = false;
        dao.save();
        return new ResultMessage(0);
    }

    public boolean isRead() {
        return dao.read;
    }

    public void setRead(boolean read) {
        dao.read = read;
    }

    public void set(String messageID) {
        dao.messageID = messageID;
        dao.save();
    }

    private Message dao;
}
