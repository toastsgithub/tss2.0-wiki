package tss2.wiki.domain;

import tss2.wiki.model.AbstractWikiRecord;
import tss2.wiki.model.WikiRecord;
import tss2.wiki.model.WikiReference;

/**
 * Created by 羊驼 on 2016/7/11.
 */
public class WikiResult {
    int exist = 0;
    AbstractWikiRecord data;
    WikiReference reference;

    public WikiResult(int exist) {
        setExist(exist);
    }

    public WikiResult(int exist, AbstractWikiRecord record, WikiReference reference) {
        setExist(exist);
        setData(record);
        setReference(reference);
    }

    public void setExist(int exist) {
        this.exist = exist;
    }

    public void setData(AbstractWikiRecord wikiRecord) {
        this.data = wikiRecord;
    }

    public int getExist() {
        return exist;
    }

    public AbstractWikiRecord getData() {
        return data;
    }

    public void setReference(WikiReference reference) {
        this.reference = reference;
    }

    public WikiReference getReference() {
        return reference;
    }
}
