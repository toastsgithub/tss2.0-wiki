package tss2.wiki.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tss2.wiki.dao.core.DAOBase;

/**
 * Created by coral on 16-7-18.
 */
@JsonIgnoreProperties(value = {"id","tableName"})
public class Reference extends DAOBase {
    public long id;
    public String tableName;
    public String title;
    public String name;
    public String url;
    public String websiteName;
    public String timestamp;

    public static Reference query() {
        return new Reference();
    }
}
