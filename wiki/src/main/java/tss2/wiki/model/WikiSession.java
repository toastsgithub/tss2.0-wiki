package tss2.wiki.model;


import tss2.wiki.dao.core.DAOBase;
import tss2.wiki.dao.Session;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 会话model
 *
 * Created by 羊驼 on 2016/7/10.
 */
public class WikiSession {

    public WikiSession() {
        dao = new Session();
        valid = false;
    }

    /**
     * Try to get exist session from session table.
     * If failed, create a new one with an empty session ID;
     *
     * @param username 用户名称
     */
    public WikiSession(String username) {
        DAOBase[] sessions = Session.Query().where("username = '" + username + "'");
        if (sessions.length == 0) {
            dao = new Session();
            valid = false;
        }
        else {
            dao = (Session) sessions[0];
            valid = true;
        }
    }

    public WikiSession(Session session) {
        this.dao = session;
        valid = true;
    }

    /**
     * 检查sessionID是否有效。如果有效则返回该会话。
     *
     * @param sessionID 需要检查的sessionID
     * @return null表示不存在这个会话。
     */
    public static WikiSession checkSession(String sessionID) {
        DAOBase[] sessions = Session.Query().where("sessionID = '" + sessionID + "'");
        if (sessions.length == 0) return null;
        if (sessions.length > 1) {
            for (int i = 0; i < sessions.length - 1; i++) {
                Session toBeDisabled = (Session) sessions[i + 1];
                new WikiSession(toBeDisabled).disable();
            }
        }
        return new WikiSession((Session) sessions[0]);
    }


    public String getUserID() {
        return dao.username;
    }

    public WikiUser getUser() {
        return new WikiUser(dao.username);
    }

    /**
     * This will renew or create a session.
     *
     * @param username 用户名
     * @param sessionID sessionID
     * @param lifeMinutes session的生命周期
     */
    public WikiSession(String username, String sessionID, int lifeMinutes) {
        DAOBase[] sessions = Session.Query().where("username = '" + username + "'");
        if (sessions.length == 0) {
            dao = new Session();
            dao.username = username;
        }
        else {
            dao = (Session) sessions[0];
        }
        dao.sessionID = sessionID;
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, lifeMinutes);
        dao.deadline = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now.getTime());
        dao.save();
        valid = true;
    }

    /**
     * return the session id of the current session.
     *
     * @return the session id
     */
    public String getSessionID() {
        return dao.sessionID;
    }


    /**
     * Get the date of expiry.
     *
     * @return the date after when the session is invalid.
     */
    public Date getDeadline() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return formatter.parse(dao.deadline);
        } catch (ParseException e) {
            System.err.println("Deadline format error");
            return null;
        }
    }

    /**
     * Jugde whether the session is valid. According to
     * the system time of the server.
     *
     * @return {@code true} if valid. {@code false} if not
     */
    public boolean isValid() {
        if (!valid) return false;
        // check lifetime
        Date now = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date deadline = null;
        try {
            deadline = formatter.parse(dao.deadline);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return deadline != null && deadline.after(now);
    }

    /**
     * make the session invalid
     */
    public void disable() {
        dao.deadline = "0000-00-00 00:00:00";
        dao.save();
        valid = false;
    }

    /**
     * renew the current session and make it valid in
     * time of specified minutes.
     *
     * @param minutes lifetime of the renewed session.
     */
    public void renew(int minutes) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, minutes);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    private Session dao;
    private boolean valid = false;
}
