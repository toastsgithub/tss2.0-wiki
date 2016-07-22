package tss2.wiki.model;

import tss2.wiki.dao.Alias;
import tss2.wiki.dao.UpdateHistory;
import tss2.wiki.dao.WikiEntry;
import tss2.wiki.dao.core.DAOBase;
import tss2.wiki.dao.core.DBAdmin;
import tss2.wiki.domain.RecordsResult;
import tss2.wiki.domain.TitleAndSummary;
import tss2.wiki.util.FileUtil;
import tss2.wiki.util.StringUtil;
import tss2.wiki.util.TimeUtil;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Wiki条目的model
 * <p>
 * Created by 羊驼 on 2016/7/10.
 */
public class WikiRecord {

    public static final String FILE_PATH_PREFIX = "wikimd/";
    public static final String FILE_PATH_SUFFIX = ".md";

    private WikiRecord() {
        File fp = new File(FILE_PATH_PREFIX);
        if (!fp.exists())
            fp.mkdirs();
    }

    public WikiRecord(String title) {
        this();
        title = getTitleFromAlias(title);//重定向title
        setTitle(title);
        DAOBase[] contents = WikiEntry.query().where("title = '" + title + "'");
        if (contents.length == 0) {
            dao = new WikiEntry();
            dao.title = title;
            dao.contentPath = null;
            //addTitle(title);
        } else {
            dao = (WikiEntry) contents[0];
        }
    }

    /**
     * 根据category获取条目title和summary
     *
     * @param categories
     * @return
     */
    public static ArrayList<TitleAndSummary> getContentByCategories(String categories) {
        DAOBase[] contents = WikiEntry.query().where("categories = '" + categories + "'");
        ArrayList<TitleAndSummary> result = new ArrayList<>();
        for (int i = 0; i < contents.length; i++) {
            result.add(new TitleAndSummary(((WikiEntry) contents[i]).title, ((WikiEntry) contents[i]).summery));
        }
        return result;
    }

    /**
     * 关键字搜索
     *
     * @param search
     * @return
     */
    public static RecordsResult recordFuzzySearch(String search) {
        search = getTitleFromAlias(search);
        System.out.println("search is: "+search);
        DAOBase[] title = WikiEntry.query().where("title like '%" + search + "%'");
        DAOBase[] tags = WikiEntry.query().where("tags like '%" + search + "%'");
        DAOBase[] categories = WikiEntry.query().where("categories like '%" + search + "%'");
        DAOBase[] summery = WikiEntry.query().where("summery like '%" + search + "%'");

        DAOBase[] all = WikiEntry.query().where("");
        ArrayList<WikiEntry> alllist = new ArrayList<>();
        for (int i = 0; i < all.length; i++) {
            alllist.add((WikiEntry) all[i]);
        }
        alllist = qsort(alllist);


        ArrayList<WikiEntry> result = new ArrayList<>();
        ArrayList<WikiEntry> R1 = new ArrayList<>();
        ArrayList<WikiEntry> R2 = new ArrayList<>();
        ArrayList<WikiEntry> R3 = new ArrayList<>();
        for (int i = 0; i < title.length; i++) {
            result.add((WikiEntry) title[i]);
        }
        result = qsort(result);
        System.out.println("size after title: "+result.size());
        for (int i = 0; i < tags.length; i++) {
            if (contain(result, (WikiEntry) tags[i])) {
            } else {
                R1.add((WikiEntry) tags[i]);
            }
        }
        for (int i = 0; i < categories.length; i++) {
            if (contain(result, (WikiEntry) categories[i]) || contain(R1, (WikiEntry) categories[i])) {
            } else {
                R2.add((WikiEntry) categories[i]);
            }
        }
        for (int i = 0; i < R2.size(); i++) {
            R1.add(R2.get(i));
        }
        R1 = qsort(R1);
        for (int i = 0; i < R1.size(); i++) {
            result.add(R1.get(i));
        }
        System.out.println("size after categories and tags: "+result.size());

        for (int i = 0; i < summery.length; i++) {
            if (contain(result, (WikiEntry) summery[i])) {
            } else {
                R3.add((WikiEntry) summery[i]);
            }
        }
        R3 = qsort(R3);
        for (int i = 0; i < R3.size(); i++) {
            result.add(R3.get(i));
        }
        System.out.println("size after summery: "+result.size());
        for (int i = 0; i < alllist.size(); i++) {
            if (search(alllist.get(i).contentPath, search)) {
                if (contain(result, alllist.get(i))) {
                    System.out.println("----");
                } else {
                    result.add(alllist.get(i));
                }
            }
        }
        //result = qsort(result);
        return new RecordsResult(1, result);
    }

