package tss2.wiki.model;

import tss2.wiki.dao.Verifying;
import tss2.wiki.dao.core.DAOBase;

/**
 * Created by coral on 16-7-24.
 */
public class WikiVerifying {

    public WikiVerifying(int verifyId) {
        DAOBase[] vs = Verifying.query().where(String.format("id = %d", verifyId));
        if (vs.length == 0) {
            dao = new Verifying();
        } else {
            dao = (Verifying) vs[0];
            dao.id = verifyId;
        }
    }

    public void setContent(int wikiId, int authorId, String title, String tags, String categories, String content) {
        dao.wikiId = wikiId;
        dao.authorid = authorId;
        dao.title = title;
        dao.tags = tags;
        dao.categories = categories;
        WikiFile file = new WikiFile(String.format("verifying/%d/%d", wikiId, authorId));
    }

    private Verifying dao;
}
