package tss2.wiki.model;

import tss2.wiki.dao.DAOBase;
import tss2.wiki.dao.UpdateHistory;
import tss2.wiki.dao.WikiEntry;
import tss2.wiki.util.FileLoader;
import tss2.wiki.util.TimeUtil;

/**
 *
 * Created by 羊驼 on 2016/7/10.
 */
public class WikiRecord {

    public static final String FILE_PATH_PREFIX = "/wikimd/";
    public static final String FILE_PATH_SUFFIX = ".md";

    public WikiRecord(String title) {
        setTitle(title);
        DAOBase[] contents = WikiEntry.query().where("title = '" + title + "");
        if (contents.length == 0) {
            dao = new WikiEntry();
            dao.title = title;
        } else {
            dao = (WikiEntry) contents[0];
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return dao.title;
    }

    public String getMdContent() {
        String path = dao.contentPath;
        return FileLoader.loadStringFromAbsolutePath(path);
    }

    public String[] getCategories() {
        String categories = dao.categories;
        return categories.split("[/]");
    }

    public String[] getTags() {
        String tags = dao.tags;
        return tags.split("[/]");
    }

    public String getCurrVersionString() {
        return dao.mainversion + "." + dao.subversion;
    }

    public String getContentByVersion(String version) {
        int mainversion = Integer.valueOf(version.split("[.]")[0]);
        int subversion = Integer.valueOf(version.split("[.]")[1]);
        DAOBase[] updatehistory = UpdateHistory.query().where("title = '" + dao.title + "' and mainversion = "
                + mainversion + " and subversion = " + subversion);
        if (updatehistory.length == 0) {
            return null;
        } else {
            String path = ((UpdateHistory) updatehistory[0]).contentPath;
            return FileLoader.loadStringFromFile(path);
        }
    }

    /**
     * 更新条目相关信息。并且记录更新历史。
     *
     * @param username      ..
     * @param categories    ..
     * @param tags          ..
     * @param summary       ..
     * @param content       ..
     * @param isSubVersion  ..
     */
    public void setContent(String username, String[] categories, String[] tags, String summary, String content, boolean isSubVersion) {
        String versionStr = getCurrVersionString();
        int mainversion = dao.mainversion;
        int subversion = dao.subversion;
        if (isSubVersion) {
            subversion += 1;
        }
        else {
            mainversion += 1;
        }
        String path = FILE_PATH_PREFIX + getTitle() + "/" + mainversion + "." + subversion + FILE_PATH_SUFFIX;
        String strCateg = "";
        for (String cat: categories) {
            if (!strCateg.equals("")) {
                strCateg += "/";
            }
            strCateg += cat;
        }
        String strTags = "";
        for (String tag: tags) {
            if (!strTags.equals("")) {
                strTags += "/";
            }
            strTags += tag;
        }

        // add update history
        UpdateHistory history = new UpdateHistory();
        history.contentPath = path;
        history.title = dao.title;
        history.tags = strTags;
        history.categories = strCateg;
        history.summary = summary;
        history.timestamp = TimeUtil.getTimeStampBySecond();
        history.mainversion = mainversion;
        history.subversion = subversion;
        history.username = username;
        history.save();

        // update wiki entry
        dao.subversion = subversion;
        dao.mainversion = mainversion;
        dao.categories = strCateg;
        dao.tags = strTags;
        dao.summery = summary;
        dao.contentPath = path;
        dao.save();

        // write content to file system
        FileLoader.writeStringToFile(path, content);
    }

    public void addVisit() {
        ++dao.visits;
        dao.save();
    }

    private WikiEntry dao;
    private String title;
}
