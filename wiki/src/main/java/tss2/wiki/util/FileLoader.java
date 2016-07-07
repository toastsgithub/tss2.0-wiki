package tss2.wiki.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by 羊驼 on 2016/7/8.
 */
public class FileLoader {

    public static String loadString(String path) {
        Scanner scanner = null;
        FileInputStream fin = null;
        String content = "";
        try {
            fin = new FileInputStream(path);
            scanner = new Scanner(fin);
            while (scanner.hasNext()) {
                content += scanner.nextLine() + "\n";
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content;
    }
}
