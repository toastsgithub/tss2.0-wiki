package tss2.wiki.model;

import tss2.wiki.dao.Verifying;
import tss2.wiki.dao.core.DAOBase;

import java.util.ArrayList;

/**
 * Created by coral on 16-7-24.
 */
public class WikiVerifying {

    public WikiVerifying(){

    }

    public WikiVerifying(int verifyId) {
        DAOBase[] vs = Verifying.query().where(String.format("id = %d", verifyId));
        if (vs.length == 0) {
            dao = new Verifying();
        } else {
            dao = (Verifying) vs[0];
            dao.id = verifyId;
        }
    }

    public void setContent(long wikiId, String username, String title, String[] tags, String[] categories, String content, ArrayList alias) {
        DAOBase[] contents = Verifying.query().where("title = '" + title + "' and username = '" + username + "'");
        if (contents.length == 0) {
            dao = new Verifying();
            dao.title = title;
            dao.username = username;
            dao.contentPath = null;
            //addTitle(title);
        } else {
            dao = (Verifying) contents[0];
        }
        String path = String.format("verifying/%s/%s", title,username);
        WikiFile file = new WikiFile(path);
        file.setContent(content);
        String strCateg = "";
        for (String cat : categories) {
            if (!strCateg.equals("")) {
                strCateg += "/";
            }
            strCateg += cat;
        }
        String strTags = "";
        for (String tag : tags) {
            if (!strTags.equals("")) {
                strTags += "/";
            }
            strTags += tag;
        }
        String strAlias = "";
        for(Object tempalias : alias){
            if(!strAlias.equals("")){
                strAlias += "/";
            }
            strAlias += tempalias.toString();
        }
        dao.wikiId = wikiId;
        dao.username = username;
        dao.title = title;
        dao.tags = strTags;
        dao.categories = strCateg;
        dao.alias = strAlias;
        dao.contentPath = path;
        dao.refused = 0;
        dao.save();

        DAOBase[] contentS = Verifying.query().where("title = '" + title + "' and username = '" + username + "'");
        dao.id = Long.valueOf(contentS[0].get("id").toString());


    }

    public long getID(){
        return dao.id;
    }

    private Verifying dao;
}
