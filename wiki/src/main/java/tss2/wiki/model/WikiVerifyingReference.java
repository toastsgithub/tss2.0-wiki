package tss2.wiki.model;

import net.sf.json.JSONObject;
import tss2.wiki.dao.Reference;
import tss2.wiki.dao.VerifyingReference;
import tss2.wiki.dao.core.DAOBase;
import tss2.wiki.util.TimeUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/24.
 */
public class WikiVerifyingReference {

    public WikiVerifyingReference(long verifyId){
        this.verifyId = verifyId;
        dao = new ArrayList<>();
        DAOBase[] refs = VerifyingReference.query().where("verifyId = '" + verifyId + "'");
        for (DAOBase ref: refs) {
            dao.add((VerifyingReference) ref);
        }
    }

    public void modify(ArrayList verifyreference){
        changeToReference(verifyreference);
    }

    private void changeToReference(ArrayList verifyreference){
        ArrayList<VerifyingReference> referenceArrayList = new ArrayList<>();
        for(int i= 0 ; i < verifyreference.size(); i++ ){
            JSONObject jsonObject = JSONObject.fromObject(verifyreference.get(i));
            VerifyingReference temp = new VerifyingReference();
            temp.verifyId = this.verifyId;
            temp.name = jsonObject.getString("name");
            temp.url = jsonObject.getString("url");
            temp.timestamp = TimeUtil.getTimeStampByDate();
            referenceArrayList.add(temp);
        }
        modifyReference(referenceArrayList);
    }

    private void removeReference(){
        for (int i = 0; i < dao.size(); i++) {
            dao.get(i).delete();    // delete from the database
            dao.remove(i);          // delete from the list
            --i;
        }
    }

    private void modifyReference(ArrayList<VerifyingReference> referenceArrayList) {
        this.removeReference();
        for(int i = 0;i< referenceArrayList.size();i++){
            referenceArrayList.get(i).save();
            dao.add(referenceArrayList.get(i));
        }
    }

    private long verifyId;
    private ArrayList<VerifyingReference> dao;
}
