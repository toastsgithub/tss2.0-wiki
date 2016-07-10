package tss2.wiki.dao;

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
        // TODO: add version

        // set timestamp
        timestamp = TimeGenerator.getTimeStampBySecond();
        super.save();
    }

    public static void main(String[] args) {
        DAOBase[] content = Summary.query().where("");
        Summary s = (Summary) content[0];
        s.setValue("summaryJO", "被我改了");
        s.save();
    }
}