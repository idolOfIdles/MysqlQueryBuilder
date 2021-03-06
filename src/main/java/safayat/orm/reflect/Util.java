package safayat.orm.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.List;

/**
 * Created by safayat on 10/22/18.
 */
public class Util {

    public static DateFormat mysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String toCamelCase(String str){
        StringBuilder stringBuilder = new StringBuilder(str);
        int  ch = str.charAt(0);
        if(ch >= 'a'){
            stringBuilder.setCharAt(0, (char)(str.charAt(0) & 0x5f));
        }
        return stringBuilder.toString();

    }

    public static String classNameToTable(String str){
        return String.valueOf(str.charAt(0)).toLowerCase() + str.substring(1);

    }

    public static String mysqlFieldtoJavaVariableName(String str){
        String[] splitted = str.split("[_]+");
        StringBuilder stringBuilder = new StringBuilder(splitted[0]);
        for(int i=1;i<splitted.length;i++){
            stringBuilder.append(toCamelCase(splitted[1]));
        }
        return stringBuilder.toString();
    }

    public static String mysqlTabletoJavaClassName(String str){
        String camelCaseName = mysqlFieldtoJavaVariableName(str);
        return camelCaseName.substring(0,1).toUpperCase() + camelCaseName.substring(1);
    }
    public static String toJavaMethodName(String variableName, String prefix){
        return prefix + toTitle(variableName);
    }

    public static List<Annotation> getMethodAnnotations(Class clazz) {

        List<Annotation> list = new ArrayList<Annotation>();

        Method[] methods = clazz.getDeclaredMethods();
        for(Method m : methods){
            for (Annotation annotation : m.getDeclaredAnnotations()){
                list.add(annotation);
            }
        }

        return list;

    }

    public static List<Annotation> getFieldAnnotations(Class clazz) {

        List<Annotation> list = new ArrayList<Annotation>();

        Field[] fields = clazz.getDeclaredFields();
        for(Field f : fields){
            for (Annotation annotation : f.getDeclaredAnnotations()){
                list.add(annotation);
            }
        }

        return list;

    }

    public static List<Annotation> getFieldAnnotations(Class clazz, Class type) {

        List<Annotation> list = new ArrayList<Annotation>();

        Field[] fields = clazz.getDeclaredFields();
        for(Field f : fields){
            for (Annotation annotation : f.getDeclaredAnnotationsByType(type)){
                list.add(annotation);
            }
        }

        return list;

    }




    public static Class getClassByMysqlType(int type) {
        if(Types.BIGINT == type) return Long.class;
        if(Types.BINARY == type) return Boolean.class;
        if(Types.INTEGER == type) return Integer.class;
        if(Types.DATE == type) return Date.class;
        if(Types.TIMESTAMP == type) return Date.class;
        if(Types.TIME == type) return java.sql.Time.class;
        if(Types.BLOB == type) return java.sql.Blob.class;
        if(Types.FLOAT == type) return Float.class;
        if(Types.DOUBLE == type) return Double.class;
        if(Types.ARRAY == type) return Array.class;
        return String.class;
    }

    public static Class getFieldClass(Class clazz, String name) throws NoSuchFieldException {
        return clazz.getDeclaredField(name).getType();
    }

    public static Object castToSpecificType(Class type, String value) {
        if(type.getSimpleName().equalsIgnoreCase("int") || type.getSimpleName().equalsIgnoreCase(Integer.class.getSimpleName())){
            return Integer.parseInt(value);
        }
        if(type.getSimpleName().equalsIgnoreCase("long") || type.getSimpleName().equalsIgnoreCase(Long.class.getSimpleName())){
            return Long.parseLong(value);
        }
        if(type.getSimpleName().equalsIgnoreCase("float") || type.getSimpleName().equalsIgnoreCase(Float.class.getSimpleName())){
            return Float.parseFloat(value);
        }
        if(type.getSimpleName().equalsIgnoreCase("double") || type.getSimpleName().equalsIgnoreCase(Double.class.getSimpleName())){
            return Double.parseDouble(value);
        }
        if(type.getSimpleName().equalsIgnoreCase("byte") || type.getSimpleName().equalsIgnoreCase(Byte.class.getSimpleName())){
            return Byte.parseByte(value);
        }

        return null;

    }

    public static String methodToVariableName(String methodName) {
        int from = 0;
        if(methodName.startsWith("get")) from = 3;
        return String.valueOf(methodName.charAt(from)).toLowerCase() + methodName.substring(from+1);
    }

    public static String listAsString(List list) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i<list.size();i++){
            stringBuilder.append(list.get(i));
            if(i<list.size()-1) stringBuilder.append(",");
        }
        return stringBuilder.toString();
    }

    public static String toQuote(String str) {
        return "\"" + str + "\"";
    }

    public static String toMysqlString(Object ob) {

        if(ob instanceof Date){
            return toQuote(mysqlDateFormat.format((Date)ob));
        }

        if(ob == null){
            return "NULL";
        }

        return toQuote(ob.toString());
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }



    public static char upper(char c){
        return  c >= 'a' && c <= 'z' ? (char)(c - ( 'a'-'A')) : c;
    }

    public static char lower(char c){
        return  c >= 'A' && c <= 'Z' ? (char)( c + ( 'a'-'A')) : c;
    }

    public static String tableName(Class clazz){
        char[] chars = clazz.getSimpleName().toCharArray();
        chars[0] = lower(chars[0]);
        return new String(chars);
    }

    public static String toTitle(String str) {
        char[] chars = str.toCharArray();
        chars[0] = upper(chars[0]);
        return new String(chars);
    }

    public static void removeLastCharacter(StringBuilder stringBuilder) {
        if(stringBuilder.length()>0) stringBuilder.deleteCharAt(stringBuilder.length()-1);
    }

    public static void rightStripIfExists(StringBuilder stringBuilder, char suffix) {
        if(stringBuilder.charAt(stringBuilder.length()-1) == suffix) removeLastCharacter(stringBuilder);
    }

    public String ltrim(String str, int count) {
        return str.length() >= count ? str.substring(count) : str;
    }

    public String rtrim(String str, int count) {
        return str.length() >= count ? str.substring(0, str.length() - count) : str;
    }

}
