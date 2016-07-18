package tss2.wiki.model;

import tss2.wiki.dao.Reference;
import tss2.wiki.dao.core.DAOBase;

import java.util.ArrayList;

/**
 * Created by coral on 16-7-18.
 */
public class WikiReference {

    public WikiReference(String title) {
        loadReferences();
    }

    public void addReference(String name, String url, String website) {
        Reference reference = new Reference();
        reference.name = name;
        reference.url = url;
        reference.websiteName = website;
        reference.title = getTitle();
        reference.save();
        dao.add(reference);
    }

    private void loadReferences() {
        dao = new ArrayList<>();
        DAOBase[] refs = Reference.query().where("title = '" + title + "'");
        for (DAOBase ref: refs) {
            dao.add((Reference) ref);
        }
    }

    public void removeReference(String name) {
        for (int i = 0; i < dao.size(); i++) {
            if (dao.get(i).name.equals(name)) {
                dao.get(i).delete();    // delete from the database
                dao.remove(i);          // delete from the list
                --i;
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;
    private ArrayList<Reference> dao;
}
