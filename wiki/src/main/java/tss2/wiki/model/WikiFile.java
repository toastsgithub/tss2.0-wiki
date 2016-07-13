package tss2.wiki.model;

import tss2.wiki.util.FileUtil;

import java.io.File;

/**
 * WikiFile model.
 *
 * Created by 羊驼 on 2016/7/11.
 */
public class WikiFile {

    private String path;

    /**
     * load the path and try to generate parent folder.
     *
     * @param path the path of the file.
     */
    public WikiFile(String path) {
        File fp = new File(path);
        if (!fp.exists()) {
            String parent = fp.getParent();
            if (parent != null) {
                File pp = new File(parent);
                if (!pp.exists()) {
                    pp.mkdirs();
                }
            }
        }
        setPath(path);
    }

    public String getPath() {
        return path;
    }

    public String getContent() {
        return FileUtil.loadStringFromAbsolutePath(path);
    }

    public void setContent(String content) {
        FileUtil.writeStringToAbsolutePath(path, content);
    }

    private void setPath(String path) {
        this.path = path;
    }
}
