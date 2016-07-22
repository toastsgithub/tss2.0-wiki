package tss2.wiki.model;

import tss2.wiki.util.StringUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

/**
 * Wiki markdown 对象
 *
 * Created by coral on 16-7-18.
 */
public class WikiMarkdown {

    public static final int DEFAULT_SUMMARY_LENGTH = 200;

    public WikiMarkdown(String mdtext) {
        setMdtext(mdtext);
        loadDs();
    }

    public String getMdtext() {
        return mdtext;
    }

    public void setMdtext(String mdtext) {
        this.mdtext = mdtext;
    }

    public void loadDs() {
        String[] lines = mdtext.split("[\n]");
        content = new ArrayList<>();

        // load lines into a tree
        Stack<MdHeader> hstack = new Stack<>();

        int i = 0;
        while (i < lines.length && !lines[i].trim().startsWith("#")) {
            MdPara para = new MdPara(lines[i]);
            content.add(para);
            ++i;
        }
        while (i < lines.length) {
            if (lines[i].trim().startsWith("#")) {

                // generate the header
                MdHeader header = new MdHeader(lines[i].trim());
                MdContent content = new MdContent();
                ++i;
                while (i < lines.length && !lines[i].trim().startsWith("#")) {
                    content.addPara(lines[i]);
                    ++i;
                    if (i >= lines.length) break;
                }
                header.setContent(content);

                if (hstack.empty()) {
                    hstack.push(header);
                    this.content.add(header);
                    continue;
                }

                // find its smallest parent and add to it as sub header.
                MdHeader preHeader;
                do {
                    /* if stack is empty before the expired parent is found,
                     * make itself as the root
                     */
                    if (hstack.empty()) {
                        preHeader = header;
                        this.content.add(header);
                        break;
                    }
                    preHeader = hstack.pop();
                } while (preHeader.getLevel() >= header.getLevel());
                if (preHeader.getLevel() < header.getLevel()) {
                    preHeader.addSubHeader(header);
                }
                hstack.push(preHeader);
            } else {
                while (i < lines.length && !lines[i].trim().startsWith("#")) {
                    content.add(new MdPara(lines[i]));
                    i++;
                }
            }
        }
    }

    private String mdtext;
    private ArrayList<MdComponent> content;

    public static void main(String[] args) {
        String md = "";
        try {
            FileInputStream fis = new FileInputStream("/home/coral/桌面/a.md");
            Scanner scanner = new Scanner(fis);
            while (scanner.hasNext()) {
                md += scanner.nextLine() + "\n";
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(WikiRecord.polish(md));
    }

    public void printHeaders() {
        content.stream().filter(component -> component instanceof MdHeader).forEach(component -> printHeader((MdHeader) component));
    }

    public void printHeader(MdHeader header) {
        for (int i = 0; i < header.getLevel(); i++) {
            System.out.print(" ");
        }
        System.out.println(header.getText());
        System.out.println(header.getContent().getText());
        for (MdHeader subheader : header.getSubHeaders()) {
            printHeader(subheader);
        }
    }

    public String getSummary(int maxLength) {
        String result = "";
        for (MdComponent component: content) {
            result += StringUtil.concatArray("", component.getText().split("[\n#]"));
            if (component instanceof MdHeader) {
                result += StringUtil.concatArray("", ((MdHeader) component).getContent().getText().split("[\n#]"));
            }
            if (result.length() >= maxLength) {
                result = result.substring(0, maxLength);
            }
        }
        return result;
    }
}
