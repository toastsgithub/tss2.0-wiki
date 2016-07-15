package tss2.wiki.dao;

import tss2.wiki.dao.core.DAOBase;
import tss2.wiki.util.TimeUtil;


/**
 *
 *
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
        // TODO: add version

        // set timestamp
        timestamp = TimeUtil.getTimeStampBySecond();
        super.save();
    }
}
