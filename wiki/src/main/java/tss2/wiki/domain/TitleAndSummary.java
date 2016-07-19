package tss2.wiki.domain;

/**
 * Created by Administrator on 2016/7/19.
 */
public class TitleAndSummary {
    private String title;
    private String summary;

    public TitleAndSummary(String title,String summary){
        this.title = title;
        this.summary = summary;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }

    public String getTitle() {
        return title;
    }
}
