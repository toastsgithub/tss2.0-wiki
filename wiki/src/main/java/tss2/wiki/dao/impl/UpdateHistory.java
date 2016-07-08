package tss2.wiki.dao.impl;

import tss2.wiki.dao.DAOBase;

/**
 * Holding update envents of wiki entries.
 *
 * Created by 羊驼 on 2016/7/8.
 */
public class UpdateHistory extends DAOBase {
    public String title;
    public String username;
    public String timestamp;
    public String content;

    public static UpdateHistory query() {
        return new UpdateHistory();
    }
}
