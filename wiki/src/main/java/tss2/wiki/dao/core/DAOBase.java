package tss2.wiki.dao.core;


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
     * Execute a query and wrap the result as an
     * {@code DAOBase} object, decided by which
     * class is being called.
     *
     * The method will try to fill every field of
     * the class. If there is no such field in the
     * DB table, or the type is not capable, this
     * field will be left {@code null};
     *
     * @param whereStatement where statement
     * @param options options, which will be concat after the main expression
     * @return the wrapped result.
     */
    public DAOBase[] where(String whereStatement, String options) {
        String query = "select * from " + getTableName();
        if (whereStatement != null && !whereStatement.equals("")) {
            query += " where(" + whereStatement + ")";
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
     * save the current DAO.
     * As the DAO is corresponding to an entry in
     * the database by the field {@code id}.
     *
     * If there is no such entry, it will create a
     * new entry in the database.
     *
     * Wlse this will overwrite the entry.
     *
     * This method can ben override.
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

    /**
     * Almost the same as {@code save()}, however this
     * method can specify the key that is used to find
     * equivalence.
     *
     * @param key the key used to find equivalence.
     */
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

    /**
     * Get table name. By default it is the current
     * class's simple name.
     * @return
     */
    public String getTableName() {
        return this.getClass().getSimpleName();
    }

    /**
     * delete the current data entry.
     */
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

    protected String getValueString() {
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
                        break;
                    case "boolean":
                    case "Boolean":
                        values += field.getName() + "=" + field.getBoolean(this) + "";
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return values;
    }
}
