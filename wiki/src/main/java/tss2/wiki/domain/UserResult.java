package tss2.wiki.domain;

/**
 * Created by coral on 16-7-12.
 */
public class UserResult {

    public UserResult(int login) {
        login = login;
    }

    public UserResult(int login, String username, int type) {
        this.login = login;
        data = new UserInfo(username, type);
    }

    public int getLogin() {
        return login;
    }

    public UserInfo getData() {
        return data;
    }

    private int login = 0;
    private UserInfo data = null;

    private class UserInfo {
        public UserInfo(String username, int type) {
            this.username = username;
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public String getUsername() {
            return username;
        }

        private String username;
        private int type;
    }
}

