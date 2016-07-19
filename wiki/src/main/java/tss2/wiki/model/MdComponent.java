package tss2.wiki.model;

/**
 * Created by coral on 16-7-18.
 */
public class MdComponent {

    public MdComponent(String text) {
        setText(text);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private String text;

}
