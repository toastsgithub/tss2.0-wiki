package tss2.wiki.domain;

import java.util.ArrayList;

/**
 * Created by coral on 16-7-14.
 */
public class OutLineResult {
    ArrayList<String> data;

    public OutLineResult() {
        data = new ArrayList<>();
    }

    public ArrayList<String> getData() {
        return data;
    }

    public void addOutLine(String outline) {
        data.add(outline);
    }
}
