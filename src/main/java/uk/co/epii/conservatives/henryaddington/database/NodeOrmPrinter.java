package uk.co.epii.conservatives.henryaddington.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.spencerperceval.FileLineIterable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: James Robinson
 * Date: 30/09/2014
 * Time: 08:14
 */
public class NodeOrmPrinter {

  private static final Logger LOG = LoggerFactory.getLogger(NodeOrmPrinter.class);

  private String outputFile;
  private String relationshipsFile;
  private PrintWriter printWriter;
  private String userNameEnvironmentVariable;
  private String passwordEnvironmentVariable;
  private String databaseEnvironmentVariable;
  private List<Table> tables;

  public String getOutputFile() {
    return outputFile;
  }

  public void setOutputFile(String outputFile) {
    if (outputFile.charAt(0) == '~') {
      this.outputFile = System.getProperty("user.home") + outputFile.substring(1);
    }
    else {
      this.outputFile = outputFile;
    }
  }

  public void print() {
    if (outputFile == null) return;
    FileWriter fileWriter = null;
    printWriter = null;
    try {
      fileWriter = new FileWriter(getOutputFile());
      printWriter = new PrintWriter(fileWriter);
      printHeader();
      declareTables();
      buildOrm();
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

  private void buildOrm() {
    printWriter.println();
    printWriter.println("function buildORM(db) {");
    for (Table table : tables) {
      buildTable(table);
    }
    if (relationshipsFile != null) {
      try {
        loadRelationShips();
      }
      catch (IOException ioe) {
        LOG.error(ioe.getMessage(), ioe);
      }
    }
    printWriter.println("}");
  }

  private void loadRelationShips() throws IOException {
    for (String line : new FileLineIterable(relationshipsFile)) {
      printWriter.println(line);
    }
  }

  private void buildTable(Table table) {
    printWriter.println(String.format("  %s = db.define(\"%s\", {", getNodeName(table.name), table.name));
    printFields(table.fields);
    printWriter.println("  },{");
    printIds(table.fields);
    printWriter.println("  });");
    printWriter.println(String.format("  exports.%s = %s;", table.name, getNodeName(table.name)));
  }

  private void printIds(List<Field> fields) {
    List<Field> ids = new ArrayList<Field>(fields.size());
    for (Field field : fields) {
      if (field.isPrimaryKey()) {
        ids.add(field);
      }
    }
    printFields(ids);
  }

  private void printFields(List<Field> fields) {
    for (int i = 0; i < fields.size(); i++) {
      Field field = fields.get(i);
      printWriter.println(String.format("    %s: %s%s", field.getDbName(),
              field.getNodeType(), i == fields.size() - 1 ? "" : ","));
    }
  }

  private void declareTables() {
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

  public void setUserNameEnvironmentVariable(String userNameEnvironmentVariable) {
    this.userNameEnvironmentVariable = userNameEnvironmentVariable;
  }

  public void setDatabaseEnvironmentVariable(String databaseEnvironmentVariable) {
    this.databaseEnvironmentVariable = databaseEnvironmentVariable;
  }

  public void setPasswordEnvironmentVariable(String passwordEnvironmentVariable) {
    this.passwordEnvironmentVariable = passwordEnvironmentVariable;
  }

  private void printHeader() {
    printWriter.println("var orm = require(\"orm\");");
    printWriter.println("");
    printWriter.println("exports.connect = function (after) {");
    printWriter.println("  var password;");
    printWriter.println(String.format("  password = process.env.%s;", passwordEnvironmentVariable));
    printWriter.println("  connectToDatabase(password, orm, after);");
    printWriter.println("}");
    printWriter.println("");
    printWriter.println("function connectToDatabase(password, orm, after) {");
    printWriter.println(String.format("  orm.connect(\"mysql://\" + process.env.%s + \":\" + password + " +
            "\"@localhost/\" + process.env.%s, function (err, db) {",
            userNameEnvironmentVariable, databaseEnvironmentVariable));
    printWriter.println("    if (err) throw err;");
    printWriter.println("    buildORM(db);");
    printWriter.println("    after();");
    printWriter.println("  });");
    printWriter.println("}");
    printWriter.println("");
  }

  public void setTables(List<Table> tables) {
    this.tables = tables;
  }

  public void setRelationshipsFile(String relationshipsFile) {
    if (relationshipsFile.charAt(0) == '~') {
      this.relationshipsFile = System.getProperty("user.home") + relationshipsFile.substring(1);
    }
    else {
      this.relationshipsFile = relationshipsFile;
    }
  }
}
