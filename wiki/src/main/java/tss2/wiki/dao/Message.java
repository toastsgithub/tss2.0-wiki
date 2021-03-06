package tss2.wiki.dao;

import tss2.wiki.dao.core.DAOBase;
import tss2.wiki.model.WikiMessage;
import tss2.wiki.util.StringUtil;

/**
 * 消息表
 *
 * Created by coral on 16-7-12.
 */
public class Message extends DAOBase {

    public String messageID;        // 该消息的唯一标识id
    public String fromUser;         // 消息发送自某个用户的用户名
    public String toUser;           // 多用户，用户名之间用/隔开。其中#1或#0表示广播到管理员(1)或普通用户(0)
    public String title;            // 消息标题
    public String detail;           // 消息正文
    public int sent = 0;            // 是否已经被发送

    public static Message query() {
        return new Message();
    }

    public static void main(String[] args) {
        Message message = new Message();
        message.messageID = StringUtil.generateTokener(16);
        message.fromUser = "123";
        message.toUser = "dzm14";
        message.title = "段段段段段段狗";
        message.detail = "消息内容";
        message.save();

        WikiMessage wm = WikiMessage.getMessage(message.messageID);
        wm.send();
    }
}
