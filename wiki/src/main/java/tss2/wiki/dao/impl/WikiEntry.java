package tss2.wiki.dao.impl;

import tss2.wiki.dao.DAOBase;

/**
 * Created by 羊驼 on 2016/7/7.
 */
public class WikiEntry extends DAOBase {
    public String title;
    public String tags;
    public String content;
    public String lastUpdateDate;

    public WikiEntry query() {
        return new WikiEntry();
    }
}
