package tss2.wiki.domain;

import tss2.wiki.model.WikiMessage;

import java.util.ArrayList;

/**
 * Created by coral on 16-7-13.
 */
public class MessageResult {
    int error = 0;

    public MessageResult() {}

    public MessageResult(int error, String message)
    {
        setError(error);
        setMessage(message);
    }

    public void setError(int error) {
        this.error = error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class UserMessage {
        int unread = 0;
        ArrayList<WikiMessage> messageList = new ArrayList<>();

        public ArrayList<WikiMessage> getMessageList() {
            return messageList;
        }

        public void addMessage(WikiMessage messageList) {
            this.messageList.add(messageList);
            if (messageList.getRead() == 0) {
                addUnread();
            }
        }

        public void addUnread() {
            ++this.unread;
        }

        public int getUnread() {
            return unread;
        }

        public void addAllMessage(ArrayList<WikiMessage> messageList) {
            messageList.forEach(this::addMessage);
        }
    }

    public String getMessage() {
        return message;
    }

    public void setData(UserMessage data) {
        this.data = data;
    }

    public int getError() {
        return error;
    }

    public UserMessage getData() {
        return data;
    }

    private UserMessage data = new UserMessage();
    private String message = "";
}
