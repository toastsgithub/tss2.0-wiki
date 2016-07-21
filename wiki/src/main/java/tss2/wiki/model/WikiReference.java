package tss2.wiki.model;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import tss2.wiki.dao.Reference;
import tss2.wiki.dao.core.DAOBase;
import tss2.wiki.util.TimeUtil;

import java.util.ArrayList;

/**
 * Created by coral on 16-7-18.
 */
public class WikiReference {

    public WikiReference(String title){
        this.title = title;
        dao = new ArrayList<>();
        DAOBase[] refs = Reference.query().where("title = '" + title + "'");
        for (DAOBase ref: refs) {
            dao.add((Reference) ref);
        }
    }

    public WikiReference(long entryid,String title) {
        this.title = title;
        this.entryid = entryid;
        loadReferences();

    }

    public void modify(ArrayList reference){
        changeToReference(reference);
    }

    private void loadReferences() {
        dao = new ArrayList<>();
        DAOBase[] refs = Reference.query().where("entryid = '" + entryid + "'");
        for (DAOBase ref: refs) {
            dao.add((Reference) ref);
        }
    }

    private void changeToReference(ArrayList reference){
        //JSONArray jsonArray = JSONArray.fromObject(reference);
        ArrayList<Reference> referenceArrayList = new ArrayList<>();
        for(int i= 0 ; i < reference.size(); i++ ){
            JSONObject jsonObject = JSONObject.fromObject(reference.get(i));
            Reference temp = new Reference();
            temp.entryid = entryid;
            temp.title = title;
            temp.name = jsonObject.getString("name");
            temp.url = jsonObject.getString("url");
            temp.websiteName = jsonObject.getString("websiteName");
            temp.timestamp = TimeUtil.getTimeStampByDate();
            referenceArrayList.add(temp);
        }
        modifyReference(referenceArrayList);
    }


    private void removeReference(){
        for (int i = 0; i < dao.size(); i++) {
            if (dao.get(i).title.equals(title)) {
                dao.get(i).delete();    // delete from the database
                dao.remove(i);          // delete from the list
                --i;
            }
        }
    }

    private void modifyReference(ArrayList<Reference> referenceArrayList) {
        this.removeReference();
        for(int i = 0;i< referenceArrayList.size();i++){
            referenceArrayList.get(i).save();
            dao.add(referenceArrayList.get(i));
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Reference> getDao() {
        return dao;
    }

    private long entryid;
    private String title;
    private ArrayList<Reference> dao;

}
