package uk.co.epii.conservatives.henryaddington.voa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.politics.williamcavendishbentinck.DatabaseSession;
import uk.co.epii.politics.williamcavendishbentinck.tables.DeliveryPointAddress;
import uk.co.epii.politics.williamcavendishbentinck.tables.Dwelling;
import uk.co.epii.politics.williamcavendishbentinck.tables.Postcode;
import uk.co.epii.spencerperceval.FileLineIterable;
import uk.co.epii.spencerperceval.data.PostcodeMatcher;
import uk.co.epii.spencerperceval.tuple.Duple;
import uk.co.epii.spencerperceval.util.Equivalence;
import uk.co.epii.spencerperceval.util.NeverEmptyHashMap;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.*;

/**
 * User: James Robinson
 * Date: 12/10/2013
 * Time: 11:26
 */
public class VOAUploader {

    private static final int MAX_LENGTH = 30;

    private static final Logger LOG = LoggerFactory.getLogger(VOAUploader.class);

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
    private DwellingLoader dwellingLoader;
    private DatabaseSession databaseSession;
    private float searchRadius;
    private List<Dwelling> dwellings;
    private Equivalence<Dwelling, DeliveryPointAddress> equivalence;
    private PostcodeLoader postcodeLoader;
    private Map<Dwelling, DeliveryPointAddress> matchedDwellingAddresses;

  public float getSearchRadius() {
        return searchRadius;
    }

    public void setSearchRadius(float searchRadius) {
        this.searchRadius = searchRadius;
    }

    public PostcodeLoader getPostcodeLoader() {
        return postcodeLoader;
    }

    public void setPostcodeLoader(PostcodeLoader postcodeLoader) {
        this.postcodeLoader = postcodeLoader;
    }

    public String getDwellingsFolder() {
        return dwellingsFolder;

    }

    public void setDwellingsFolder(String dwellingsFolder) {
        this.dwellingsFolder = dwellingsFolder.replaceAll("^~", System.getProperty("user.home"));
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
    for (File file : new File(dwellingsFolder).listFiles()) {
      String prefix = getPrefix();
      Collection<Dwelling> dwellings = processDwellings(new FileLineIterable(file));
      dwellings = removeDuplicatesAndPrependPrefix(prefix, dwellings);
      databaseSession.upload(dwellings);
    }
  }

  private Collection<Dwelling> removeDuplicatesAndPrependPrefix(String prefix, Collection<Dwelling> dwellings) {
    Map<String, Dwelling> unique = new HashMap<String, Dwelling>();
    int maxLength = MAX_LENGTH;
    for (Dwelling dwelling : dwellings) {
      dwelling.setLarn(prefix + dwelling.getLarn());
      Dwelling duplicate = unique.get(dwelling.getLarn());
      if (duplicate == null) {
        unique.put(dwelling.getLarn(), dwelling);
        continue;
      }
      if (dwelling.getLarn().length() > maxLength) {
        maxLength = dwelling.getLarn().length();
      }
      if (dwelling.getVoaAddress().equals(duplicate.getVoaAddress()) &&
              dwelling.getPostcode().equals(duplicate.getPostcode())) {
        if (dwelling.getCouncilTaxBand() == duplicate.getCouncilTaxBand()) {
          continue;
        }
        duplicate.setCouncilTaxBand((char)Math.max(duplicate.getCouncilTaxBand(), dwelling.getCouncilTaxBand()));
      }
      LOG.warn(String.format("Non Equal Dwellings with the same LARN: \n%s %s %s\n%s %s %s",
              dwelling.getVoaAddress(), dwelling.getPostcode(), dwelling.getCouncilTaxBand() + "",
              duplicate.getVoaAddress(), duplicate.getPostcode(), duplicate.getCouncilTaxBand() + ""));
    }
    if (maxLength > MAX_LENGTH) {
      throw new RuntimeException("Larns found as long as: " + maxLength);
    }
    return unique.values();
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
        Postcode postcode = postcodeLoader.getPostcode(group.getFirst());
        List<Dwelling> dwellings = group.getSecond();
        matchDwellings(postcode, dwellings);
        LOG.debug("Postcode: {} {}", group.getFirst(), dwellings.size());
        for (Dwelling dwelling : dwellings) {
            processDwelling(dwelling);
        }
    }

    private void processDwelling(Dwelling dwelling) {
        DeliveryPointAddress dwellingAddress = matchedDwellingAddresses.get(dwelling);
        if (dwellingAddress != null) {
            dwelling.setUprn(dwellingAddress.getUprn());
        }
        this.dwellings.add(dwelling);
    }

    private void matchDwellings(Postcode postcode, List<Dwelling> dwellings) {
        if (postcode == null) {
            matchedDwellingAddresses = new HashMap<Dwelling, DeliveryPointAddress>();
        }
        else {
            List<DeliveryPointAddress> addresses = dwellingLoader.getAddresses(postcode);
            matchedDwellingAddresses = equivalence.match(dwellings, addresses);
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
            String postcode = postcodeLoader.extractPostcode(line);
            if (postcode == null) {
                continue;
            }
            Dwelling dwelling = parseDwelling(postcode, line);
            if (dwelling != null) {
                map.get(dwelling.getPostcode()).add(dwelling);
            }
        }
        List<Duple<String, List<Dwelling>>> list =
                new ArrayList<Duple<String, List<Dwelling>>>(map.size());
        for (Map.Entry<String, List<Dwelling>> entry : map.entrySet()) {
            String postcode = entry.getKey();
            postcodeLoader.loadPostcode(postcode);
            list.add(new Duple<String, List<Dwelling>>(postcode, entry.getValue()));
        }
        return list;
    }

    private Dwelling parseDwelling(String postcode, String line) {
        String[] split = line.split("~");
        String councilTaxBand = split[1];
        if (councilTaxBand.length() > 1) {
            return null;
        }
        String address = line.substring(0, line.indexOf(postcode)).replaceAll("[ ,]*$", "");
        String larn = split[3];
        return new Dwelling(address, postcode, councilTaxBand.charAt(0), larn, null, null, null, null);
    }

  private static int counter = 0;

  public static synchronized int getAuthorityCount() {
    return counter++;
  }

  public String getPrefix() {
    int authority = getAuthorityCount();
    char a = (char)(authority / 26 + 65);
    char b = (char)(authority % 26 + 65);
    return a + "" + b + "_";
  }
}
