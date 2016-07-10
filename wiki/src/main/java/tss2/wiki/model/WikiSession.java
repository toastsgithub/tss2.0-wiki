package tss2.wiki.model;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * 会话model
 *
 * Created by 羊驼 on 2016/7/10.
 */
public class WikiSession {

    String sessionID;
    Date deadline;

    public WikiSession() {
    }

    public WikiSession(String sessionID, Date deadline) {
        this.sessionID = sessionID;
        this.deadline = deadline;
    }

    public Date getDeadline() {
        return deadline;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

}
