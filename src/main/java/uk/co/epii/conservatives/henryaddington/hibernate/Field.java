package uk.co.epii.conservatives.henryaddington.hibernate;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: James Robinson
 * Date: 27/09/2013
 * Time: 22:07
 */
public class Field {

    private final String dbName;
    private final String javaType;
    private final String hibernateType;
    private final String javaName;
    private final boolean primaryKey;
    private final boolean nullable;

    Field(String name, String javaType, String hibernateType, boolean primaryKey, boolean nullable) {
        this.dbName = name;
        this.javaType = javaType;
        this.hibernateType = hibernateType;
        this.javaName = createJavaName(name);
        this.primaryKey = primaryKey;
        this.nullable = nullable;
    }

    private String createJavaName(String name) {
        String[] words = name.split("_");
        StringBuilder stringBuilder = new StringBuilder(name.length());
        for (int i  = 0; i < words.length; i++) {
            String lowercase = words[i].toLowerCase();
            if (i == 0) {
                if (lowercase.length() == 1) {
                    stringBuilder.append(Character.toUpperCase(lowercase.charAt(0)));
                }
                else {
                    stringBuilder.append(lowercase);
                }
            }
            else {
                stringBuilder.append(Character.toUpperCase(lowercase.charAt(0)));
                stringBuilder.append(lowercase.substring(1));
            }
        }
        return stringBuilder.toString();
    }

    private static Pattern fieldPattern = Pattern.compile(" *([^ ]*) *([^ \\(\\)]*)[ \\(\\)].*");
    private static Map<String, String> javaTypeMap = new HashMap<String, String>();
    static {
        javaTypeMap.put("INT", "int");
        javaTypeMap.put("VARCHAR", "String");
        javaTypeMap.put("CHAR", "String");
        javaTypeMap.put("TINYTEXT", "String");
        javaTypeMap.put("BIGINT", "long");
        javaTypeMap.put("FLOAT", "float");
        javaTypeMap.put("DATE", "Date");
    }

    private static Map<String, String> javaNullTypeMap = new HashMap<String, String>();
    static {
        javaNullTypeMap.put("INT", "Integer");
        javaNullTypeMap.put("VARCHAR", "String");
        javaNullTypeMap.put("CHAR", "String");
        javaNullTypeMap.put("TINYTEXT", "String");
        javaNullTypeMap.put("BIGINT", "Long");
        javaNullTypeMap.put("FLOAT", "Float");
        javaNullTypeMap.put("DATE", "Date");
    }

    private static Map<String, String> hibernateTypeMap = new HashMap<String, String>();
    static {
        hibernateTypeMap.put("INT", "integer");
        hibernateTypeMap.put("VARCHAR", "string");
        hibernateTypeMap.put("CHAR", "character");
        hibernateTypeMap.put("TINYTEXT", "string");
        hibernateTypeMap.put("BIGINT", "long");
        hibernateTypeMap.put("FLOAT", "float");
        hibernateTypeMap.put("DATE", "date");
    }

    public static Field parse(String in) {
        Matcher matcher = fieldPattern.matcher(in);
        matcher.find();
        boolean nullable = !in.contains("NOT NULL");
        return new Field(matcher.group(1),
                nullable ?
                        javaNullTypeMap.get(matcher.group(2).toUpperCase()) :
                        javaTypeMap.get(matcher.group(2).toUpperCase()),
                hibernateTypeMap.get(matcher.group(2).toUpperCase()),
                in.contains("PRIMARY KEY"), nullable);
    }

    public String getDbName() {
        return dbName;
    }

    public String getHibernateType() {
        return hibernateType;
    }

    public String getJavaType() {
        return javaType;
    }

    public String getJavaName(boolean capitalized) {
        if (!capitalized) {
            return javaName;
        }
        return Character.toUpperCase(javaName.charAt(0)) + javaName.substring(1);
    }

    public boolean isNullable() {
        return nullable;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }
}
