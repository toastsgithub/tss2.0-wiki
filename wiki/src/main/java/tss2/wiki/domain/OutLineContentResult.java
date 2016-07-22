package tss2.wiki.domain;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/22.
 */
public class OutLineContentResult {
    private String name;
    private ArrayList<TitleAndSummary> content;

    public OutLineContentResult(String name,ArrayList<TitleAndSummary> content){
        this.setName(name);
        this.setContent(content);
    }

    public String getName() {
        return name;
    }

    public ArrayList<TitleAndSummary> getContent() {
        return content;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(ArrayList<TitleAndSummary> content) {
        this.content = content;
    }
}
