package tss2.wiki.domain;


/**
 * Created by Administrator on 2016/7/7.
 */
public class LoginResult {
    private boolean included;
    private User user = new User();

    public boolean isIncluded() {
        return included;
    }

    public void setIncluded(boolean included) {
        this.included = included;
    }

    public User getUser() {
        return user;
    }

    public void setUser(String username, int type) {
        this.user.username = username;
        this.user.type = type;
    }

    public class User {
        public String username;
        public int type;
    }
}
