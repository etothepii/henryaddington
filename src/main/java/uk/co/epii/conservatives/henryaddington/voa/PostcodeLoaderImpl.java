package uk.co.epii.conservatives.henryaddington.voa;

import uk.co.epii.conservatives.williamcavendishbentinck.DatabaseSession;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.Postcode;
import uk.co.epii.spencerperceval.FileLineIterable;
import uk.co.epii.spencerperceval.data.PostcodeMatcher;

import java.util.*;

/**
 * User: James Robinson
 * Date: 26/10/2013
 * Time: 22:49
 */

public class PostcodeLoaderImpl implements PostcodeLoader {

    private String postcodesFolder;
    private PostcodeMatcher postcodeMatcher;
    private Set<String> postcodeAreas;
    private Map<String, Postcode> postcodes;
    private DatabaseSession databaseSession;

    public PostcodeLoaderImpl() {
        this.postcodeAreas = new HashSet<String>();
        this.postcodes = new HashMap<String, Postcode>();
    }

    public PostcodeLoaderImpl(String postcodesFolder) {
        this.postcodesFolder = postcodesFolder;
    }

    public PostcodeMatcher getPostcodeMatcher() {
        return postcodeMatcher;
    }

    public void setPostcodeMatcher(PostcodeMatcher postcodeMatcher) {

        this.postcodeMatcher = postcodeMatcher;
    }

    public String getPostcodesFolder() {
        return postcodesFolder;
    }

    public void setPostcodesFolder(String postcodesFolder) {
        this.postcodesFolder = postcodesFolder.replaceAll("^~", System.getProperty("user.home"));
    }

    public DatabaseSession getDatabaseSession() {
        return databaseSession;
    }

    public void setDatabaseSession(DatabaseSession databaseSession) {
        this.databaseSession = databaseSession;
    }


    private void loadPostcodeArea(String postcodeArea) {
        loadPostcodeFile(String.format("%s/%s.csv", postcodesFolder, postcodeArea.toLowerCase()));
    }

    private void loadPostcodeFile(String file) {
        List<Postcode> postcodes = new ArrayList<Postcode>();
        for (String line : new FileLineIterable(file)) {
            Postcode postcode = parsePostcode(line);
            postcodes.add(postcode);
            this.postcodes.put(postcode.getPostcode(), postcode);
        }
        databaseSession.upload(postcodes);
    }

    private Postcode parsePostcode(String line) {
        String postcode = postcodeMatcher.extractPostcode(line);
        if (postcode == null) {
            throw new IllegalStateException(String.format("Invalid postcode data in file: %s", line));
        }
        String[] parts = line.split(",", 5);
        int accuracy = Integer.parseInt(parts[1]);
        int xCoordinate = Integer.parseInt(parts[2]);
        int yCoordinate = Integer.parseInt(parts[3]);
        return new Postcode(postcode, accuracy, xCoordinate, yCoordinate);
    }

    @Override
    public Postcode getPostcode(String postcodeString) {
        Postcode postcode = postcodes.get(postcodeString);
        if (postcode == null) {
            loadPostcode(postcodeString);
        }
        return postcodes.get(postcodeString);
    }

    @Override
    public String extractPostcode(String line) {
        return postcodeMatcher.extractPostcode(line);
    }

    @Override
    public void loadPostcode(String postcode) {
        String postcodeArea = postcode.split("[0-9]", 2)[0];
        if (postcodeAreas.add(postcodeArea)) {
            loadPostcodeArea(postcodeArea);
        }
    }
}
