package uk.co.epii.conservatives.henryaddington.voa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.williamcavendishbentinck.DatabaseSession;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.DeliveryPointAddress;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.Dwelling;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.Postcode;
import uk.co.epii.spencerperceval.FileLineIterable;
import uk.co.epii.spencerperceval.tuple.Duple;
import uk.co.epii.spencerperceval.util.Equivalence;
import uk.co.epii.spencerperceval.util.NeverEmptyHashMap;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: James Robinson
 * Date: 12/10/2013
 * Time: 11:26
 */
public class VOAUploader {

    private static final Logger LOG = LoggerFactory.getLogger(VOAUploader.class);

    private final Pattern postcodeFinderPattern = Pattern.compile(".* ([A-Z0-9]* [A-Z0-9]*)~.*");
    private final Pattern postcodeAreaPattern = Pattern.compile("^([A-Z]*)[^A-Z].*");
    private final Pattern postcodeDataPattern = Pattern.compile("^\"([A-Z][A-Z0-9]*) +([0-9][A-Z]+)\",([0-9]*),([0-9]*),([0-9]*),");
    private final Comparator<Duple<String, String>> firstComparator = new Comparator<Duple<String, String>>() {
        @Override
        public int compare(Duple<String, String> o1, Duple<String, String> o2) {
            return o1.getFirst().compareTo(o2.getFirst());
        }
    };
    private final Comparator<Dwelling> addressComparator = new Comparator<Dwelling>() {
        @Override
        public int compare(Dwelling o1, Dwelling o2) {
            return o1.getVoaAddress().compareTo(o2.getVoaAddress());
        }
    };

    private String dwellingsFolder;
    private String postcodesFolder;
    private DwellingLoader dwellingLoader;
    private DatabaseSession databaseSession;
    private List<Dwelling> dwellings;
    private Set<String> postcodeAreas;
    private Equivalence<Dwelling, DeliveryPointAddress> equivalence;

    public String getDwellingsFolder() {
        return dwellingsFolder;
    }

    public void setDwellingsFolder(String dwellingsFolder) {
        this.dwellingsFolder = dwellingsFolder.replaceAll("^~", System.getProperty("user.home"));
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

    public DwellingLoader getDwellingLoader() {
        return dwellingLoader;
    }

    public void setDwellingLoader(DwellingLoader dwellingLoader) {
        this.dwellingLoader = dwellingLoader;
    }

    public List<Dwelling> getDwellings() {
        return dwellings;
    }

    public Equivalence<Dwelling, DeliveryPointAddress> getEquivalence() {
        return equivalence;
    }

    public void setEquivalence(Equivalence<Dwelling, DeliveryPointAddress> equivalence) {
        this.equivalence = equivalence;
    }

    public void processDwellings() {
        postcodeAreas = new HashSet<String>();
        for (File file : new File(dwellingsFolder).listFiles()) {
            List<Dwelling> dwellings = processDwellings(new FileLineIterable(file));
            databaseSession.upload(dwellings);
            for (Dwelling dwelling : dwellings) {
                Matcher matcher = postcodeAreaPattern.matcher(dwelling.getPostcode());
                if (matcher.matches()) {
                    postcodeAreas.add(matcher.group(1));
                }
            }
        }
        processPostcodeAreas();
    }

    private void processPostcodeAreas() {
        for (String postcodeArea : postcodeAreas) {
            loadPostcodeFile(String.format("%s/%s.csv", postcodesFolder, postcodeArea));
        }
    }

    private void loadPostcodeFile(String file) {
        List<Postcode> postcodes = new ArrayList<Postcode>();
        for (String line : new FileLineIterable(file)) {
            Matcher matcher = postcodeDataPattern.matcher(line);
            if (!matcher.find()) {
                throw new IllegalStateException(String.format("Invalid postcode data in file: %s", file));
            }
            String postcode = String.format("%s %s", matcher.group(1), matcher.group(2));
            int accuracy = Integer.parseInt(matcher.group(3));
            int xCoordinate = Integer.parseInt(matcher.group(4));
            int yCoordinate = Integer.parseInt(matcher.group(5));
            postcodes.add(new Postcode(postcode, accuracy, xCoordinate, yCoordinate));
        }
        databaseSession.upload(postcodes);
    }

    public List<Dwelling> processDwellings(Iterable<String> lines) {
        List<Duple<String, List<Dwelling>>> groups = getGroupedByPostcode(lines);
        dwellings = new ArrayList<Dwelling>();
        for (Duple<String, List<Dwelling>> group : groups) {
            processGroup(group);
        }
        return dwellings;
    }

    private void processGroup(Duple<String, List<Dwelling>> group) {
        String postcode = group.getFirst();
        List<Dwelling> dwellings = group.getSecond();
        List<DeliveryPointAddress> addresses = dwellingLoader.getAddresses(postcode);
        LOG.debug("Postcode: {} {}", postcode, dwellings.size());
        Map<Dwelling, DeliveryPointAddress> matchedDwellingAddresses = equivalence.match(dwellings, addresses);
        for (Dwelling dwelling : dwellings) {
            DeliveryPointAddress dwellingAddress = matchedDwellingAddresses.get(dwelling);
            if (dwellingAddress != null) {
                dwelling.setUprn(dwellingAddress.getUprn());
            }
            this.dwellings.add(dwelling);
        }
    }

    private List<Duple<String, List<Dwelling>>> getGroupedByPostcode(Iterable<String> lines) {
        Map<String, List<Dwelling>> map = new NeverEmptyHashMap<String, List<Dwelling>>() {
            @Override
            protected List<Dwelling> create() {
                return new ArrayList<Dwelling>();
            }
        };
        for (String line : lines) {
            Matcher matcher = postcodeFinderPattern.matcher(line);
            if (matcher.find()) {
                Dwelling dwelling = parseDwelling(matcher, line);
                if (dwelling != null) {
                    map.get(dwelling.getPostcode()).add(dwelling);
                }
            }
        }
        List<Duple<String, List<Dwelling>>> list =
                new ArrayList<Duple<String, List<Dwelling>>>(map.size());
        for (Map.Entry<String, List<Dwelling>> entry : map.entrySet()) {
            list.add(new Duple<String, List<Dwelling>>(entry.getKey(), entry.getValue()));
        }
        return list;
    }

    private Dwelling parseDwelling(Matcher matcher, String line) {
        String[] split = line.split("~");
        String councilTaxBand = split[1];
        if (councilTaxBand.length() > 1) {
            return null;
        }
        String address = line.substring(0, matcher.start(1)).replaceAll("[ ,]*$", "");
        String postcode = matcher.group(1);
        String larn = split[3];
        return new Dwelling(address, postcode, councilTaxBand.charAt(0), larn, null, null);
    }
}
