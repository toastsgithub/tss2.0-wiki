package tss2.wiki.service.impl;

import tss2.wiki.dao.DAOBase;
import tss2.wiki.dao.impl.Tag;
import tss2.wiki.service.ContentService;

import java.util.ArrayList;

/**
 * Created by 羊驼 on 2016/7/9.
 */
public class ContentServiceImpl implements ContentService {
    @Override
    public ArrayList<String> getTags() {
        ArrayList<String> result = new ArrayList<>();
        DAOBase[] tags = Tag.query().where("");
        for (DAOBase tag: tags) {
            result.add(tag.get("tag").toString());
        }
        return result;
    }
}
