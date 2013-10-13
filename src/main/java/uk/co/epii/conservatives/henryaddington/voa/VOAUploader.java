package uk.co.epii.conservatives.henryaddington.voa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.williamcavendishbentinck.extensions.DeliveryPointAddressExtensions;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.DeliveryPointAddress;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.Dwelling;
import uk.co.epii.spencerperceval.FileLineIterable;
import uk.co.epii.spencerperceval.tuple.Duple;
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
    private final Pattern flatFinderPattern = Pattern.compile("[^,]*FLAT[^,]*");
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
    private List<Dwelling> dwellings;

    public String getDwellingsFolder() {
        return dwellingsFolder;
    }

    public void setDwellingsFolder(String dwellingsFolder) {
        this.dwellingsFolder = dwellingsFolder;
    }

    public String getPostcodesFolder() {
        return postcodesFolder;
    }

    public void setPostcodesFolder(String postcodesFolder) {
        this.postcodesFolder = postcodesFolder;
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

    public void processDwellings() {
        for (File file : new File(dwellingsFolder).listFiles()) {
            processDwellings(new FileLineIterable(file));
        }
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
        Collections.sort(dwellings, addressComparator);
        for (Dwelling dwelling : dwellings) {
            processDwelling(addresses, dwelling);
        }
    }

    private void processDwelling(List<DeliveryPointAddress> addresses, Dwelling dwelling) {
        long uprn;
        if ((uprn = findUPRN(addresses, dwelling)) == null) {
            LOG.warn("No match for: {} {}", dwelling.getVoaAddress(), dwelling.getPostcode());
        }
        dwelling.setUprn(uprn);
        this.dwellings.add(dwelling);
    }

    private Long findUPRN(List<DeliveryPointAddress> addresses, Dwelling dwelling) {
        for (int i = 0; i < addresses.size(); i++) {
            DeliveryPointAddress address = addresses.get(i);
            if (voaOSEqual(dwelling, address)) {
                addresses.remove(i);
                return address.getUprn();
            }
        }
        return null;
    }

    private boolean voaOSEqual(Dwelling dwelling, DeliveryPointAddress address) {
        if (dwelling == null && address == null) {
            return true;
        }
        if (dwelling == null ^ address == null) {
            return false;
        }
        String voaAddress = standardizeAddress(DwellingExtensions.getAddress(dwelling));
        String osAddress = standardizeAddress(DeliveryPointAddressExtensions.getAddress(address));
        return voaAddress.contains(osAddress) || osAddress.contains(voaAddress);
    }

    private String standardizeAddress(String address) {
        address = address.substring(0, address.lastIndexOf(','));
        address = address.toUpperCase();
        address = removeFlat(address);
        address = address.replaceAll("[, ]*", "");
        return address;
    }

    String removeFlat(String address) {
        Matcher matcher = flatFinderPattern.matcher(address);
        if (!matcher.find()) {
            return address;
        }
        StringBuilder stringBuilder = new StringBuilder(address.length());
        stringBuilder.append(address.substring(0, matcher.start()));
        String flat = matcher.group(0);
        for (int i = 0; i < flat.length(); i++) {
            char c = flat.charAt(i);
            if (Character.isDigit(c)) {
                stringBuilder.append(c);
            }
            else if (i > 0 && Character.isDigit(flat.charAt(i - 1)) && Character.isLetter(c) &&
                    (i + 1 == flat.length() || Character.isWhitespace(flat.charAt(i + 1)))) {
                stringBuilder.append(c);
            }
        }
        stringBuilder.append(address.substring(matcher.end()));
        return stringBuilder.toString();
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
        long larn = Long.parseLong(split[3]);
        return new Dwelling(address, postcode, councilTaxBand, larn, null, null);
    }
}
