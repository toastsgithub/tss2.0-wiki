package tss2.wiki.dao;

/**
 * Created by coral on 16-7-15.
 */
public class User2Message extends DAOBase {
    public String username;
    public String messageID;
    public int read;

    public static User2Message query() {
        return new User2Message();
    }
}
