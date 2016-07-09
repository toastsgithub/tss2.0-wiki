package tss2.wiki.domain;

import tss2.wiki.dao.impl.User;

/**
 * Created by Administrator on 2016/7/7.
 */
public class LoginResult {
    private boolean included;
    private User user;

    public boolean isIncluded() {
        return included;
    }

    public User getUser() {
        return user;
    }

    public void setIncluded(boolean included) {
        this.included = included;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
