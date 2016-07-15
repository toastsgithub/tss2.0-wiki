package tss2.wiki.dao;

import tss2.wiki.dao.core.DAOBase;

/**
 * Created by 羊驼 on 2016/7/10.
 */
public class Session extends DAOBase {
    public String username;
    public String sessionID;
    public String deadline;

    public static Session Query() {
        return new Session();
    }
}
