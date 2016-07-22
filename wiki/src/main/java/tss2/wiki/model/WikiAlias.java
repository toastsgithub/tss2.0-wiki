package tss2.wiki.model;

import tss2.wiki.dao.Alias;
import tss2.wiki.dao.core.DAOBase;
import tss2.wiki.util.StringUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/22.
 */
public class WikiAlias {


    /**
     * 重写别名列表
     *
     * @param title
     * @param arrayList
     */
    public void Alias(long entryid, String title, ArrayList arrayList) {
        String str = StringUtil.concatArray("/", arrayList);
        modifyAlias(entryid, title, str);
    }

    private  void modifyAlias(long entryid, String title, String allalias) {
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
     * 获取所有title和alias
     *
     * @return
     */
    public  ArrayList<String> getAlias() {
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



}
