package tss2.wiki.util;

import java.util.List;
import java.util.Random;

/**
 * Created by 羊驼 on 2016/7/10.
 */
public class StringUtil {

    public static String concatArray(String connector, String[] array) {
        if (array == null) return null;
        String result = "";
        for (String s: array) {
            if (!result.equals("")) result += connector;
            result += s;
        }
        return result;
    }
    public static String concatArray(String connector, List<String> array) {
        if (array == null) return null;
        String result = "";
        for (String s: array) {
            if (!result.equals("")) result += connector;
            result += s;
        }
        return result;
    }

    public static String generateTokener(int length) {
        String template = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        String sessionid = "";
        Random rand = new Random();
        for (int i = 0; i < length; ++i) {
            sessionid += template.charAt(rand.nextInt(template.length()));
        }
        return sessionid;
    }
}
