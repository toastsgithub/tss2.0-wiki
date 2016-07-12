package tss2.wiki.control.impl;

import tss2.wiki.dao.DAOBase;
import tss2.wiki.dao.Tag;
import tss2.wiki.domain.ResultMessage;
import tss2.wiki.control.service.ContentService;
import tss2.wiki.model.WikiRecord;
import tss2.wiki.model.WikiSession;
import tss2.wiki.model.WikiUser;
import tss2.wiki.vo.WikiEntryVO;

import java.util.ArrayList;
import java.util.Map;

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

    @Override
    public ResultMessage process(WikiEntryVO wikiEntry) {
        Map map = wikiEntry.getData();
        String operation = map.get("operation").toString();
        switch (operation) {
            case "add":
                return add(wikiEntry);
        }
        return null;
    }

    private ResultMessage add(WikiEntryVO data) {
        Map map = (Map) data.getData().get("data");
        String title = map.get("title").toString();
        WikiRecord entry = new WikiRecord(title);
        String summary = map.get("summary").toString();
        String[] categories = map.get("categories").toString().split("[/]");
        String[] tags = map.get("tags").toString().split("[/]");
        String content = map.get("content").toString();
        WikiUser user = new SessionServiceimpl().getUserBySession(data.getSessionID());
        entry.setContent(user.getUsername(), categories, tags, summary, content, true);
        return new ResultMessage(0);
    }
}
