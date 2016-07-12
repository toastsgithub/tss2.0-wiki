package tss2.wiki.vo;

import tss2.wiki.dao.WikiEntry;
import tss2.wiki.model.WikiSession;

import java.util.Map;

/**
 * Created by 羊驼 on 2016/7/10.
 */
public class WikiEntryVO {
    WikiSession session;
    Map data;

    public WikiEntryVO(WikiSession sessionID, Map data) {
        setData(data);
        setSession(sessionID);
    }

    public String getSessionID() {
        return session.getSessionID();
    }

    public Map getData() {
        return data;
    }

    public void setSession(WikiSession session) {
        this.session = session;
    }

    public WikiSession getSession() {
        return session;
    }

    public void setData(Map data) {
        this.data = data;
    }
}
