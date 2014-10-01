package uk.co.epii.conservatives.henryaddington.database;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * User: James Robinson
 * Date: 30/09/2014
 * Time: 08:14
 */
public class NodeOrmPrinter {

  private String outputFile;
  private List<Table> tables;

  public String getOutputFile() {
    return outputFile;
  }

  public void setOutputFile(String outputFile) {
    this.outputFile = outputFile;
  }

  public void print() {
    if (outputFile == null) return;
    FileWriter fileWriter = null;
    PrintWriter printWriter = null;
    try {
      fileWriter = new FileWriter(getOutputFile());
      printWriter = new PrintWriter(fileWriter);
      printHeader(printWriter);
      declareTables(printWriter);
      buildOrm(printWriter);
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

  private void buildOrm(PrintWriter printWriter) {
    printWriter.println();
    printWriter.println("function buildORM(db) {");
    printWriter.println("}");
  }

  private void declareTables(PrintWriter printWriter) {
    for (Table table : tables) {
      printWriter.println(String.format("var %s;", getNodeName(table.name)));
    }
    printWriter.println();
  }

  private String getNodeName(String name) {
    StringBuilder stringBuilder = new StringBuilder(name.length());
    stringBuilder.append(Character.toLowerCase(name.charAt(0)));
    stringBuilder.append(name.substring(1));
    return stringBuilder.toString();
  }

  private void printHeader(PrintWriter printWriter) {
    printWriter.println("var orm = require(\"orm\");\n");
    printWriter.println("\n");
    printWriter.println("exports.connect = function (fs, after) {\n");
    printWriter.println("  var password;\n");
    printWriter.println("  password = process.env.FREDNORTH_DB_PASSWORD;\n");
    printWriter.println("  connectToDatabase(password, orm);\n");
    printWriter.println("  after();\n");
    printWriter.println("}\n");
    printWriter.println("\n");
    printWriter.println("function connectToDatabase(password, orm) {\n");
    printWriter.println("  orm.connect(\"mysql://\" + process.env.FREDNORTH_DB_USERNAME + \":\" + password + \"@localhost/\" + process.env.DATABASE, function (err, db) {\n");
    printWriter.println("    if (err) throw err;\n");
    printWriter.println("    buildORM(db);\n");
    printWriter.println("  });\n");
    printWriter.println("}\n");
    printWriter.println("\n");
  }

  public void setTables(List<Table> tables) {
    this.tables = tables;
  }
}
