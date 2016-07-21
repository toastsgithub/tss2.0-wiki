package tss2.wiki.model;

import java.util.ArrayList;

/**
 * Created by coral on 16-7-18.
 */
public class MdContent extends MdComponent {

    public MdContent() {
        super("");
        paras = new ArrayList<>();
    }

    public MdContent(String text) {
        super(text);
        paras = new ArrayList<>();
    }

    public MdPara[] getParas() {
        MdPara[] result = new MdPara[paras.size()];
        return paras.toArray(result);
    }

    @Override
    public String getText() {
        String result = "";
        for (MdPara para: paras) {
            result += para.getText() + "\n";
        }
        return result;
    }

    public void addPara(String para) {
        paras.add(new MdPara(para));
    }

    private ArrayList<MdPara> paras;
}
