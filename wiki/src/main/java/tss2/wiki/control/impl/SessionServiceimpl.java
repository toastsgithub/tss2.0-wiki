package tss2.wiki.control.impl;

import tss2.wiki.model.WikiSession;
import tss2.wiki.control.service.SessionService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by 羊驼 on 2016/7/10.
 */
public class SessionServiceimpl implements SessionService {

    public static final int SESSION_ID_LENGTH = 32;             // 默认会话id长度
    public static final int DEFAULT_SESSION_LIFETIME = 24 * 60; // 默认会话生命周期为一天。

    public SessionServiceimpl() {
        sessionTable = new HashMap<>();
    }


    /**
     * 在会话列表中检查该用户的会话。如果该用户的会话不存在
     * 或者已经过期，则返回null。
     * 否则，返回这个用户的会话id。
     *
     * @param username 需要检查会话id的用户名
     * @return null表示会话不存在或失效，sessionID表示该用户的会话。
     */
    @Override
    public WikiSession checkSession(String username) {
        if (!sessionTable.containsKey(username)) {
            return null;
        }
        WikiSession session = sessionTable.get(username);
        Date now = Calendar.getInstance().getTime();
        if (now.after(session.getDeadline())) {
            return null;
        }
        return session;
    }

    /**
     * 新建一个用户的会话，而不会去检查这个用户的合法性。
     * 这个会话在{@code minutes}分钟内是有效的。
     * 如果当前用户存在一个有效的会话，这个用户仍然会创
     * 建一个新的会话，并且原会话id将会失效。
     *
     * @param username 需要创建会话的用户名。
     * @param minutes 会话的生命周期。
     * @return 创建的会话id。
     */
    @Override
    public WikiSession register(String username, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minutes);
        String sessionID = generateSessionID(SESSION_ID_LENGTH);
        WikiSession session = new WikiSession(sessionID, calendar.getTime());
        sessionTable.put(username, session);
        return session;
    }

    @Override
    public WikiSession checkSession(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String sessionID = "";
        for (Cookie cookie: cookies) {
            if (cookie.getName().equals("sessionID")) {
                sessionID = cookie.getValue();
            }
        }
        return checkSession(sessionID);
    }

    /**
     * 为这个用户创建一个具有默认会话长度的会话。
     *
     * @param username 需要创建会话的用户名。
     * @return 创建的会话id。
     */
    @Override
    public WikiSession register(String username) {
        return register(username, DEFAULT_SESSION_LIFETIME);
    }

    /**
     * 使用户的会话失效。
     *
     * @param username 需要关闭会话的用户名。
     */
    public void disableSession(String username) {
        sessionTable.remove(username);
    }

    @Override
    public String getUserBySession(String sessionID) {
        for (String username: sessionTable.keySet()) {
            if (sessionTable.get(username).getSessionID().equals(sessionID)) {
                return username;
            }
        }
        return null;
    }

    private HashMap<String, WikiSession> sessionTable;

    private String generateSessionID(int length) {
        String template = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        String sessionid = "";
        Random rand = new Random();
        for (int i = 0; i < length; ++i) {
            sessionid += template.charAt(rand.nextInt(template.length()));
        }
        return sessionid;
    }
}
