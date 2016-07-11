package tss2.wiki.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Auto generate timestamp string by millis or by date.
 * Provide fast tranformation from date to string and
 * string to date.
 * Created by 羊驼 on 2016/7/9.
 */
public class TimeUtil {

    public static String getTimeStampBySecond() {
        Date curr = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(curr);
    }

    public static String getTimeStampByDate() {
        Date curr = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(curr);
    }

    public static Date parseDateBySecond(String time) {
        return parseDate(time, "yyyy-MM-dd HH:mm:ss");
    }

    public static Date parseDateByDay(String time) {
        return parseDate(time, "yyyy-MM-dd");
    }

    public static Date parseDate(String time, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat("format");
        try {
            return formatter.parse(time);
        } catch (ParseException e) {
            System.err.println("TimeUtil: invalid date format");
            return null;
        }
    }

    public static Date getDelayedDate(int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }
}
