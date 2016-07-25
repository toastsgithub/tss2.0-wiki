package tss2.wiki.model;

import tss2.wiki.dao.Image;
import tss2.wiki.dao.core.DAOBase;

/**
 * TODO: 增加对插入图片的支持.
 *
 * Created by 羊驼 on 2016/7/11.
 */
public class WikiImage {

    private WikiImage() { }

    public static WikiImage loadImage(String imageId) {
        WikiImage image = new WikiImage();
        DAOBase[] images = Image.query().where(String.format("imageId = '%s'", image));
        if (images.length > 0) {
            image.dao = (Image) images[0];
        } else {
            return null;
        }
        return image;
    }

    private Image dao;
}
