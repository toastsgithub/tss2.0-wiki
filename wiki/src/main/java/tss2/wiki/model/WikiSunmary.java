package tss2.wiki.model;

import tss2.wiki.dao.DAOBase;
import tss2.wiki.dao.Summary;

/**
 * Created by Administrator on 2016/7/10.
 */
public class WikiSunmary {
    public String getSummary(){
        DAOBase[] content = Summary.query().where("");
        String jsonString = content[0].get("summaryJO").toString();
        return jsonString;
    }

    public void setSummary(String jsonString){
        DAOBase[] content = Summary.query().where("");
        content[0].setValue("summaryJO",jsonString);
        content[0].save();
    }

}
