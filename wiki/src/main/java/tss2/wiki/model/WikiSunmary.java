package tss2.wiki.model;

import tss2.wiki.dao.DAOBase;
import tss2.wiki.dao.Summary;
import tss2.wiki.util.FileUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/10.
 * add(),delete(),modify()的实现均基于search（）方法的基本思路
 */
public class WikiSunmary {

    static String path = "/Users/duanzhengmou/Desktop/temp_data/summary.dat";

    public WikiSunmary() {
        Object result = FileUtil.loadObjectFromAbsolutePath(path);
        if (result == null) {
            map = create();
            FileUtil.writeObjectToAbsolutePath(path, map);
            return;
        }
        if (result instanceof Map) {
            map = (Map<String, ArrayList<Map>>) result;
        }
    }

    public Map getSummary() {
        return map;
    }

    /**
     * add(),delete(),modify()的基本遍历算法
     * @param m
     */
    public void search(Map m) {
        String k = m.keySet().toString().substring(1, m.keySet().toString().length() - 1); //k = key
        System.out.println(k);
        ArrayList<Map> a = (ArrayList<Map>) m.get(k);
        //System.out.println(a.length);
        for (int i = 0; i < a.size(); i++) {    //遍历m.value
            k = a.get(i).keySet().toString().substring(1, a.get(i).keySet().toString().length() - 1); //k = key
            this.search(a.get(i));  //m.value内的每一项均为Map<String, ArrayList<Map>>结构，递归遍历

        }
    }

    /**
     * @param father    要增加节点的父节点
     * @param addString 要增加的节点
     */
    public void add(String father, String addString) {
        this.add(map, father, addString);
        FileUtil.writeObjectToAbsolutePath(path, map);
    }

    /**
     * @param father 要删除节点的父节点
     * @param delete 要删除的节点
     */
    public void delete(String father, String delete) {
        this.delete(map, father, delete);
        FileUtil.writeObjectToAbsolutePath(path, map);
    }

    /**
     * @param father 要修改节点的父节点
     * @param before 修改前节点
     * @param after  修改后节点
     */
    public void modify(String father, String before, String after) {
        this.modify(map, father, before, after);
        FileUtil.writeObjectToAbsolutePath(path,map);
    }

    //递归思路参考this.search()
    private void add(Map map, String father, String addString) {
        String k = map.keySet().toString().substring(1, map.keySet().toString().length() - 1); //k = key
        //System.out.println(k);
        ArrayList<Map> a = (ArrayList<Map>) map.get(k);
        if (k.equals(father)) {
            Map<String, ArrayList<Map>> add = new HashMap(); //在a中添加结构为Map<String, ArrayList<Map>> 的叶节点add，add的value为空的ArrayList<Map>
            ArrayList<Map> tempArr = new ArrayList<Map>();
            add.put(addString, tempArr);
            a.add(add);
        }
        for (int i = 0; i < a.size(); i++) {
            k = a.get(i).keySet().toString().substring(1, a.get(i).keySet().toString().length() - 1); //k = key
                if (k.equals(father)) {
                    //System.out.println(k);
                    Map<String, ArrayList<Map>> add = (Map<String, ArrayList<Map>>) a.get(i);
                    ArrayList<Map> tempArr = add.get(k);
                    Map<String, ArrayList<Map>> e = new HashMap(); //在tempArr中添加结构为Map<String, ArrayList<Map>> e，e的value为空的ArrayList<Map>
                    ArrayList<Map> valueArr = new ArrayList<Map>();
                    e.put(addString, valueArr);
                    tempArr.add(e);
                    break;
                }
                this.add(a.get(i), father, addString);
        }
    }

    private void delete(Map map, String father, String delete) {
        String k = map.keySet().toString().substring(1, map.keySet().toString().length() - 1); //k = key
        System.out.println(k);
        ArrayList<Map> a = (ArrayList<Map>) map.get(k);
        if (k.equals(father)) {
            //遍历a，删除a中的delete元素
            for (int l = 0; l < a.size(); l++) {
                String every = a.get(l).keySet().toString().substring(1, a.get(l).keySet().toString().length() - 1);
                if (every.equals(delete)) {
                    a.remove(l);
                }
            }
        }
        for (int i = 0; i < a.size(); i++) {
            k = a.get(i).keySet().toString().substring(1, a.get(i).keySet().toString().length() - 1); //k = key
                if (k.equals(father)) {
                    System.out.println(k);
                    Map<String, ArrayList<Map>> add = (Map<String, ArrayList<Map>>) a.get(i);
                    ArrayList<Map> tempArr = add.get(k);
                    //遍历tempArr，删除tempArr中的delete元素
                    for (int l = 0; l < tempArr.size(); l++) {
                        String every = tempArr.get(l).keySet().toString().substring(1, tempArr.get(l).keySet().toString().length() - 1);
                        if (every.equals(delete)) {
                            tempArr.remove(l);
                        }
                    }
                    break;
                }
                this.delete(a.get(i), father, delete);
        }
    }

