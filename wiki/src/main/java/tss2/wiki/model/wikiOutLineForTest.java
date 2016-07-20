package tss2.wiki.model;

import tss2.wiki.util.FileUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/20.
 */
public class wikiOutLineForTest {

    static String path = "/home/coral/文档/Tencent Files/2419335621/FileRecv/OutLineTest.dat";

    private static void loadPath() {
        path = WikiOutline.class.getClassLoader().getResource("").getPath().replaceAll("[%][2][0]", " ") + "OutLineTest.dat";
    }

    public wikiOutLineForTest() {
        loadPath();
        Object result = FileUtil.loadObjectFromAbsolutePath(path);
        if (result == null) {
            System.err.println("fail in loading outline");
            FileUtil.writeObjectToAbsolutePath(path, map);
            return;
        }
        if (result instanceof Map) {
            System.out.println("load successfully");
            //System.out.println();
            map = (Map<String, ArrayList<Map>>) result;
        }
    }

    public Map getSummary() {
        return map;
    }


    public static void setMap(Map<String, ArrayList<Map>> map) {
        wikiOutLineForTest.map = map;
        FileUtil.writeObjectToAbsolutePath(path, map);
    }

    private static Map map = null;
}
