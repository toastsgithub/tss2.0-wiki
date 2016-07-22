package tss2.wiki.script;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by coral on 16-7-21.
 */
public class GenerateSwebok {
    public static void main(String[] args) {
        FileInputStream fis = null;
        Scanner scanner = null;
        String md = "";
        try {
            fis = new FileInputStream("chap1.txt");
            scanner = new Scanner(fis);
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (line.matches("CHAPTER.*")) {            // 章节标题
                    md += "#" + line + "\n" + "[TOC]\n";
                } else if (line.matches("INTRODUCTION")) {  // introduction
                    md += "**" + line + "**" + "\n";
                } else if (line.matches("[0-9]+[.][0-9]+[.].*")) {  // 小节标题
                    md += "###" + line + "\n";
                } else if (line.matches("[0-9]+[.].*")) {  // 大标题
                    md += "##" + line + "\n";
                } else if (line.matches("Figure [0-9]+[.][0-9]+[.].*")) {   // 图标
                    md += "*" + line + "*" + "\n";
                } else {
                    md += line + "\n";
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("error loading chap1.txt");
        }
        System.out.println(md);
    }
}
