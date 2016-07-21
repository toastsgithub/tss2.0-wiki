package tss2.wiki.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tss2.wiki.dao.core.DAOBase;

/**
 * Created by coral on 16-7-18.
 */
@JsonIgnoreProperties(value = {"getId","getTableName"})
public class Reference extends DAOBase {
    public String title;
    public String name;
    public String url;
    public String websiteName;
    public String timestamp;
    public long entryid;

    public String getTableName() {
        return super.getTableName();
    }

    public long getId() {
        return 0L;
    }

    public static Reference query() {
        return new Reference();
    }
}
