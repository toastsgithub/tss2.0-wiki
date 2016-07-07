package com.wiki.dao.Impl;

import com.wiki.dao.DAOBase;

/**
 * Created by 羊驼 on 2016/7/7.
 */
public class WikiEntry extends DAOBase {
    public String title;
    public String tags;
    public String context;

    public WikiEntry query() {
        return new WikiEntry();
    }
}
