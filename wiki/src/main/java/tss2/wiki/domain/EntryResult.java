package tss2.wiki.domain;

/**
 * Created by Administrator on 2016/7/25.
 */
public class EntryResult {
    private int error = 0;
    private String message;
    private Entry entry;

    public EntryResult(int error,String message){
        this.setMessage(message);
        this.setError(error);
    }

    public EntryResult(int error,Entry entry){
        this.setEntry(entry);
        this.setError(error);
    }

    public void setError(int error) {
        this.error = error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }

    public int getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public Entry getEntry() {
        return entry;
    }
}
