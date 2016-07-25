package tss2.wiki.dao;

import tss2.wiki.dao.core.DAOBase;

/**
 * 用于保存待审核的文章信息。
 *
 * Created by coral on 16-7-24.
 */
public class Verifying extends DAOBase {
    public String username;
    public long wikiId;
    public String title;
    public String tags;
    public String categories;
    public String contentPath;
    public String alias;
    public int refused;

    public static Verifying query() {
        return new Verifying();
    }
}
