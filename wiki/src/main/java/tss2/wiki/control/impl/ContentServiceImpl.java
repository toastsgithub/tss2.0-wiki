package tss2.wiki.control.impl;

import tss2.wiki.dao.core.DAOBase;
import tss2.wiki.dao.Tag;
import tss2.wiki.domain.CommonResult;
import tss2.wiki.control.service.ContentService;
import tss2.wiki.model.WikiMarkdown;
import tss2.wiki.model.WikiRecord;
import tss2.wiki.model.WikiReference;
import tss2.wiki.model.WikiUser;
import tss2.wiki.vo.WikiEntryVO;

import java.util.ArrayList;
import java.util.Map;

import static tss2.wiki.model.WikiRecord.Alias;

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
    public CommonResult process(WikiEntryVO wikiEntry) {
        Map map = wikiEntry.getData();
        String operation = map.get("operation").toString();
        switch (operation) {
            case "add":
                return add(wikiEntry);
        }
        return null;
    }

    private CommonResult add(WikiEntryVO data) {
        Map map = (Map) data.getData().get("data");
        String title = map.get("title").toString();
        WikiRecord entry = new WikiRecord(title);
        String summary = map.get("summary").toString();
        String[] categories = map.get("categories").toString().split("[/]");
        ArrayList arrTags = (ArrayList) map.get("tags");
        String[] tags = new String[arrTags.size()];
        for (int i = 0; i < tags.length; i++) {
            tags[i] = arrTags.get(i).toString();
        }
        String content = map.get("content").toString();
        long entryid = (long)Integer.parseInt(map.get("id").toString());

        ArrayList r =(ArrayList) map.get("reference");
        WikiReference wikiReference = new WikiReference(entryid,title);
        wikiReference.modify(r);

        ArrayList alias = (ArrayList) map.get("alias");
        Alias(entryid,title,alias);



        if (summary.equals("")) {
            WikiMarkdown wikimd = new WikiMarkdown(content);
            summary = wikimd.getSummary(WikiMarkdown.DEFAULT_SUMMARY_LENGTH);
        }

        WikiUser user = new SessionServiceimpl().getUserBySession(data.getSessionID());
        entry.setContent(entryid,user.getUsername(), categories, tags, summary, content, true);
        return new CommonResult(0);
    }
}
