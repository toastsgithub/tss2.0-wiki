package tss2.wiki.domain;

/**
 * Created by Administrator on 2016/7/26.
 */
public class AdminisResult {
    private int error = 0 ;
    private String message;

    public AdminisResult(int error){
        setError(error);
    }
    public AdminisResult(int error,String message){
        setError(error);
        setMessage(message);
    }

    public int getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public void setError(int error) {
        this.error = error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
