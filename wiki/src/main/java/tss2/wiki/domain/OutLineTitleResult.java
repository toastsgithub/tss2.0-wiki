package tss2.wiki.domain;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/14.
 */
public class OutLineTitleResult {
    private int exist = 0;
    private ArrayList<TitleAndSummary> titlelist;

    public OutLineTitleResult(int exist, ArrayList<TitleAndSummary> titlelist){
        this.exist = exist;
        this.titlelist = titlelist;
    }

    public OutLineTitleResult(int exist){
        this.exist = exist;
    }

    public void setExist(int exist) {
        this.exist = exist;
    }

    public void setTitlelist(ArrayList<TitleAndSummary> titlelist) {
        this.titlelist = titlelist;
    }

    public int getExist() {
        return exist;
    }

    public ArrayList<TitleAndSummary> getTitlelist() {
        return titlelist;
    }
}
