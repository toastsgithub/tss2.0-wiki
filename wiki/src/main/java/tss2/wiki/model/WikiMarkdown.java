package tss2.wiki.model;

/**
 * Wiki markdown 对象
 *
 * Created by coral on 16-7-18.
 */
public class WikiMarkdown {

    public WikiMarkdown(String mdtext) {
        setMdtext(mdtext);
    }

    public String getMdtext() {
        return mdtext;
    }

    public void setMdtext(String mdtext) {
        this.mdtext = mdtext;
    }

    public void loadDs() {
        String[] lines = mdtext.split("[\n\r]");
    }

    private String mdtext;
}
