package tss2.wiki.model;

import tss2.wiki.dao.core.DAOBase;
import tss2.wiki.dao.Message;
import tss2.wiki.dao.User2Message;

/**
 * WikiMessage.
 *
 * Created by coral on 16-7-12.
 */
public class WikiMessage {

    public WikiMessage(String username) {
        DAOBase[] ms = Message.query().where("toUser like '%" + username + "%'");
    }

    private User2Message[] usermessages;
    private Message[] messages;
}
