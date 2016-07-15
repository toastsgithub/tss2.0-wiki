package tss2.wiki.dao;

import tss2.wiki.dao.core.DAOBase;

/**
 * Created by coral on 16-7-15.
 */
public class User2Message extends DAOBase {
    public String fromUser;
    public String toUser;
    public String messageID;
    public int isread;

    public static User2Message query() {
        return new User2Message();
    }
}
