package tss2.wiki.dao.impl;

import tss2.wiki.dao.DAOBase;
import tss2.wiki.db.DBAdmin;
import tss2.wiki.util.TimeGenerator;


/**
 * Created by 羊驼 on 2016/7/9.
 */
public class Summary extends DAOBase {
    public String summaryJO;
    public String timestamp;
    public int version = 0;

    public static Summary query() {
        return new Summary();
    }

    @Override
    public void save() {
        // add version

        // set timestamp
        timestamp = TimeGenerator.getTimeStampByMillis();
        super.save();
    }
}
