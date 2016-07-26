package tss2.wiki.model;


import tss2.wiki.dao.UpdateHistory;
import tss2.wiki.dao.Verifying;
import tss2.wiki.dao.VerifyingReference;
import tss2.wiki.dao.core.DAOBase;
import tss2.wiki.domain.Entry;
import tss2.wiki.domain.ModifyHistory;
import tss2.wiki.domain.ReferenceStru;
import tss2.wiki.util.FileUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/25.
 */

public class WikiModifyInfor {
    public ArrayList<ModifyHistory> getModifyList(WikiUser user){
        ArrayList<ModifyHistory> modifyHistoryArrayList = new  ArrayList<ModifyHistory>();

        DAOBase[] contents = UpdateHistory.query().where("username = '" + user.getUsername() + "'");
        for(int i = 0; i < contents.length; i++){
            modifyHistoryArrayList.add(new ModifyHistory(((UpdateHistory)contents[i]).username,((UpdateHistory)contents[i]).id,
                    ((UpdateHistory)contents[i]).title,((UpdateHistory)contents[i]).timestamp,2));
        }

        DAOBase[] contents1 = Verifying.query().where("username = '" + user.getUsername() + "'");
        for(int i = 0; i < contents1.length; i++){
            modifyHistoryArrayList.add(new ModifyHistory(((Verifying)contents1[i]).username,((Verifying)contents1[i]).id,
                    ((Verifying)contents1[i]).title,((Verifying)contents1[i]).timestamp,((Verifying)contents1[i]).refused));
        }

        return modifyHistoryArrayList;
    }

    public Entry getEntry(int state,long id){
        Entry entry = new Entry();
        if(state == 2){
            DAOBase[] contents = UpdateHistory.query().where("id = '" + id + "'");
            if(contents.length == 0){
                return null;
            }
            UpdateHistory updateHistory = (UpdateHistory)contents[0];
            entry.setUsername(updateHistory.username);
            entry.setCategories(updateHistory.categories);
            entry.setSummery(updateHistory.summary);
            entry.setTitle(updateHistory.title);
            entry.setTimestamp(updateHistory.timestamp);
            String[] re = new String[0];
            if(updateHistory.tags.split("/").equals("")){
                entry.setTags(re);
            }else{
                entry.setTags(updateHistory.tags.split("/"));
            }
            entry.setAlias(re);
            String path = updateHistory.contentPath;
            entry.setContent(FileUtil.loadStringFromAbsolutePath(path));
        }else{
            DAOBase[] contents = Verifying.query().where("id = '" + id + "'");
            if(contents.length == 0){
                return null;
            }
            Verifying verifying = (Verifying)contents[0];
            entry.setUsername(verifying.username);
            entry.setCategories(verifying.categories);
            entry.setSummery(verifying.summery);
            entry.setTitle(verifying.title);
            entry.setTimestamp(verifying.timestamp);
            String[] re = new String[0];
            if(verifying.tags.split("/").equals("")){
                entry.setTags(re);
            }else{
                entry.setTags(verifying.tags.split("/"));
            }
            if(verifying.alias.split("/").equals("")){
                entry.setAlias(re);
            }else{
                entry.setAlias(verifying.alias.split("/"));
            }
            String path = verifying.contentPath;
            entry.setContent(FileUtil.loadStringFromAbsolutePath(path));

            ArrayList<ReferenceStru> referenceStrus = new ArrayList<ReferenceStru>();
            DAOBase[] contents1 = VerifyingReference.query().where("verifyId = '" + id + "'");
            for(int i = 0; i < contents1.length; i++){
                ReferenceStru temp = new ReferenceStru(((VerifyingReference)contents1[i]).id,((VerifyingReference)contents1[i]).name,
                        ((VerifyingReference)contents1[i]).url,((VerifyingReference)contents1[i]).timestamp);
                referenceStrus.add(temp);
            }
            entry.setReferenceStrus(referenceStrus);
        }

        return entry;
    }

}
