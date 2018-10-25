package dao;

import annotation.ManyToOne;
import annotation.OneToMany;
import model.Desk;
import model.student;
import queryBuilder.MysqlQuery;
import util.Util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

/**
 * Created by safayat on 10/20/18.
 */
public class CommonDAO {

    private String dbUserName = "root";

    private String dbPassword = "";

        private String dbName  = "rssdesk";
//    private String dbName  = "schoolmanagement";

        private String dbUrl = "jdbc:mysql://localhost:3306/rssdesk?useSSL=false";
//    private String dbUrl = "jdbc:mysql://localhost:3306/schoolmanagement";



    private Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;

    }

    private List<String> getPrimaryKeys(String table, Connection connection) {


        List<String> primaryKeys = new ArrayList<String>();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet rs = metaData.getPrimaryKeys(dbName, null, table);
            while (rs.next()){
                primaryKeys.add(rs.getString("column_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return primaryKeys;

    }


    public List<student> getAllstudents() throws Exception{

        Connection dbConnection = null;
        PreparedStatement statement = null;
        List<student> students = new ArrayList<student>();
        try {
            MysqlQuery sqlQuery = MysqlQuery.get("dk.name, us.*")
                    .table("student", "dk")
                    .leftJoin("User", "us").on("us.id", "dk.user_id")
                    .getQuery();

            String sql = sqlQuery.toString();

            dbConnection = getConnection();
            statement = dbConnection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();


            Map<String, Boolean> methodMap = new HashMap<String, Boolean>();
            Map<String, Method> methodByName = new HashMap<String, Method>();
            Method[] methods = student.class.getDeclaredMethods();
            for(Method m : methods){
                methodMap.put(m.getName(), true);
                methodByName.put(m.getName(), m);
            }

            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            int columnCount = resultSetMetaData.getColumnCount();
            while(rs.next()){
                student student = new student();

                for(int i=1;i<=columnCount;i++){
                    String columnName = resultSetMetaData.getColumnName(i);
                    String tableName  = resultSetMetaData.getTableName(i);
                    if(tableName.equalsIgnoreCase(student.getClass().getSimpleName())){
                        String getMethodName = Util.toJavaMethodName(columnName, "set");
                        if(methodMap.containsKey(getMethodName)){
                            Method columnMethod = methodByName.get(getMethodName);
                            Object ob = null;
                            try{
                                ob = rs.getObject(i);
                            }catch (Exception e){
                            }
                            columnMethod.invoke(student, ob);
                        }
                    }
                }

                students.add(student);

            }


        } catch (SQLException e) {
            e.printStackTrace();
            if(statement!=null){
                try {
                    statement.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            if(dbConnection!=null){
                try {
                    dbConnection.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return students;
    }


    public <T> List<T> getData(Class<T> clazz, ResultSet resultSet) throws Exception{

        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        Set<String> tableSet = new HashSet<String>();
        Map<String, Annotation> annotationByTable = new HashMap<String, Annotation>();
        String parentClassUniqueField = "";
        List<Annotation> annotationList = Util.getMethodAnnotations(clazz);
        for(Annotation annotation : annotationList){
            if( annotation instanceof OneToMany){
                OneToMany oneToMany = (OneToMany) annotation;
                String type = oneToMany.getClass().getSimpleName();
                parentClassUniqueField = oneToMany.outer();
                annotationByTable.put(type, annotation);
            }
            if( annotation instanceof ManyToOne){
                ManyToOne manyToOne = (ManyToOne)annotation;
                String type = manyToOne.getClass().getSimpleName();
                annotationByTable.put(type, annotation);
                if(parentClassUniqueField.isEmpty()){
                    parentClassUniqueField = manyToOne.outer();
                }
            }
        }

        if(parentClassUniqueField.isEmpty()) return null;
        int parentClassUniqueFieldIndex = 0;
        int columnCount = resultSetMetaData.getColumnCount();
        for(int i=1;i<=columnCount;i++){
            if(clazz.getSimpleName().equalsIgnoreCase(resultSetMetaData.getTableName(i))) continue;
            tableSet.add(resultSetMetaData.getTableName(i));
            if(resultSetMetaData.getColumnName(i).equalsIgnoreCase(parentClassUniqueField)){
                parentClassUniqueFieldIndex = i;
            }
        }

        if(parentClassUniqueFieldIndex == 0) return null;

        Map<Object, Class<T>> parentMap = new HashMap<Object, Class<T>>();
        while (resultSet.next()){

            Object uniqueFieldValue = resultSet.getObject(parentClassUniqueFieldIndex);
            Class<T> parent = parentMap.get(uniqueFieldValue);
            boolean duplicate = true;
            if(parent == null ){
                parent = clazz.getClass().newInstance();
                parentMap.put(uniqueFieldValue, parent);
                duplicate = false;
            }

            for(int i=1;i<=columnCount;i++){
                String columnName = resultSetMetaData.getColumnName(i);
                String tableName = resultSetMetaData.getTableName(i);
                if(tableName.equalsIgnoreCase(clazz.getSimpleName())){
                    if(duplicate) continue;
                    String methodName = Util.toJavaMethodName(columnName,"set");
                    Method method = clazz.getDeclaredMethod(methodName, Util.getClassByType(resultSetMetaData.getColumnType(i)));
                    method.invoke(parent, resultSet.getObject(i));
                }else {
                    Annotation innerAnnotation = annotationByTable.get(tableName);
                    if(innerAnnotation instanceof OneToMany){
                        OneToMany oneToMany = (OneToMany)innerAnnotation;
                        Method method = clazz.getDeclaredMethod(Util.toJavaMethodName(oneToMany.name(),"get"));
                        List<Object> data = (List<Object>)method.invoke(parent);
                    }
                }
            }



        }











        return null;
    }


}
