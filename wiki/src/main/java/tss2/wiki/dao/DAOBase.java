package tss2.wiki.dao;


import tss2.wiki.dao.core.DBAdmin;

import javax.sql.RowSet;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Base of all DAOs. Every entity subscribe to a
 * record in the table that the class holding to.
 *
 * The name of the table that the class holding to
 * is decided by the method <code>getTableName</code>.
 * By default, it is the class's simple name.
 *
 * The DAOs provides basic query function which a sql
 * query like <code>'select * from A where (...)'</code>
 * can provide. To execute more complicated query function,
 * use {@Code DBAdmin.query()}
 *
 * @see DBAdmin
 * Created by coral on 16-5-2.
 */
public abstract class DAOBase {

    public int id;

    public DAOBase() { }

    public DAOBase[] where(String whereStatment) {
        return where(whereStatment, "");
    }



    /**
     * 执行查询，并将结果包装为对象。
     *
     * @param whereStatment where条件语句
     * @param options 附加的条件语句，将会被直接拼接到where后面
     * @return 查询的结果。
     */
    public DAOBase[] where(String whereStatment, String options) {
        String query = "select * from " + getTableName();
        if (whereStatment != null && !whereStatment.equals("")) {
            query += " where(" + whereStatment + ")";
        }
        if (options != null && !options.equals("")) {
            query += options + ";";
        }
        return query(query);
    }

    /**
     * set the value of the field with the assigned value.
     * This method throws no exceptions nor return anything,
     * thus even if the setting ends in failure it will
     * do nothing to notify you, neither it will crash.
     *
     * @param key the name of the field
     * @param value the value you want to set
     */
    public void setValue(String key, Object value) {
        Field field = null;
        try {
            field = getClass().getField(key);
            field.set(this, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * executing a sql query string and return wrapped
     * objects.
     *
     * @param qs sql expression
     * @return wrapped objects
     */
    public DAOBase[] query(String qs) {
        RowSet rs =  DBAdmin.query(qs);
        ArrayList<DAOBase> result = new ArrayList<>();
        if (rs == null){
            return result.toArray(new DAOBase[0]);
        }
        try{
            while (rs.next()) {
                DAOBase tmp = this.getClass().newInstance();
                for (Field field: getClass().getFields()) {
                    try {
                        switch (field.getType().getSimpleName()) {
                            case "Integer":
                            case "int":
                                field.setInt(tmp, rs.getInt(field.getName()));
                                break;
                            case "Character":
                            case "char":
                                field.setChar(tmp, (char) rs.getShort(field.getName()));
                                break;
                            case "String":
                                field.set(tmp, rs.getString(field.getName()));
                                break;
                            case "double":
                            case "Double":
                            case "Float":
                            case "float":
                                field.setDouble(tmp, rs.getDouble(field.getName()));
                                break;
                            case "Date":
                                field.set(tmp, rs.getDate(field.getName()));
                        }
                    } catch (SQLException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                result.add(tmp);
            }
            rs.close();
        } catch (SQLException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return result.toArray(new DAOBase[result.size()]);
    }

    /**
     * 保存当前数据项。如果已经存在于表中，则把原数据覆盖为最新值。
     */
    public void save() {
        ResultSet rs = DBAdmin.query("select * from " + getTableName() + " where (id = " + this.get("id") + ");");
        String sql = "";
        try {
            String values = getValueString();
            if (!rs.next()) {
                sql = "insert into " + getTableName() + " set " + values + ";";
                DBAdmin.execute(sql);
            } else {
                sql = "update " + getTableName() + " set " + values + " where id = " + id + ";";
                DBAdmin.execute(sql);
            }
        } catch (SQLException e) {
            System.err.println(sql);
            e.printStackTrace();
        }
    }

    public void save(String key) {
        ResultSet rs = DBAdmin.query("select * from " + getTableName() + " where (" + key + " = '" + this.get(key) + "');");
        String sql = "";
        try {
            String values = getValueString();
            if (!rs.next()) {
                sql = "insert into " + getTableName() + " set " + values + ";";
                DBAdmin.execute(sql);
            } else {
                sql = "update " + getTableName() + " set " + values + " where " + key + " = '" + this.get(key) + "';";
                DBAdmin.execute(sql);
            }
        } catch (SQLException e) {
            System.err.println(sql);
            e.printStackTrace();
        }
    }

    public String getTableName() {
        return this.getClass().getSimpleName();
    }

    public void delete() {
        DBAdmin.execute("delete from " + getTableName() + " where (id = " + this.id + ");");
    }

    public Object get(String key) {
        try {
            Field field = this.getClass().getField(key);
            return field.get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getValueString() {
        String values = "";
        ArrayList<Field> fields = new ArrayList<>();
        Collections.addAll(fields, this.getClass().getFields());
        for (Field field : fields) {
            if (!values.equals("")) values += ", ";
            try {
                switch (field.getType().getSimpleName()) {
                    case "Integer":
                    case "int":
                        values += field.getName() + '=' + field.getInt(this);
                        break;
                    case "Character":
                    case "char":
                        values += field.getName() + '=' + "'" + ((char) field.getShort(this)) + "'";
                        break;
                    case "[C":
                    case "String":
                        values += field.getName() + '=' + "'" + field.get(this) + "'";
                        break;
                    case "double":
                    case "Double":
                    case "Float":
                    case "float":
                        values += field.getName() + '=' + field.getDouble(this);
                        break;
                    case "Date":
                        values += field.getName() + '=' + "'" + field.get(this).toString() + "'";
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return values;
    }
}
