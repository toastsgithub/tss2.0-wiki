package tss2.wiki.dao;

import tss2.wiki.dao.core.DAOBase;
import tss2.wiki.util.StringUtil;

/**
 * Created by coral on 16-7-12.
 */
public class Message extends DAOBase {

    public String messageID;        // 该消息的唯一标识id
    public String fromUser;         // 消息发送自某个用户的用户名
    public String toUser;           // 多用户，用户名之间用/隔开。其中#1或#0表示广播到管理员(1)或普通用户(0)
    public String title;            // 消息标题
    public String detail;           // 消息正文

    public static Message query() {
        return new Message();
    }
}
