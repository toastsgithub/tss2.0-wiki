package tss2.wiki.dao.impl;

import tss2.wiki.dao.DAOBase;

/**
 * Created by 羊驼 on 2016/7/7.
 */
public class WikiEntry extends DAOBase {
    public String title;
    public String tags;
    public String content;

    public WikiEntry query() {
        return new WikiEntry();
    }

    public static void main(String[] args) {
        WikiEntry we = new WikiEntry();
        we.content = "123";
        we.tags = "123";
        we.title = "1234";
        we.save();
    }
}
