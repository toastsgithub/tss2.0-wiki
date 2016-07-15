package tss2.wiki.domain;

import tss2.wiki.dao.WikiEntry;
import tss2.wiki.model.WikiRecord;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/14.
 */
public class RecordsResult {
    private int exist = 0;
    private ArrayList<WikiEntry>  list;
    public RecordsResult(int exist){
        this.exist = exist;
    }

    public RecordsResult(int exist,ArrayList<WikiEntry>  list){
        this.exist = exist;
        this.list = list;
    }

    public int getExist() {
        return exist;
    }

    public ArrayList<WikiEntry> getList() {
        return list;
    }

    public void setExist(int exist) {
        this.exist = exist;
    }

    public void setList(ArrayList<WikiEntry> list) {
        this.list = list;
    }
}
