package tss2.wiki.control.service;

import tss2.wiki.model.WikiSession;
import tss2.wiki.model.WikiUser;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by 羊驼 on 2016/7/10.
 */
public interface SessionService {
    /**
     * 在会话列表中检查该用户的会话。如果该用户的会话不存在
     * 或者已经过期，则返回null。
     * 否则，返回这个用户的会话id。
     *
     * @param username 需要检查会话id的用户名
     * @return null表示会话不存在或失效，sessionID表示该用户的会话。
     */
    WikiSession checkUser(String username);

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
    WikiSession register(String username, int minutes);

    /**
     * 在会话列表中检查当前亲贵的会话。如果该用户的会话不存在
     * 或者已经过期，则返回null。
     * 否则，返回这个用户的会话id。
     *
     * @param request 需要检查会话id的用户名
     * @return null表示会话不存在或失效，sessionID表示该用户的会话。
     */
    WikiSession checkUser(HttpServletRequest request);

    /**
     * 为这个用户创建一个具有默认会话长度的会话。
     *
     * @param username 需要创建会话的用户名。
     * @return 创建的会话id。
     */
    WikiSession register(String username);

    /**
     * 使用户的会话失效。
     *
     * @param username 需要关闭会话的用户名。
     */
    void disableSession(String username);

    /**
     * 根据session id 获取用户id
     * @param sessionID session id
     * @return 用户名
     */
    WikiUser getUserBySession(String sessionID);
}
