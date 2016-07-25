package tss2.wiki.domain;

/**
 * Created by Administrator on 2016/7/25.
 */
public class ModifyHistory {
    private String username;
    private long wikiId;
    private String title;
    private String timestamp;
    //state:0,1,2 待审核，已拒绝,已添加
    private int state;

    public ModifyHistory(String username,long wikiId,String title,String timestamp,int state){
        this.setUsername(username);
        this.setWikiId(wikiId);
        this.setTitle(title);
        this.setTimestamp(timestamp);
        this.setState(state);
    }

    public String getUsername() {
        return username;
    }

    public long getWikiId() {
        return wikiId;
    }

    public String getTitle() {
        return title;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getState() {
        return state;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setWikiId(long wikiId) {
        this.wikiId = wikiId;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setState(int state) {
        this.state = state;
    }
}
