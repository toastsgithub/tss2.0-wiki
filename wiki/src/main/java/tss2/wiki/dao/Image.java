package tss2.wiki.dao;

/**
 * Created by coral on 16-7-24.
 */
public class Image {
    public String path;
    public String imageId;

    public static Image query() {
        return new Image();
    }
}
