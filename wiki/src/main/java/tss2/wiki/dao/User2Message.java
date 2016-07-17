package tss2.wiki.dao;

import tss2.wiki.dao.core.DAOBase;
import tss2.wiki.dao.core.DBAdmin;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by coral on 16-7-15.
 */
public class User2Message extends DAOBase {
    public String fromUser;
    public String toUser;
    public String messageID;
    public int isread;

    public static User2Message query() {
        return new User2Message();
    }

    @Override
    public void save() {
        String keystring = "toUser = '" + toUser + "' and " +
                "messageID = '" + messageID + "'" ;
        ResultSet rs = DBAdmin.query("select * from " + getTableName() + " where (" + keystring + ");");
        String sql = "";
        try {
            String values = getValueString();
            if (!rs.next()) {
                sql = "insert into " + getTableName() + " set " + values + ";";
                DBAdmin.execute(sql);
            } else {
                sql = "update " + getTableName() + " set " + values + " where(" + keystring + ");";
                DBAdmin.execute(sql);
            }
        } catch (SQLException e) {
            System.err.println(sql);
            e.printStackTrace();
        }
    }
}
