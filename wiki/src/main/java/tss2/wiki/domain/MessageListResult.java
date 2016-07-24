package tss2.wiki.domain;

import java.util.ArrayList;

/**
 * Created by coral on 16-7-17.
 */
public class MessageListResult {
    private int error = 0;
    private ArrayList<SimpleMessage> data;
    private String message = null;

    public MessageListResult(int error) {
        setError(error);
        data = new ArrayList<>();
    }

    public MessageListResult(int error, String message) {
        setError(error);
        setMessage(message);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setError(int error) {
        this.error = error;
    }

    public int getError() {
        return error;
    }

    public ArrayList<SimpleMessage> getData() {
        return data;
    }

    public void addMessage(String messageID, String fromUser, String title, String content) {
        data.add(new SimpleMessage(messageID, fromUser, title, content));
    }

    public class SimpleMessage {
        String title;
        String messageID;
        String fromUser;
        String detail;

        public SimpleMessage(String messageID, String fromUser, String title, String content) {
            setTitle(title);
            setFromUser(fromUser);
            setMessageID(messageID);
            setDetail(content);
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public String getTitle() {
            return title;
        }

        public String getFromUser() {
            return fromUser;
        }

        public String getMessageID() {
            return messageID;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setFromUser(String fromUser) {
            this.fromUser = fromUser;
        }

        public void setMessageID(String messageID) {
            this.messageID = messageID;
        }
    }
}
