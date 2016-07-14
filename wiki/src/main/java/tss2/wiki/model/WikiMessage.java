package tss2.wiki.model;

import tss2.wiki.dao.DAOBase;
import tss2.wiki.dao.Message;
import tss2.wiki.domain.ResultMessage;
import tss2.wiki.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * WikiMessage.
 *
 * Created by coral on 16-7-12.
 */
public class WikiMessage {

    /**
     * search a message according to a messageID.
     * if not exists, create a new one, but will not
     * write an empty entry to the database.
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

    public WikiMessage(Message message) {
        this.dao = message;
    }

    public String getMessageID() {
        return dao.messageID;
    }

    public String getFromUser() {
        return dao.fromUser;
    }

    /**
     * Get the list of the target users.
     *
     * @return the list holding the users.
     */
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

    /**
     * set the content of the message. and save the changes
     * back to the database.
     *
     * @param fromUser which user the message come from
     * @param toUser to which user the message is sent to
     * @param title the title of the message
     * @param detail the content of the message.
     * @return a result message that always saying no error.
     */
    public ResultMessage setContent(String fromUser, String[] toUser, String title, String detail) {
        dao.fromUser = fromUser;
        dao.toUser = StringUtil.concatArray("/", toUser);
        dao.title = title;
        dao.detail = detail;
        dao.read = 0;
        dao.save();
        return new ResultMessage(0);
    }

    public int getRead() {
        return dao.read;
    }

    public void setRead(int read) {
        dao.read = read;
    }

    public void set(String messageID) {
        dao.messageID = messageID;
        dao.save();
    }

    private Message dao;
}
