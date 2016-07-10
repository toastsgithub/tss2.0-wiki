package tss2.wiki.dao;

/**
 * contains wiki entries.
 *
 * Created by 羊驼 on 2016/7/7.
 */
public class WikiEntry extends DAOBase {
    public String title;
    public String tags;
    public String contentPath;
    public String categories;
    public String summery;
    public int visits = 0;
    public int mainversion, subversion; // latest version number

    public static WikiEntry query() {
        return new WikiEntry();
    }
}
