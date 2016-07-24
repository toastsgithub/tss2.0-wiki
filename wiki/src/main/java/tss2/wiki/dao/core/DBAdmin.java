package tss2.wiki.dao.core;


import com.sun.rowset.CachedRowSetImpl;
import tss2.wiki.dao.*;

import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 数据库管理类。负责一切sql操作。
 * <p>
 * Created by coral on 16-5-1.
 */
public class DBAdmin {

    private static final String localhost = "localhost", ali1 = "121.42.184.4", ali2 = "114.215.88.219", tx = "182.254.146.31";
    private static final String dbName = "tss2";
    private static final String URL = "jdbc:mysql://" + ali1 + "/" + dbName + "?useSSL=false&serverTimezone=UTC&characterEncoding=utf8";
    private static final String USER = "root";
    private static final String PASSWD = "root";
    private static Connection conn = null;
    private static Statement stmt = null;

    // register classes here
    private static Class[] tables = {
            Alias.class,
            Message.class,
            Reference.class,
            Session.class,
            Summary.class,
            Tag.class,
            User.class,
            User2Message.class,
            UpdateHistory.class,
            Verifying.class,
            WikiEntry.class,
    };

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            try {
                conn = DriverManager.getConnection(URL, USER, PASSWD);
                stmt = conn.createStatement();
                System.out.println("Successfully connected to " + URL);
                migrate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 自动添加类中已经定义的但是数据库中不存在的表.
     */
    public static void migrate() {
        System.out.println("make migrations...");
        ArrayList<String> tbls = getAvailableTables();
        for (Class clz : tables) {
            boolean contains = false;
            for (String tbl : tbls) {
                if (tbl.equals(clz.getSimpleName())) contains = true;
            }
            if (!contains) createTable(clz);
        }
        /*for (String table : tbls) {   // 删除未被定义的表
            boolean contains = false;
            for (Class clz : tables) {
                if (table.equals(clz.getSimpleName())) contains = true;
            }
            if (!contains) dropTable(table);
        }*/
        System.out.println("finished.");
    }

    public static Connection getConnection() {
        return conn;
    }

    public static void createTable(String name, Class table) {
        // generate field string
        String strFields = "";
        ArrayList<Field> fields = new ArrayList<>();
        Collections.addAll(fields, table.getFields());
        for (Field field : fields) {
            String type = getTypeName(field.getType().getSimpleName());
            if (!type.equals("")) {
                if (!strFields.equals("")) strFields += ", ";
                strFields += field.getName() + " " + type;
            }
        }

        try {
            if (conn.isClosed()) {
                conn = DriverManager.getConnection(URL, USER, PASSWD);
            }
            if (stmt.isClosed()) {
                stmt = conn.createStatement();
            }
            stmt.execute(String.format("create table if not EXISTS %s (%s, PRIMARY KEY (id));", name, strFields));
            stmt.execute(String.format("alter table %s modify id int(11) auto_increment;", name));
            stmt.execute(String.format("alter table %s modify id int(11) default 1;", name));
            stmt.execute(String.format("alter table %s modify id int(11) auto_increment;", name));
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.printf("create table if not EXISTS %s (%s, PRIMARY KEY (id));%n", name, strFields);
            return;
        }
        System.out.println("+ " + name);
    }

    public static void createTable(Class table) {
        createTable(table.getSimpleName(), table);
    }

    /**
     * 删除一张表
     *
     * @param tableName
     */
    public static void dropTable(String tableName) {
        try {
            if (conn.isClosed()) {
                conn = DriverManager.getConnection(URL, USER, PASSWD);
            }
            if (stmt.isClosed()) {
                stmt = conn.createStatement();
            }
            stmt.execute(String.format("drop table if exists %s;", tableName));
            System.out.println("- " + tableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 新增列
     *
     * @param table
     * @param field
     */
    public static void addColumn(String table, Field field) {
        try {
            if (conn.isClosed()) {
                conn = DriverManager.getConnection(URL, USER, PASSWD);
            }
            if (stmt.isClosed()) {
                stmt = conn.createStatement();
            }
            stmt.execute(String.format("alter table %s add column %s %s;", table, field.getName(), getTypeName(field.getType().getSimpleName())));
            System.out.printf("+ %s.field %s%n", table, getTypeName(field.getType().getSimpleName()));
        } catch (SQLException e) {
            //e.printStackTrace();
        }
    }

    public static void addColumn(String table, String field, String type) {
        try {
            if (conn.isClosed()) {
                conn = DriverManager.getConnection(URL, USER, PASSWD);
            }
            if (stmt.isClosed()) {
                stmt = conn.createStatement();
            }
            stmt.execute(String.format("alter table %s add column %s %s;", table, field, getTypeName(type)));
        } catch (SQLException e) {
            //e.printStackTrace();
            return;
        }
        System.out.printf("+ %s.%s %s%n", table, field, getTypeName(type));

    }

    public static void dropColumn(String table, String fieldName) {
        try {
            if (conn.isClosed()) {
                conn = DriverManager.getConnection(URL, USER, PASSWD);
            }
            if (stmt.isClosed()) {
                stmt = conn.createStatement();
            }
            stmt.execute(String.format("alter table %s drop column %s;", table, fieldName));
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        System.out.printf("- %s.field%n", table);
    }

    public static String[] getFields(String table) {
        ResultSet rs = null;
        ArrayList<String> arrField = new ArrayList<>();
        try {
            if (conn.isClosed()) {
                conn = DriverManager.getConnection(URL, USER, PASSWD);
            }
            if (stmt.isClosed()) {
                stmt = conn.createStatement();
            }
            rs = stmt.executeQuery(String.format("desc %s;", table));
            while (rs.next()) {
                arrField.add(rs.getString("Field"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String[] res = new String[arrField.size()];
        return arrField.toArray(res);
    }

    private static String getTypeName(String type) {
        switch (type) {
            case "long":
            case "Long":
            case "Integer":
            case "int":
                type = "int(11)";
                break;
            case "Character":
            case "char":
                type = "char(1)";
                break;
            case "[C":
            case "String":
                type = "text";
                break;
            case "double":
            case "Double":
            case "Float":
            case "float":
                type = "decimal(20, 10)";
                break;
            case "Date":
                type = "date";
                break;
            default:
                type = "";
        }
        return type;
    }


    public static ArrayList<String> getAvailableTables() {
        // get available tables
        ArrayList<String> tbls = new ArrayList<>();
        ResultSet rs;
        try {
            if (conn.isClosed()) {
                conn = DriverManager.getConnection(URL, USER, PASSWD);
            }
            if (stmt.isClosed()) {
                stmt = conn.createStatement();
            }
            rs = stmt.executeQuery("SHOW TABLES;");
            while (rs.next()) {
                tbls.add(rs.getString("Tables_in_" + dbName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tbls;
    }

    public static RowSet query(String sqlQuery) {
        try {
            if (conn.isClosed()) {
                conn = DriverManager.getConnection(URL, USER, PASSWD);
            }
            Statement stmt = conn.createStatement();
            CachedRowSet rs = new CachedRowSetImpl();
            ResultSet s = stmt.executeQuery(sqlQuery);

            rs.populate(s);
            s.close();
            stmt.close();
            return rs;

        } catch (SQLException e) {
            System.err.println(sqlQuery);
            e.printStackTrace();
            return null;
        }
    }

    public static void execute(String sqlExecute) {
        try {
            if (conn.isClosed()) {
                conn = DriverManager.getConnection(URL, USER, PASSWD);
            }
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sqlExecute);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(sqlExecute);
        }
    }

    public static void main(String[] args) {

    }
}
