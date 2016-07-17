package tss2.wiki.dao;

import tss2.wiki.dao.core.DAOBase;

/**
 * Created by 羊驼 on 2016/7/9.
 */
public class Tag extends DAOBase {

    public String tag;

    public Tag() {}

    public Tag(String tag) {
        this.tag = tag;
    }

    public static Tag query() {
        return new Tag();
    }
}
