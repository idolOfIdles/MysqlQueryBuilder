package safayat.orm.jdbcUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by safayat on 10/26/18.
 */
public class PrimaryKeyInfo {

    private Map<String,Class> primaryKeyDbTypeByName;
    private Map<String,Class> primaryKeyClassTypeByName;
    private String  tableName;
    private String databaseName;
    private boolean  isAutoIncrement;

    public PrimaryKeyInfo(String tableName, String databaseName) {
        this.tableName = tableName;
        this.databaseName = databaseName;
        primaryKeyDbTypeByName = new HashMap<>();
        primaryKeyClassTypeByName = new HashMap<>();
    }

    public Map<String, Class> getPrimaryKeyDbTypeByName() {
        return primaryKeyDbTypeByName;
    }

    public void setPrimaryKeyDbTypeByName(Map<String, Class> primaryKeyDbTypeByName) {
        this.primaryKeyDbTypeByName = primaryKeyDbTypeByName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public boolean isAutoIncrement() {
        return isAutoIncrement;
    }

    public void setIsAutoIncrement(boolean isAutoIncrement) {
        this.isAutoIncrement = isAutoIncrement;
    }

    public Map<String, Class> getPrimaryKeyClassTypeByName() {
        return primaryKeyClassTypeByName;
    }

    public void setPrimaryKeyClassTypeByName(Map<String, Class> primaryKeyClassTypeByName) {
        this.primaryKeyClassTypeByName = primaryKeyClassTypeByName;
    }

    public String[] getPrimaryKeys() {
        return primaryKeyDbTypeByName.keySet().toArray(new String[0]);
    }

    public List<String> getPrimaryKeysAsList() {
        String[] primaryKeys =  getPrimaryKeys();
        List<String> primaryKeyList = new ArrayList<>();
        for (String key : primaryKeys)primaryKeyList.add(key);
        return primaryKeyList;
    }

    public String getSinglePrimaryKey() {
        return (String) primaryKeyDbTypeByName.keySet().toArray()[0];
    }

    public void addPrimaryKey(String key, Class type) {
         primaryKeyDbTypeByName.put(key, type);
    }

    public Class getKeyType(String key) {
         return primaryKeyDbTypeByName.get(key);
    }

    public void addClassPrimaryKey(String key, Class type) {
         primaryKeyClassTypeByName.put(key, type);
    }

    public Class getClassKeyType(String key) {
         return primaryKeyClassTypeByName.get(key);
    }
}