    private void modify(Map map, String father, String before, String after) {
        String k = map.keySet().toString().substring(1, map.keySet().toString().length() - 1); //k = key
        System.out.println(k);
        ArrayList<Map> a = (ArrayList<Map>) map.get(k);
        if (k.equals(father)) {
            //遍历a，将a中的before改为after
            for (int l = 0; l < a.size(); l++) {
                String every = a.get(l).keySet().toString().substring(1, a.get(l).keySet().toString().length() - 1); //k = key
                if (every.equals(before)) {
                    Map<String, ArrayList<Map>> add = new HashMap(); //创建after的map
                    ArrayList<Map> tempArr = new ArrayList<Map>();
                    add = a.get(l); //将after对应到before
                    tempArr = add.get(every); //取出before的value
                    add.remove(before);
                    add.put(after, tempArr); //将before的value的after绑定
                    a.remove(l); //删除a中的before map
                    a.add(l, add); //再a中添加添加after map
                    break;
                }
            }
        }
        for (int i = 0; i < a.size(); i++) {
            k = a.get(i).keySet().toString().substring(1, a.get(i).keySet().toString().length() - 1);
                if (k.equals(father)) {
                    System.out.println(k);
                    Map<String, ArrayList<Map>> add = (Map<String, ArrayList<Map>>) a.get(i);
                    ArrayList<Map> tempArr = add.get(k);
                    //遍历tempArr，将tempArr中的before改为after
                    for (int l = 0; l < tempArr.size(); l++) {
                        String every = tempArr.get(l).keySet().toString().substring(1, tempArr.get(l).keySet().toString().length() - 1);
                        if (every.equals(before)) {
                            int aaa; //防止代码重复的声明，忽略这句话
                            Map<String, ArrayList<Map>> addM = new HashMap();
                            ArrayList<Map> Arr = new ArrayList<Map>();
                            addM = tempArr.get(l);
                            Arr = addM.get(every);
                            addM.remove(before);
                            addM.put(after, Arr);
                            tempArr.remove(l);
                            tempArr.add(l, addM);
                            break;
                        }
                    }
                    break;
                }
                this.modify(a.get(i), father, before, after);
        }
    }

    public static void setMap(Map<String, ArrayList<Map>> map) {
        WikiSunmary.map = map;
        FileUtil.writeObjectToAbsolutePath(path, map);
    }

    /**
     * 创建初始测试用大纲，存储格式为Map<String, ArrayList<Map>>
     * （叶节点也为Map<String, ArrayList<Map>>格式，其value为空）
     * @return
     */
    public Map create() {
        Map<String, ArrayList<Map>> result = new HashMap();
        Map<String, ArrayList<Map>> a = new HashMap();
        Map<String, ArrayList<Map>> b = new HashMap();

        Map<String, ArrayList<Map>> e = new HashMap();
        ArrayList<Map> eArr = new ArrayList<Map>();
        e.put("段段", eArr);
        ArrayList<Map> aArr = new ArrayList<Map>();
        aArr.add(e);
        a.put("一班", aArr);

        ArrayList<Map> bArr = new ArrayList<Map>();
        Map<String, ArrayList<Map>> c = new HashMap();
        Map<String, ArrayList<Map>> d = new HashMap();
        Map<String, ArrayList<Map>> f = new HashMap();
        Map<String, ArrayList<Map>> g = new HashMap();
        ArrayList<Map> cArr = new ArrayList<Map>();
        ArrayList<Map> dArr = new ArrayList<Map>();
        ArrayList<Map> fArr = new ArrayList<Map>();
        ArrayList<Map> gArr = new ArrayList<Map>();
        f.put("考拉爸爸", fArr);
        g.put("儿子", gArr);
        cArr.add(f);
        c.put("考拉", cArr);
        dArr.add(g);
        d.put("浩浩", dArr);
        bArr.add(c);
        bArr.add(d);
        b.put("二班", bArr);
        ArrayList<Map> ruanjian = new ArrayList<Map>();
        ruanjian.add(a);
        ruanjian.add(b);
        result.put("软件", ruanjian);
        return result;
    }

    public static void main(String[] args) {
        WikiSunmary c = new WikiSunmary();
        System.out.println(map);
        c.add("一班", "段段");
        System.out.println("-----");
        System.out.println(map);
    }

    private static Map map = null;
}