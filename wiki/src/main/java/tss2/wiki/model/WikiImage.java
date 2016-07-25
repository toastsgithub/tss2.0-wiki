package tss2.wiki.model;

import tss2.wiki.dao.Image;
import tss2.wiki.util.StringUtil;
import tss2.wiki.util.TimeUtil;

import java.io.File;

/**
 * TODO: 增加对插入图片的支持.
 *
 * Created by 羊驼 on 2016/7/11.
 */
public class WikiImage {

    public WikiImage() {
    }

    public String getId() {
        if (dao.imageId == null || dao.imageId.equals("")) {
            dao.imageId = StringUtil.concatArray("", TimeUtil.getTimeStampBySecond().split("[ -:]")) + StringUtil.generateTokener(4);
        }
        return dao.imageId;
    }

    public File getFile() {
        if (dao.path == null || dao.path.equals("")) {
            return null;
        }
        return new File(dao.path);
    }
    private Image dao;
}
