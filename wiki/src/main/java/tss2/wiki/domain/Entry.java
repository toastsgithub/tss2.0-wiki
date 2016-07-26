package tss2.wiki.domain;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/25.
 */
public class Entry {
    private String title;
    private String[] alias;
    private String[] tags;
    private String content;
    private String categories;
    private String summery;
    private String username;
    private String timestamp;
    private ArrayList<ReferenceStru> referenceStrus = new ArrayList<>();

    public void setReferenceStrus(ArrayList<ReferenceStru> referenceStrus) {
        this.referenceStrus = referenceStrus;
    }

    public ArrayList<ReferenceStru> getReferenceStrus() {
        return referenceStrus;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setAlias(String[] alias) {
        this.alias = alias;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSummery(String summery) {
        this.summery = summery;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCategories() {
        return categories;
    }

    public String getContent() {
        return content;
    }

    public String getSummery() {
        return summery;
    }

    public String getTitle() {
        return title;
    }

    public String getUsername() {
        return username;
    }

    public String[] getAlias() {
        return alias;
    }

    public String[] getTags() {
        return tags;
    }
}
