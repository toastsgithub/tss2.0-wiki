package tss2.wiki.control.impl;

import tss2.wiki.dao.core.DAOBase;
import tss2.wiki.dao.Tag;
import tss2.wiki.domain.CommonResult;
import tss2.wiki.control.service.ContentService;
import tss2.wiki.model.*;
import tss2.wiki.util.StringUtil;
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

        ArrayList alias = (ArrayList) map.get("alias");


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
        if (summary.equals("")) {
            WikiMarkdown wikimd = new WikiMarkdown(content);
            summary = wikimd.getSummary(WikiMarkdown.DEFAULT_SUMMARY_LENGTH);
        }

        if(entryid == 0){
            if(contain(alias)){
                return new CommonResult(1,"Alias Exist");
            }
        }

        WikiUser user = new SessionServiceimpl().getUserBySession(data.getSessionID());
        if(user.getType()==1){
            entry.setContent(entryid, user.getUsername(), categories, tags, summary, content, true);
            if (entryid == 0) {
                entryid = entry.getID();
            }
            WikiAlias wikiAlias = new WikiAlias();
            wikiAlias.Alias(entryid,title,alias);
            WikiReference wikiReference = new WikiReference(entryid,title);
            wikiReference.modify(r);
            return new CommonResult(0,"add/modify successfully!");
        }else{
            WikiVerifying wikiVerifying = new WikiVerifying();
            wikiVerifying.setContent(entryid,user.getUsername(),title,tags,categories,content,alias,summary);
            WikiVerifyingReference wikiVerifyingReference = new WikiVerifyingReference(wikiVerifying.getID());
            wikiVerifyingReference.modify(r);
            WikiMessage wikiMessage = new WikiMessage(user.getUsername(),"#1","有新的提交信息","用户"+user.getUsername()+"提交了《"+title+"》的修改申请");
            wikiMessage.send();
            return new CommonResult(0,"submit successfully!");
        }

    }

    private boolean contain(ArrayList alias){
        ArrayList<String> aliasList = new ArrayList<>();
        WikiAlias wikiAlias = new WikiAlias();
        aliasList = wikiAlias.getAlias();
        for(int i = 0;i < alias.size();i++){
            for(int j = 0;j < aliasList.size();j++){
                if(aliasList.get(j).equals(alias.get(i).toString())){
                    System.out.println("Alias Exist!!!");
                    return true;
                }
            }
        }
        return false;
    }

}
