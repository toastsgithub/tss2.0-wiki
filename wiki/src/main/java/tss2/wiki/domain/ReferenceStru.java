package tss2.wiki.domain;

/**
 * Created by Administrator on 2016/7/26.
 */
public class ReferenceStru {
    private long verifyId;
    private String name;
    private String url;
    private String timestamp;

    public ReferenceStru(long verifyId,String name,String url,String timestamp){
        setTimestamp(timestamp);
        setName(name);
        setUrl(url);
        setVerifyId(verifyId);
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setVerifyId(long verifyId) {
        this.verifyId = verifyId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public long getVerifyId() {
        return verifyId;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
