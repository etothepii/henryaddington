package uk.co.epii.conservatives.henryaddington.database;

import java.io.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: James Robinson
 * Date: 27/09/2013
 * Time: 22:42
 */
public class HibernatePrinter {

    private String javaDirectory;
    private String javaPackage;
    private List<String> javaImports;
    private String hibernateDirectory;

    public void setJavaDirectory(String javaDirectory) {
        this.javaDirectory = javaDirectory.replace("~", System.getProperty("user.home"));
    }

    public void setJavaPackage(String javaPackage) {
        this.javaPackage = javaPackage;
    }

    public void setJavaImports(List<String> javaImports) {
        this.javaImports = javaImports;
    }

    public void setHibernateDirectory(String hibernateDirectory) {
        this.hibernateDirectory = hibernateDirectory.replace("~", System.getProperty("user.home"));
    }

    public void print(List<Table> tables) {
        for (Table table : tables) {
            buildPojo(javaDirectory, table);
            buildHibernateMapping(hibernateDirectory, table);
        }
    }

    private void buildHibernateMapping(String directory, Table table) {
        directory += directory.endsWith(File.separator) ? "" : File.separator;
        String file = String.format("%s%s.hbm.xml", directory, table.name);
        FileWriter fileWriter = null;
        PrintWriter printWriter = null;
        try {
            fileWriter = new FileWriter(file, false);
            printWriter = new PrintWriter(fileWriter);
            printWriter.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            printWriter.println("<!DOCTYPE database-mapping PUBLIC");
            printWriter.println(" \"-//Hibernate/Hibernate Mapping DTD//EN\"");
            printWriter.println(" \"http://www.database.org/dtd/database-mapping-3.0.dtd\">");
            printWriter.println();
            printWriter.println("<database-mapping>");
            printWriter.println(String.format("    <class name=\"%s.%s\" table=\"%s\">", javaPackage, table.name, table.name));
            Collections.sort(table.fields, new Comparator<Field>() {
                @Override
                public int compare(Field a, Field b) {
                    if (a.isPrimaryKey() == b.isPrimaryKey()) {
                        return 0;
                    }
                    return a.isPrimaryKey() ? -1 : 1;
                }
            });
            for (Field field : table.fields) {
                if (field.isPrimaryKey()) {
                    printWriter.println(String.format("        <id name=\"%s\" type=\"%s\" column=\"%s\">",
                            field.getJavaName(false), field.getHibernateType(), field.getDbName()));
                    printWriter.println("            <generator class=\"assigned\"/>");
                    printWriter.println("        </id>");
                }
                else {
                    printWriter.println(String.format("        <property name=\"%s\" column=\"%s\" type=\"%s\" />",
                            field.getJavaName(false), field.getDbName(), field.getHibernateType()));
                }
            }
            printWriter.println("    </class>");
            printWriter.println("</database-mapping>");
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        finally {
            if (printWriter != null) {
                printWriter.flush();
                printWriter.close();
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                }
                catch (IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            }
        }
    }

    private void buildPojo(String directory, Table table) {
        directory += directory.endsWith(File.separator) ? "" : File.separator;
        String file = String.format("%s%s.java", directory, table.name);
        FileWriter fileWriter = null;
        PrintWriter printWriter = null;
        try {
            fileWriter = new FileWriter(file, false);
            printWriter = new PrintWriter(fileWriter);
            if (javaPackage != null) {
                printWriter.println(String.format("package %s;", javaPackage));
                printWriter.println();
            }
            if (javaImports != null) {
                for (String javaImport : javaImports) {
                    printWriter.println(String.format("import %s;", javaImport));
                }
                printWriter.println();
            }
            printWriter.println();
            printWriter.println(String.format("public class %s {", table.name));
            printWriter.println();
            for (Field field : table.fields) {
                printWriter.println(String.format("    private %s %s;", field.getJavaType(), field.getJavaName(false)));
            }
            printWriter.println();
            printWriter.println(String.format("    public %s () {}", table.name));
            printWriter.println(String.format("    public %s (%s) {", table.name, getConstructorParameters(table.fields)));
            for (Field field : table.fields) {
                printWriter.println(String.format("        this.%s = %s;", field.getJavaName(false), field.getJavaName(false)));
            }
            printWriter.println(String.format("    }"));
            printWriter.println();
            for (Field field : table.fields) {
                printWriter.println(String.format("    public %s get%s() {", field.getJavaType(), field.getJavaName(true)));
                printWriter.println(String.format("        return %s;", field.getJavaName(false)));
                printWriter.println(String.format("    }"));
                printWriter.println();
                printWriter.println(String.format("    public void set%s(%s %s) {", field.getJavaName(true), field.getJavaType(), field.getJavaName(false)));
                printWriter.println(String.format("        this.%s = %s;", field.getJavaName(false), field.getJavaName(false)));
                printWriter.println(String.format("    }"));
                printWriter.println();
            }
            printWriter.println(String.format("}"));
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        finally {
            if (printWriter != null) {
                printWriter.flush();
                printWriter.close();
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                }
                catch (IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            }
        }
    }

    private String getConstructorParameters(List<Field> fields) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            if (i > 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(String.format("%s %s", field.getJavaType(), field.getJavaName(false)));
        }
        return stringBuilder.toString();
    }

}
