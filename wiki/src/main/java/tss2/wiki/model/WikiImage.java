package tss2.wiki.model;

import org.springframework.web.multipart.MultipartFile;
import tss2.wiki.dao.Image;
import tss2.wiki.dao.core.DAOBase;
import tss2.wiki.util.StringUtil;

import java.io.File;
import java.io.IOException;

/**
 * 增加对插入图片的支持. 保存图片并为图片分配一个唯一的
 * url和编号。
 *
 * Created by 羊驼 on 2016/7/11.
 */
public class WikiImage {

    private WikiImage() { }

    public static final String IMAGE_PREFIX = WikiImage.class.getClassLoader().getResource("").getPath().replaceAll("[%][2][0]", " ") + "images/";
    public static File parent;
    static {
        File file = new File(IMAGE_PREFIX);
        file.mkdirs();
        parent = file.getParentFile().getParentFile().getParentFile();
        parent = new File(parent, "images/");
        parent.mkdirs();
    }

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

    public WikiImage(MultipartFile multipartFile) {
        dao = new Image();
        String[] arrnames = multipartFile.getOriginalFilename().split("[.]");
        dao.format = arrnames[arrnames.length - 1];
        generateId();

        File file = new File(parent, dao.imageId + "." + dao.format);
        dao.path = file.getPath();
        try {
            multipartFile.transferTo(file); // write the file to the file system
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getImageId() {
        generateId();
        return dao.imageId;
    }

    public String getImagePath() {
        return dao.path;
    }

    private void generateId() {
        if (dao.imageId == null || dao.imageId.equals("")) {
            dao.imageId = StringUtil.concatArray("", dao.timestamp.split("[ :/-]")) + StringUtil.generateTokener(6);
            dao.save();
        }
    }

    public String getImageUrl() {
        return String.format("/file/image/%s.%s", dao.imageId, dao.format);
    }

    private Image dao;
}
