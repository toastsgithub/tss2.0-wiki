package tss2.wiki.vo;

import tss2.wiki.dao.WikiEntry;
import tss2.wiki.model.WikiSession;

import java.util.Map;

/**
 * Created by 羊驼 on 2016/7/10.
 */
public class WikiEntryVO {
    String sessionID;
    Map data;

    public WikiEntryVO(String sessionID, Map data) {
        setData(data);
        setSessionID(sessionID);
    }

    public String getSessionID() {
        return sessionID;
    }

    public Map getData() {
        return data;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public void setData(Map data) {
        this.data = data;
    }
}
