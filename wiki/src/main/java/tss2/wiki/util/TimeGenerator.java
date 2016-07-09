package tss2.wiki.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Auto generate timestamp string by millis or by date.
 * Provide fast tranformation from date to string and
 * string to date.
 * Created by 羊驼 on 2016/7/9.
 */
public class TimeGenerator {

    public static String getTimeStampByMillis() {
        Date curr = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(curr);
    }

    public static String getTimeStampByDate() {
        Date curr = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(curr);
    }
}
