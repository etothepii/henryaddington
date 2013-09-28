package uk.co.epii.conservatives.henryaddington.hibernate;

import sun.misc.Regexp;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: James Robinson
 * Date: 27/09/2013
 * Time: 22:06
 */
public class HibernateBuilder {

    private String resource;
    private HibernatePrinter hibernatePrinter;

    public void setResource(String resource) {
        this.resource = resource;
    }

    public void setHibernatePrinter(HibernatePrinter hibernatePrinter) {
        this.hibernatePrinter = hibernatePrinter;
    }

    public void process() {
        hibernatePrinter.print(read());
    }

    private List<Table> read() {
        Pattern createTablePattern = Pattern.compile("CREATE TABLE ([^ ]*) \\(");
        ArrayList<Table> tables = new ArrayList<Table>();
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            inputStreamReader = new InputStreamReader(HibernateBuilder.class.getResourceAsStream(resource));
            bufferedReader = new BufferedReader(inputStreamReader);
            String in;
            Table activeTable = null;
            while ((in = bufferedReader.readLine()) != null) {
                if (in.contains("CREATE TABLE")) {
                    Matcher matcher = createTablePattern.matcher(in);
                    matcher.find();
                    activeTable = new Table(matcher.group(1));
                    tables.add(activeTable);
                }
                else if (!in.trim().startsWith("INDEX") && in.endsWith(",") || in.endsWith(")")) {
                    activeTable.fields.add(Field.parse(in));
                }
            }
            return tables;
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                }
                catch (IOException ioe) {}
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                }
                catch (IOException ioe) {}
            }
        }
    }

}