    private static boolean contain(ArrayList<WikiEntry> wikiEntryArrayList, WikiEntry wikiEntry) {
        for (int i = 0; i < wikiEntryArrayList.size(); i++) {
            if (wikiEntryArrayList.get(i).title.equals(wikiEntry.title)) {
                return true;
            }
        }
        return false;
    }

    private static boolean search(String path, String search) {
        //WikiRecord wikiRecord = new WikiRecord(wikiEntry.title);
        String content = FileUtil.loadStringFromAbsolutePath(path);
        //System.out.println("path:"+path);
        //System.out.println("content:"+content);
        if (content == null) {
            return false;
        }
        return content.contains(search);
    }

    private static ArrayList<WikiEntry> qsort(ArrayList<WikiEntry> wikiEntryArrayList) {
        int l = wikiEntryArrayList.size();
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < l - i - 1; j++) {
                if (Integer.parseInt(wikiEntryArrayList.get(j).get("visits").toString()) < (Integer.parseInt(wikiEntryArrayList.get(j + 1).get("visits").toString()))) {
                    WikiEntry wikiEntry = wikiEntryArrayList.get(j + 1);
                    wikiEntryArrayList.remove(j + 1);
                    wikiEntryArrayList.add(j + 1, wikiEntryArrayList.get(j));
                    wikiEntryArrayList.remove(j);
                    wikiEntryArrayList.add(j, wikiEntry);
                }
            }
        }
        return wikiEntryArrayList;
    }

    /**
     * 在传入title时调用，将title/alias转化为title
     *
     * @param alias
     * @return
     */
    public static String getTitleFromAlias(String alias) {
        DAOBase[] contents = Alias.query().where("alias like '%" + alias + "%'");
        ArrayList<Alias> aliasArrayList = new ArrayList<>();
        for (int i = 0; i < contents.length; i++) {
            aliasArrayList.add((Alias) contents[i]);
        }
        if (aliasArrayList.size() == 0) {

            return alias;
        }
        for (int i = 0; i < aliasArrayList.size(); i++) {
            String[] str = aliasArrayList.get(i).alias.split("/");
            for (int j = 0; j < str.length; j++) {
                if (str[j].equals(alias)) {
                    return aliasArrayList.get(i).title;
                }
            }
        }
        return alias;
    }


    /**
     * 重写别名列表
     *
     * @param title
     * @param arrayList
     */
    public static void Alias(long entryid, String title, ArrayList arrayList) {
        String str = StringUtil.concatArray("/", arrayList);
        modifyAlias(entryid, title, str);
    }

    private static void modifyAlias(long entryid, String title, String allalias) {
        DAOBase[] contents = Alias.query().where("entryid = '" + entryid + "'");
        if (contents.length == 0) {
            Alias alias = new Alias();
            alias.entryid = entryid;
            alias.title = title;
            alias.alias = title + "/" + allalias;
            alias.save();
        } else {
            Alias alias = (Alias) contents[0];
            alias.entryid = entryid;
            alias.title = title;
            alias.alias = title + "/" + allalias;
            alias.save();
        }
    }

    /**
     * 在别名列表里添加新的一项
     *
     * @param title
     */
    public static void addTitle(long entryid, String title) {
        DAOBase[] contents = Alias.query().where("entryid = '" + entryid + "'");
        if (contents.length == 0) {
            Alias alias1 = new Alias();
            alias1.entryid = entryid;
            alias1.title = title;
            alias1.alias = title;
            alias1.save();
        } else {
            Alias alias1 = (Alias) contents[0];
            alias1.entryid = entryid;
            alias1.title = title;
            alias1.alias = title;
            alias1.save();
        }
    }

    /**
     * 获取所有title和alias
     *
     * @return
     */
    public static ArrayList<String> getAlias() {
        DAOBase[] contents = Alias.query().where("");
        ArrayList<String> aliaslists = new ArrayList<>();
        for (int i = 0; i < contents.length; i++) {
            Alias temp = (Alias) contents[i];
            String[] str = temp.alias.split("[/]");
            for (int j = 0; j < str.length; j++) {
                aliaslists.add(str[j]);
            }
        }
        return aliaslists;
    }

    public WikiRecord(String title, int mainversion, int subversion) {

    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return dao.title;
    }

    public String getContent() {
        String path = dao.contentPath;
        if (dao.contentPath == null) return null;
        return FileUtil.loadStringFromAbsolutePath(path);
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
            return FileUtil.loadStringFromFile(path);
        }
    }

    /**
     * 更新条目相关信息。并且记录更新历史。
     *
     * @param username     ..
     * @param categories   ..
     * @param tags         ..
     * @param summary      ..
     * @param content      ..
     * @param isSubVersion ..
     */
    public void setContent(long entryid, String username, String[] categories, String[] tags, String summary, String content, boolean isSubVersion) {
        String versionStr = getCurrVersionString();

        DAOBase[] contents = WikiEntry.query().where("id = '" + entryid + "'");
        if (contents.length == 0) {
            dao = new WikiEntry();
            dao.title = title;
            dao.contentPath = null;
            //addTitle(title);
        } else {
            dao = (WikiEntry) contents[0];
        }

        int mainversion = dao.mainversion;
        int subversion = dao.subversion;
        if (isSubVersion) {
            subversion += 1;
        } else {
            mainversion += 1;
        }
        String path = FILE_PATH_PREFIX + getTitle() + "/" + mainversion + "." + subversion + FILE_PATH_SUFFIX;
        File fp = new File(FILE_PATH_PREFIX + getTitle() + "/");
        if (!fp.exists()) {
            boolean result = fp.mkdirs();
        }
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


        // update wiki entry
        dao.subversion = subversion;
        dao.mainversion = mainversion;
        dao.categories = strCateg;
        dao.tags = strTags;
        dao.summery = summary;
        dao.contentPath = path;
        dao.save();

        if (dao.id == 0) {
            DAOBase[] daos = WikiEntry.query().where("title = '" + dao.title + "' and mainversion = " + mainversion + " and subversion = " + subversion);
            dao.id = Long.valueOf(daos[0].get("id").toString());
        }

        history.entryid = dao.id;
        history.save();

        addTitle(dao.id, title);
        // write content to file system
        WikiFile file = new WikiFile(path);
        file.setContent(content);
    }

    public void addVisit() {
        ++dao.visits;
        dao.save();
    }

    public String getDate() {
        ResultSet rs = DBAdmin.query("select max(timestamp) from UpdateHistory where (title = '" + getTitle() + "');");
        String date = null;
        try {
            if (rs.next()) {
                date = rs.getString("max(timestamp)");
                //System.out.println(rs.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(date);
        return date;
    }

    public String getEditor() {
        DAOBase[] contents = UpdateHistory.query().where("timestamp = '" + getDate() + "'");
        UpdateHistory updateHistory = (UpdateHistory) contents[0];
        return updateHistory.username;
    }

    public long getID() {
        return dao.id;
    }

    public ArrayList<String> getAliasByTitle() {
        DAOBase[] contents = Alias.query().where("title = '" + dao.title + "'");
        String[] str = ((Alias) contents[0]).alias.split("/");
        ArrayList<String> result = new ArrayList<>();
        if (str.length <= 1) {
            return null;
        }
        for (int i = 1; i < str.length; i++) {
            result.add(str[i]);
        }
        return result;
    }

    public int getVisits() {
        return dao.visits;
    }

    public String getSummery() {
        return dao.summery;
    }

    /**
     * 删除条目，同时删除别名列表里的相应项
     */
    public void delete() {
        dao.delete();
        DAOBase[] contents = Alias.query().where("title = '" + title + "'");
        Alias aliasDao = (Alias) contents[0];
        aliasDao.delete();
    }

    public static String polish(String content) {
        DAOBase[] wikis = WikiEntry.query().where("");
        for (DAOBase wiki : wikis) {
            char[] bytes = new char[wiki.get("title").toString().length()];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = wiki.get("title").toString().charAt(i);
            }
            String regex = '[' + StringUtil.concatArray("][", bytes) + ']';
            content = content.replaceFirst(wiki.get("title").toString(), "[" + wiki.get("title").toString() + "](/html/entry_content.html?entry=" + wiki.get("title").toString() + ")");
        }
        return content;
    }

    private WikiEntry dao;
    private String title;

    public static void main(String args[]) {
        //addTitieAlias("测试条目","abc");
        //System.out.println(getTitleFromAlias("abc"));
        WikiRecord wikiRecord = new WikiRecord("测试条目");
        System.out.println(wikiRecord.getDate());
        //wikiRecord.delete();
    }
}
