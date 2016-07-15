package tss2.wiki.dao;

import tss2.wiki.dao.core.DAOBase;

/**
 * Created by 羊驼 on 2016/7/9.
 */
public class Tag extends DAOBase {

    public Tag() {}

    public Tag(String tag) {
        this.tag = tag;
    }

    public String tag;

    public static Tag query() {
        return new Tag();
    }

    public static void main(String[] args) {
        new Tag("段段").save();
        new Tag("诚诚").save();
        new Tag("考拉芝").save();
        new Tag("王奔").save();
    }
}
