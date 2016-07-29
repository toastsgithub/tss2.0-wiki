package tss2.wiki.model;

/**
 * Created by zhengyunzhi on 16/7/29.
 */
public abstract class AbstractWikiRecord {
    public abstract String getContent();
    public abstract String getTitle();
    public abstract String[] getTags();
    public abstract String[] getCategories();
    public abstract String getDate();
    public abstract String getEditor();
    public abstract long getID();
    public abstract int getVisits();
    public abstract String getSummery();
}
