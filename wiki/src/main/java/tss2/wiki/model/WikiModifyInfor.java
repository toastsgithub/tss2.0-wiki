package tss2.wiki.model;


import tss2.wiki.dao.UpdateHistory;
import tss2.wiki.dao.Verifying;
import tss2.wiki.dao.core.DAOBase;
import tss2.wiki.domain.ModifyHistory;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/25.
 */

public class WikiModifyInfor {
    public ArrayList<ModifyHistory> getModifyList(WikiUser user){
        ArrayList<ModifyHistory> modifyHistoryArrayList = new  ArrayList<ModifyHistory>();

        DAOBase[] contents = UpdateHistory.query().where("username = '" + user.getUsername() + "'");
        for(int i = 0; i < contents.length; i++){
            modifyHistoryArrayList.add(new ModifyHistory(((UpdateHistory)contents[i]).username,((UpdateHistory)contents[i]).entryid,
                    ((UpdateHistory)contents[i]).title,((UpdateHistory)contents[i]).timestamp,2));
        }

        DAOBase[] contents1 = Verifying.query().where("username = '" + user.getUsername() + "'");
        for(int i = 0; i < contents1.length; i++){
            modifyHistoryArrayList.add(new ModifyHistory(((Verifying)contents1[i]).username,((Verifying)contents1[i]).wikiId,
                    ((Verifying)contents1[i]).title,((Verifying)contents1[i]).timestamp,((Verifying)contents1[i]).refused));
        }

        return modifyHistoryArrayList;
    }
}
