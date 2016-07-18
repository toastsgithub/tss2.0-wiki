package tss2.wiki.dao;

import tss2.wiki.dao.core.DAOBase;

/**
 * Created by coral on 16-7-18.
 */
public class Reference extends DAOBase {
    public String title;
    public String alias;

    public static Reference query() {
        return new Reference();
    }
}
