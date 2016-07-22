package tss2.wiki.model;

import tss2.wiki.dao.WikiEntry;
import tss2.wiki.dao.core.DAOBase;
import tss2.wiki.domain.OutLineContentResult;
import tss2.wiki.domain.OutLineTitleResult;
import tss2.wiki.domain.TitleAndSummary;
import tss2.wiki.util.FileUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/10.
 * add(),delete(),modify()的实现均基于search（）方法的基本思路
 */
public class WikiOutline {

    static String path = "/home/coral/文档/Tencent Files/2419335621/FileRecv/summary.dat";

    private static void loadPath() {
        path = WikiOutline.class.getClassLoader().getResource("").getPath().replaceAll("[%][2][0]", " ") + "summary.dat";
    }

    public WikiOutline() {
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
        WikiOutline.map = map;
        FileUtil.writeObjectToAbsolutePath(path, map);
    }


    public ArrayList<OutLineContentResult> getTitleAndSummery(){
        DAOBase[] wikis = WikiEntry.query().where("");
        ArrayList<WikiEntry> wikiEntryArrayList= new ArrayList<>();
        ArrayList<String>  categories = new ArrayList<>();
        for(int i = 0; i < wikis.length;i++){
            wikiEntryArrayList.add((WikiEntry)wikis[i]);
        }
        for(int i = 0; i < wikiEntryArrayList.size(); i++){
            if(!categories.contains(wikiEntryArrayList.get(i).categories)){
                categories.add(wikiEntryArrayList.get(i).categories);
            }
        }
        ArrayList<OutLineContentResult> results = new ArrayList<>();
        for(int i = 0;i < categories.size(); i++){
            ArrayList<TitleAndSummary> titleAndSummaries = new ArrayList<>();
            for(int j = 0;j < wikiEntryArrayList.size(); j++){
                if(wikiEntryArrayList.get(j).categories.equals(categories.get(i))){
                    titleAndSummaries.add(new TitleAndSummary(wikiEntryArrayList.get(j).title,wikiEntryArrayList.get(j).summery));
                }
            }
            results.add(new OutLineContentResult(categories.get(i),titleAndSummaries));
        }

        return results;
    }

    private static Map map = null;
}