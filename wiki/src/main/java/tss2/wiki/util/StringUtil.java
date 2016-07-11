package tss2.wiki.util;

import java.util.List;

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
}
