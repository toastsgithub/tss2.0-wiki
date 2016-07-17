package tss2.wiki.domain;

/**
 * Created by coral on 16-7-13.
 */
public class MessageResult {

    public MessageResult(int error) {
        setError(error);
    }

    public MessageResult(int error, String fromUser, String title, String detail) {
        setError(error);
        setDetail(detail);
        setFromUser(fromUser);
        setTitle(title);
    }

    public int getError() {
        return error;
    }

    public String getDetail() {
        return detail;
    }

    public void setError(int error) {
        this.error = error;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getFromUser() {
        return fromUser;
    }

    public String getTitle() {
        return title;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private int error = 0;
    private String detail = "";
    private String title = "";
    private String fromUser = "";
}
