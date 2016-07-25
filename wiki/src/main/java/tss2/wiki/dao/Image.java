package tss2.wiki.dao;

import tss2.wiki.dao.core.DAOBase;

/**
 * Created by coral on 16-7-24.
 */
public class Image extends DAOBase {
    public String path;
    public String format;
    public String imageId;

    public static Image query() {
        return new Image();
    }
}
