package tss2.wiki.model;

import java.util.ArrayList;

/**
 * markdown 行
 * Created by coral on 16-7-18.
 */
public class MdHeader extends MdComponent {

    private void init() {
        subHeaders = new ArrayList<>();
    }

    public MdHeader(String text) {
        super(text);
        init();
        setText(text);
    }

    public MdHeader(String text, int level) {
        this(text);
        setLevel(level);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public MdHeader[] getSubHeaders() {
        MdHeader[] result = new MdHeader[subHeaders.size()];
        return subHeaders.toArray(result);
    }

    public void addSubHeader(String header) {
        subHeaders.add(new MdHeader(header, getLevel() + 1));
    }
    public void addSubHeader(MdHeader header) {
        subHeaders.add(header);
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        text = text.trim();
        level = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '#') {
                ++level;
            }
            else {
                break;
            }
        }
    }

    public void setContent(MdContent content) {
        this.content = content;
    }

    public MdContent getContent() {
        return content;
    }

    private int level;
    private MdContent content;
    private ArrayList<MdHeader> subHeaders;
}
