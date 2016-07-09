package tss2.wiki.dao.impl;

import tss2.wiki.dao.DAOBase;
import tss2.wiki.db.DBAdmin;

/**
 * contains wiki entries.
 *
 * Created by 羊驼 on 2016/7/7.
 */
public class WikiEntry extends DAOBase {
    public String title;
    public String tags;
    public String contentPath;
    public String summery;
    public int visits;
    public int mainversion, subversion; // latest version number

    public WikiEntry query() {
        return new WikiEntry();
    }

    public static void main(String[] args) {
        DBAdmin.dropTable("WikiEntry");
        DBAdmin.createTable(WikiEntry.class);
        DBAdmin.dropTable("UpdateHistory");
        DBAdmin.createTable(UpdateHistory.class);
    }
}
