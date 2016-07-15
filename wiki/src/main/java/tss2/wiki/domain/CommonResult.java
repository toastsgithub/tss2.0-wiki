package tss2.wiki.domain;

/**
 * Created by 羊驼 on 2016/7/9.
 */
public class CommonResult {
    private int error = 0;
    private String message = "";

    public CommonResult(int error) {
        setError(error);
    }

    public CommonResult(int error, String message) {
        setError(error);
        setMessage(message);
    }

    public void setError(int error) {
        this.error = error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
