package tss2.wiki.dao.impl;

import tss2.wiki.dao.DAOBase;

/**
 * Created by 羊驼 on 2016/7/9.
 */
public class Summary extends DAOBase {
    public String summaryJO;

    public static Summary query() {
        return new Summary();
    }
}
