package uk.co.epii.conservatives.henryaddington;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: James Robinson
 * Date: 20/09/2013
 * Time: 23:17
 */
public class DatabaseUploader {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseUploader.class);

    private Map<Integer, String> recordIdentiferTableMap;
    private String createScriptLocation;
    private String passwordLocation;
    private String username;
    private String driver;
    private String password;
    private String database;
    private String server;
    private String protocol;
    private Connection connection;
    private int port;
    private List<String> files;
    private String sqlDumpFile;

    public void setFiles(List<String> files) {
        this.files = new ArrayList<String>(files.size());
        for (String file : files) {
            this.files.add(file.replaceAll("^\\~", System.getProperty("user.home")));
        }
    }

    public void setCreateScriptLocation(String createScriptLocation) {
        this.createScriptLocation = createScriptLocation;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setRecordIdentiferTableMap(Map<Integer, String> recordIdentiferTableMap) {
        this.recordIdentiferTableMap = recordIdentiferTableMap;
    }

    public void setPasswordLocation(String passwordLocation) {
        this.passwordLocation = passwordLocation;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public void setPort(int port) {
        this.port = port;
    }

  public void setSqlDumpFile(String sqlDumpFile) {
    if (sqlDumpFile.charAt(0) == '~') {
      this.sqlDumpFile = System.getProperty("user.home") + sqlDumpFile.substring(1);
    }
    else {
      this.sqlDumpFile = sqlDumpFile;
    }
  }

  public void setServer(String server) {
        this.server = server;
    }

    public void init() {
        loadPassword();
        loadDriver();
        deviseProtocol();
        connect(username, password);
        LOG.debug("protocol: " + protocol);
    }

    private void loadDriver() {
        try {
            Class.forName(driver).newInstance();
            LOG.debug("Loaded the appropriate driver");
        } catch (ClassNotFoundException cnfe) {
            LOG.error("Unable to load the JDBC driver " + driver +
                    "Please check your CLASSPATH.", cnfe);
        } catch (InstantiationException ie) {
            LOG.error("Unable to instantiate the JDBC driver " + driver, ie);
        } catch (IllegalAccessException iae) {
            LOG.error("Not allowed to access the JDBC driver " + driver, iae);
        }
    }

    private void deviseProtocol() {
        protocol = String.format("jdbc:mysql://%s:%d/%s", server, port, database);
    }

    private void connect(String username, String password) {
        try {
            LOG.debug("connecting...");
            connection = DriverManager.getConnection(protocol, username, password);
            LOG.debug("connected...");
        }
        catch (SQLException se) {
            connection = null;
            throw new RuntimeException(se);
        }
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return connection.prepareStatement(sql, autoGeneratedKeys);
    }

    private void loadPassword() {
        try {
            FileReader fileReader = new FileReader(passwordLocation);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            password = bufferedReader.readLine();
            bufferedReader.close();
            fileReader.close();
        }
        catch (IOException ioe) {
            LOG.error("Unable to find password: {}", passwordLocation);
        }
    }

    public boolean cleanDatabase() {
        List<String> statements = new ArrayList<String>();
        StringBuilder query = new StringBuilder(16384);
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(
                    DatabaseUploader.class.getResourceAsStream(createScriptLocation));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String in;
            while ((in = bufferedReader.readLine()) != null) {
                if (query.length() != 0) {
                    query.append("\n");
                }
                if (in.contains(";")) {
                    String[] split = in.split(";");
                    for (int i = 0; i < split.length; i++) {
                        query.append(split[i]);
                        if (i < split.length - 1 || in.endsWith(";") && query.length() > 0) {
                            statements.add(query.toString());
                            query = new StringBuilder(16384);
                        }
                    }
                }
                else {
                    query.append(in);
                }
            }
            bufferedReader.close();
            inputStreamReader.close();
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        try {
            for (String statement : statements) {
                LOG.info(statement);
                PreparedStatement preparedStatement = prepareStatement(statement);
                preparedStatement.execute();
            }
            return true;
        }
        catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }
    }

    public void upload() {
        Map<String, String> tableTempFiles = parseDataToOneTempFilePerTable();
        try {
            for (Map.Entry<String, String> entry : tableTempFiles.entrySet()) {
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                String query = String.format("LOAD DATA LOCAL INFILE '%s' INTO TABLE `%s` " +
                        "FIELDS TERMINATED BY ',' ENCLOSED BY '\"'",
                        entry.getValue(), entry.getKey());
                statement.executeUpdate(query);
            }
            uploadSqlDumpFile();
        }
        catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        }
    }

  private void uploadSqlDumpFile() {
    if (sqlDumpFile == null) {
      return;
    }
    try {
      executeSqlDumpFile();
    }
    catch (SQLException sqle) {
      LOG.error(sqle.getMessage(), sqle);
    }
    catch (IOException ioe) {
      LOG.error(ioe.getMessage(), ioe);
    }
  }

  private void executeSqlDumpFile() throws IOException, SQLException {
    LOG.info("Uploading dump file");
    FileReader fileReader = new FileReader(sqlDumpFile);
    BufferedReader bufferedReader = new BufferedReader(fileReader);
    String in;
    StringBuilder stringBuilder = new StringBuilder(500000);
    while ((in = bufferedReader.readLine()) != null) {
      stringBuilder.append(in);
      stringBuilder.append('\n');
    }
    bufferedReader.close();
    fileReader.close();
    Statement statement = connection.createStatement();
    int index = 0;
    while (index < stringBuilder.length()) {
      LOG.debug("index: {}", index);
      int toIndex = stringBuilder.indexOf(";", index);
      if (toIndex < 0) {
        toIndex = stringBuilder.length();
      }
      String command = stringBuilder.substring(index, toIndex).trim();
      index = toIndex + 1;
      if (command.length() == 0) {
        continue;
      }
      LOG.debug(command);
      statement.execute(command);
    }
    LOG.info("Uploaded dump file");
  }

  private Map<String, String> parseDataToOneTempFilePerTable () {
        Map<Integer, FileWriter> fileWriters = new HashMap<Integer, FileWriter>();
        Map<Integer, PrintWriter> printWriters = new HashMap<Integer, PrintWriter>();
        try {
            String tmpDir = System.getProperty("java.io.tmpdir");
            Map<String, String> tableTempFiles = new HashMap<String, String>();
            for (String file : files) {
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String in;
                while ((in = bufferedReader.readLine()) != null) {
                    String[] parts = in.split(",", 2);
                    int recordIdentifier = Integer.parseInt(parts[0]);
                    if (!recordIdentiferTableMap.containsKey(recordIdentifier)) {
                        continue;
                    }
                    PrintWriter printWriter = printWriters.get(recordIdentifier);
                    if (printWriter == null) {
                        String tableName = recordIdentiferTableMap.get(recordIdentifier);
                        String tempFile = String.format("%s%s.csv", tmpDir , tableName);
                        tableTempFiles.put(tableName, tempFile);
                        LOG.debug("tempFile: {}", tempFile);
                        FileWriter fileWriter = new FileWriter(tempFile, false);
                        fileWriters.put(recordIdentifier, fileWriter);
                        printWriter = new PrintWriter(fileWriter, true);
                        printWriters.put(recordIdentifier, printWriter);
                    }
                    printWriter.println(in.replaceAll("\"\"", "NULL").replaceAll(",,", ",NULL,").replaceAll(",,",",NULL,"));
                }
            }
            return tableTempFiles;
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        finally {
            for (PrintWriter printWriter : printWriters.values()) {
                printWriter.flush();
                printWriter.close();
            }
            for (FileWriter fileWriter : fileWriters.values()) {
                try {
                    fileWriter.close();
                }
                catch (IOException ioe) {
                    throw new RuntimeException(ioe);
                }
            }
        }
    }
}
