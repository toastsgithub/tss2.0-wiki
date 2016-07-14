package tss2.wiki.domain;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/14.
 */
public class OutLineTitleResult {
    private int exist = 0;
    private ArrayList<String> titlelist;

    public OutLineTitleResult(int exist, ArrayList<String> titlelist){
        this.exist = exist;
        this.titlelist = titlelist;
    }

    public OutLineTitleResult(int exist){
        this.exist = exist;
    }

    public void setExist(int exist) {
        this.exist = exist;
    }

    public void setTitlelist(ArrayList<String> titlelist) {
        this.titlelist = titlelist;
    }

    public int getExist() {
        return exist;
    }

    public ArrayList<String> getTitlelist() {
        return titlelist;
    }
}
