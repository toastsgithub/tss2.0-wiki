package tss2.wiki.domain;

import tss2.wiki.model.AbstractWikiRecord;
import tss2.wiki.model.WikiRecord;

/**
 * Created by zhengyunzhi on 16/7/29.
 */
public class SimpleWikiRecord extends AbstractWikiRecord {
    String content;

    public SimpleWikiRecord(String content) {
        setContent(content);
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String[] getTags() {
        return new String[0];
    }

    @Override
    public String[] getCategories() {
        return new String[0];
    }

    @Override
    public String getDate() {
        return null;
    }

    @Override
    public String getEditor() {
        return null;
    }

    @Override
    public long getID() {
        return 0;
    }

    @Override
    public int getVisits() {
        return 0;
    }

    @Override
    public String getSummery() {
        return null;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
