package tss2.wiki.control.impl;

import tss2.wiki.model.WikiSession;
import tss2.wiki.control.service.SessionService;
import tss2.wiki.util.StringUtil;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * Created by 羊驼 on 2016/7/10.
 */
public class SessionServiceimpl implements SessionService {

    public static final int SESSION_ID_LENGTH = 32;             // 默认会话id长度
    public static final int DEFAULT_SESSION_LIFETIME = 24 * 60; // 默认会话生命周期为一天。

    public SessionServiceimpl() { }


    @Override
    public WikiSession checkUser(String username) {
        WikiSession wikiSession = new WikiSession(username);
        if (wikiSession.isValid()) {
            return null;
        }
        return wikiSession;
    }

    public WikiSession checkSession(String sessionID) {
        return WikiSession.checkSession(sessionID);
    }


    @Override
    public WikiSession register(String username, int minutes) {
        return new WikiSession(username, new SessionServiceimpl().generateSessionID(32), minutes);
    }

    @Override
    public WikiSession checkUser(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String sessionID = "";
        for (Cookie cookie: cookies) {
            if (cookie.getName().equals("sessionID")) {
                sessionID = cookie.getValue();
            }
        }
        return checkSession(sessionID);
    }

    @Override
    public WikiSession register(String username) {
        return register(username, DEFAULT_SESSION_LIFETIME);
    }

    public void disableSession(String username) {
        new WikiSession(username).disable();
    }

    @Override
    public String getUserBySession(String sessionID) {
        WikiSession session = WikiSession.checkSession(sessionID);
        if (session == null) return null;
        return session.getUserID();
    }

    private String generateSessionID(int length) {
        return StringUtil.generateTokener(length);
    }
}
