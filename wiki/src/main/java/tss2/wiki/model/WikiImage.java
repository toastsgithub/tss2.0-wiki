package tss2.wiki.model;

/**
 * Created by 羊驼 on 2016/7/11.
 */
public class WikiImage {

    public String getFloderPath() {
        return floderPath;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public void setFloderPath(String floderPath) {
        this.floderPath = floderPath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private String path;
    private String name;
    private String floderPath;
}
