package tss2.wiki.dao;

/**
 * Holding update events of wiki entries.
 *
 * Created by 羊驼 on 2016/7/8.
 */
public class UpdateHistory extends DAOBase {
    public String title;
    public String username;
    public String timestamp;
    public String summary;
    public String categories;
    public String contentPath;
    public String tags;
    public int mainversion, subversion;

    public static UpdateHistory query() {
        return new UpdateHistory();
    }
}
