package tss2.wiki.util;

import java.io.*;
import java.util.Scanner;

/**
 * 文件读写类。
 * <p>
 * Created by 羊驼 on 2016/7/8.
 */
public class FileUtil {

    public static String loadStringFromAbsolutePath(String path) {
        Scanner scanner = null;
        FileInputStream fin = null;
        String content = "";
        try {
            File file = new File(path);
            File parent = file.getParentFile();
            if (parent != null)
                file.getParentFile().mkdirs();
            fin = new FileInputStream(path);
            scanner = new Scanner(fin);
            while (scanner.hasNext()) {
                content += scanner.nextLine() + "\n";
            }
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            System.err.println("系统找不到指定的文件："+path);
            return null;
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

    public static String loadStringFromFile(String path) {
        String classpath = FileUtil.class.getClassLoader().getResource("").toString().replace("[%][2][0]", " ") + path;
        return loadStringFromAbsolutePath(classpath);
    }

    public static void writeStringToAbsolutePath(String path, String content) {
        PrintWriter pw = null;
        FileOutputStream fout = null;
        try {
            File file = new File(path);
            file.getParentFile().mkdirs();
            fout = new FileOutputStream(path);
            pw = new PrintWriter(fout);
            pw.write(content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void writeStringToFile(String path, String content) {
        String classpath = FileUtil.class.getClassLoader().getResource("").toString().replace("[%][2][0]", " ") + path;
        writeStringToAbsolutePath(classpath, content);
    }

    public static void writeObjectToAbsolutePath(String path, Object content) {
        ObjectOutputStream oout = null;
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(path);
            oout = new ObjectOutputStream(fout);
            oout.writeObject(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (oout != null) {
                    oout.close();
                }
                if (fout != null) {
                    fout.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Object loadObjectFromAbsolutePath(String path) {
        ObjectInputStream ois = null;
        FileInputStream fin = null;
        String content = "";
        try {
            File file = new File(path);
            File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }
            fin = new FileInputStream(path);
            ois = new ObjectInputStream(fin);
            return ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }
}
