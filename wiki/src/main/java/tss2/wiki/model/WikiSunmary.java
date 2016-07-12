package tss2.wiki.model;

import tss2.wiki.dao.DAOBase;
import tss2.wiki.dao.Summary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/10.
 */
public class WikiSunmary {
    public String getSummary(){
        DAOBase[] content = Summary.query().where("");
        String jsonString = content[0].get("summaryJO").toString();
        return jsonString;
    }

    public void setSummary(String jsonString){
        DAOBase[] content = Summary.query().where("");
        content[0].setValue("summaryJO",jsonString);
        content[0].save();
    }

    public void search(Map m){
        String k = m.keySet().toString().substring(1, m.keySet().toString().length()-1);
        System.out.println(k);
        ArrayList<Map> a = (ArrayList<Map>) m.get(k);
        //System.out.println(a.length);
        for(int i = 0; i<a.size(); i++){
            k = a.get(i).keySet().toString().substring(1, a.get(i).keySet().toString().length()-1);
            if(a.get(i).get(k)!=null){
                this.search(a.get(i));
            }else{
                System.out.println(k);
                System.out.println(a.get(i).get(k));
            }
        }
    }

    public void add(Map map,String father,String addString){
        String k = map.keySet().toString().substring(1, map.keySet().toString().length()-1);
        System.out.println(k);
        ArrayList<Map> a = (ArrayList<Map>) map.get(k);
        if(k.equals(father)){
            Map<String,ArrayList<Map>> add = new HashMap();
            ArrayList<Map> tempArr = new ArrayList<Map>();
            //tempArr = null;
            add.put(addString, tempArr);
            a.add(add);
        }
        //System.out.println(a.length);
        for(int i = 0; i<a.size(); i++){
            k = a.get(i).keySet().toString().substring(1, a.get(i).keySet().toString().length()-1);
            if(a.get(i).get(k)!=null){
                if(k.equals(father)){
                    System.out.println(k);
                    Map<String,ArrayList<Map>> add = (Map<String, ArrayList<Map>>) a.get(i);
                    ArrayList<Map> tempArr = add.get(k);
                    Map<String,ArrayList<Map>> e = new HashMap();
                    ArrayList<Map> valueArr = new ArrayList<Map>();
                    e.put(addString, valueArr);
                    tempArr.add(e);
                    //add.put(addString, tempArr);
                    break;
                }
                this.add(a.get(i),father,addString);
            }else{
                System.out.println(k);
                System.out.println(a.get(i).get(k));

            }
        }
        //return null;
    }

    public Map create(){
        Map<String,ArrayList<Map>> result = new HashMap();
        Map<String,ArrayList<Map>> a = new HashMap();
        Map<String,ArrayList<Map>> b = new HashMap();

        Map<String,ArrayList<Map>> e = new HashMap();
        ArrayList<Map> eArr = new ArrayList<Map>();
        e.put("段段", eArr);
        ArrayList<Map> aArr = new ArrayList<Map>();
        aArr.add(e);
        a.put("一班", aArr);

        ArrayList<Map> bArr = new ArrayList<Map>();
        Map<String,ArrayList<Map>> c = new HashMap();
        Map<String,ArrayList<Map>> d = new HashMap();
        Map<String,ArrayList<Map>> f = new HashMap();
        Map<String,ArrayList<Map>> g = new HashMap();
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
    public static void main(String[] args){
        WikiSunmary c = new WikiSunmary();
        Map temp = c.create();
        System.out.println(temp);
        c.add(temp, "儿子", "duanduan");
        System.out.println("-----");
        //c.search(temp);
        System.out.println(temp);
        //c.search(c.add(temp, "ruanjian", "h"));
    }
}
