package tss2.wiki.model;

import tss2.wiki.dao.Verifying;
import tss2.wiki.dao.VerifyingReference;
import tss2.wiki.dao.core.DAOBase;
import tss2.wiki.util.FileUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/25.
 */
public class WikiAdministrator {

    public void agree(long id){
        DAOBase[] contents = Verifying.query().where("id = '" + id + "'");
        Verifying verifying = (Verifying)contents[0];
        DAOBase[] content1 = VerifyingReference.query().where("verifyId = '" + id + "'");
        ArrayList<VerifyingReference> verifyingReferenceArrayList= new ArrayList<>();
        for(int i = 0; i < content1.length; i++){
            verifyingReferenceArrayList.add((VerifyingReference)content1[i]);
        }

        WikiRecord entry = new WikiRecord(verifying.title);
        String content = FileUtil.loadStringFromAbsolutePath(verifying.contentPath);
        entry.setContent(verifying.wikiId, verifying.username, verifying.categories.split("/"), verifying.tags.split("/"), verifying.summery,content, true);
        if (verifying.wikiId == 0) {
            verifying.wikiId = entry.getID();
        }

        ArrayList<String> list = new ArrayList<>();
        String[] strings = verifying.alias.split("/");
        for(int i = 0;i<strings.length;i++){
            list.add(strings[i]);
        }
        WikiAlias wikiAlias = new WikiAlias();
        wikiAlias.Alias(verifying.wikiId,verifying.title,list);

        WikiReference wikiReference = new WikiReference(verifying.wikiId,verifying.title);
        wikiReference.move(verifyingReferenceArrayList);

        verifying.delete();
        for(int i = verifyingReferenceArrayList.size()-1;i >= 0; i--){
            if(verifyingReferenceArrayList.get(i).verifyId == id){
                verifyingReferenceArrayList.get(i).delete();
            }
        }



    }


    public void reject(String admin,long id){
        DAOBase[] contents = Verifying.query().where("id = '" + id + "'");
        Verifying verifying = (Verifying)contents[0];
        verifying.refused = 1;
        verifying.save();

        WikiMessage wikiMessage = new WikiMessage(admin,verifying.username,"您有新的反馈信息","管理员"+admin+"拒绝了你的修改申请");
        wikiMessage.send();
    }
}
