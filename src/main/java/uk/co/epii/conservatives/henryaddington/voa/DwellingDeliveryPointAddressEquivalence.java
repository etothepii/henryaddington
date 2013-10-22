package uk.co.epii.conservatives.henryaddington.voa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.DeliveryPointAddress;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.Dwelling;
import uk.co.epii.spencerperceval.util.Equivalence;
import uk.co.epii.spencerperceval.util.Group;
import uk.co.epii.spencerperceval.util.Grouper;

import java.util.*;

/**
 * User: James Robinson
 * Date: 14/10/2013
 * Time: 19:32
 */
public class DwellingDeliveryPointAddressEquivalence implements Equivalence<Dwelling, DeliveryPointAddress> {

    private static final Logger LOG = LoggerFactory.getLogger(DwellingDeliveryPointAddressEquivalence.class);
    private static final Grouper<StubDwelling> DWELLING_GROUPER = new Grouper<StubDwelling>();
    private static final Grouper<StubDeliveryPointAddress> ADDRESS_GROUPER = new Grouper<StubDeliveryPointAddress>();

    private Map<StubDwelling, Dwelling> stubDwellingMap;
    private Map<StubDeliveryPointAddress, DeliveryPointAddress> stubAddressMap;
    private List<StubDwelling> stubDwellings;
    private List<StubDeliveryPointAddress> stubAddresses;
    private List<Group<StubDwelling>> dwellingGroups;
    private List<Group<StubDeliveryPointAddress>> addressGroups;
    private HashMap<Dwelling, DeliveryPointAddress> matched;
    private List<StubDwelling> unmatchedDwellings;

    @Override
    public Map<Dwelling, DeliveryPointAddress> match(List<Dwelling> dwellings, List<DeliveryPointAddress> addresses) {
        restart(dwellings, addresses);
        matchGroupsByName();
        matchGroupsBySize();
        matchGroupsByApproximateSize();
        if (onlyOneGroupOfEach()) {
            matchSingleGroup();
        }
        for (StubDwelling stubDwelling : unmatchedDwellings) {
            Dwelling dwelling = stubDwellingMap.get(stubDwelling);
            matched.put(dwelling, null);
            LOG.debug("Failed to match: {}", stubDwelling);
        }
        for (Group<StubDwelling> group : dwellingGroups) {
            for (StubDwelling stubDwelling : group) {
                Dwelling dwelling = stubDwellingMap.get(stubDwelling);
                matched.put(dwelling, null);
                LOG.debug("Failed to match: {}", stubDwelling);
            }
        }
        return matched;
    }

    private void matchGroupsByApproximateSize() {
        for (int i = 0; i < dwellingGroups.size(); i++) {
            int maxSize = (int)Math.round(Math.ceil(dwellingGroups.size() * Math.E));
            int minSize = (int)Math.round(Math.floor(dwellingGroups.size() / Math.E));
            int addressMatch = -1;
            for (int j = 0; j < addressGroups.size(); j++) {
                if (addressGroups.get(j).size() <= maxSize && addressGroups.get(j).size() >= minSize) {
                    if (addressMatch == -1) {
                        addressMatch = j;
                    }
                    else if (addressMatch >= 0) {
                        addressMatch = -2;
                    }
                }
            }
            if (addressMatch >= 0) {
                process(dwellingGroups.remove(i--), addressGroups.remove(addressMatch));
            }
        }
    }

    private void matchGroupsByName() {
        for (int i = 0; i < dwellingGroups.size(); i++) {
            for (int j = 0; j < addressGroups.size(); j++) {
                String commonDwelling = compress(dwellingGroups.get(i).getCommon().toString());
                String commonAddress = compress(addressGroups.get(j).getCommon().toString());
                if (commonDwelling.equals(commonAddress)) {
                    process(dwellingGroups.remove(i--), addressGroups.remove(j));
                    break;
                }
            }
        }
    }

    private void matchGroupsBySize() {
        for (int i = 0; i < dwellingGroups.size(); i++) {
            int addressMatch = -1;
            for (int j = 0; j < addressGroups.size(); j++) {
                if (dwellingGroups.get(i).size() == addressGroups.get(j).size()) {
                    if (addressMatch == -1) {
                        addressMatch = j;
                    }
                    else if (addressMatch >= 0) {
                        addressMatch = -2;
                    }
                }
            }
            if (addressMatch >= 0) {
                process(dwellingGroups.remove(i--), addressGroups.remove(addressMatch));
            }
        }
    }

    private void restart(List<Dwelling> dwellings, List<DeliveryPointAddress> addresses) {
        mapDwellings(dwellings);
        mapAddresses(addresses);
        dwellingGroups = DWELLING_GROUPER.group(stubDwellings);
        addressGroups = ADDRESS_GROUPER.group(stubAddresses);
        matched = new HashMap<Dwelling, DeliveryPointAddress>();
        unmatchedDwellings = new ArrayList<StubDwelling>();
    }

    private void matchSingleGroup() {
        process(dwellingGroups.get(0), addressGroups.get(0));
    }

    private boolean onlyOneGroupOfEach() {
        return dwellingGroups.size() == 1 &&
                addressGroups.size() == 1 &&
                dwellingGroups.size() == 1 &&
                addressGroups.size() == 1;
    }

    private void process(Group<StubDwelling> dwellings, Group<StubDeliveryPointAddress> addresses) {
        if (dwellings.size() == 1 && addresses.size() == 1) {
            DeliveryPointAddress matchedAddress = stubAddressMap.get(addresses.get(0));
            Dwelling matchedDwelling = stubDwellingMap.get(dwellings.get(0));
            matched.put(matchedDwelling, matchedAddress);
            return;
        }
        matchDeltas(addresses, dwellings);
        for (StubDwelling dwelling : dwellings) {
            unmatchedDwellings.add(dwelling);
        }
    }

    private void matchDeltas(Group<StubDeliveryPointAddress> addresses,
                             Group<StubDwelling> dwellings) {
        for (int i = 0; i < addresses.size(); i++) {
            StubDeliveryPointAddress address = addresses.get(i);
            String addressDifference = addresses.getCommon().getDifference(address);
            if (addressDifference == null) {
                addressDifference = address.toString();
            }
            addressDifference = compress(addressDifference);
            for (int j = 0; j < dwellings.size(); j++) {
                StubDwelling dwelling = dwellings.get(j);
                String dwellingDifference = dwellings.getCommon().getDifference(dwelling);
                if (dwellingDifference == null) {
                    dwellingDifference = dwelling.toString();
                }
                dwellingDifference = compress(dwellingDifference);
                if (dwellingDifference.equals(addressDifference)) {
                    matched.put(
                            stubDwellingMap.get(dwellings.remove(j)),
                            stubAddressMap.get(addresses.remove(i--)));
                    break;
                }
            }
        }
    }

    private String compress(String string) {
        return string.replaceAll("[^0-9A-z]", "").toUpperCase();
    }

    private void mapAddresses(List<DeliveryPointAddress> addresses) {
        stubAddressMap = new HashMap<StubDeliveryPointAddress, DeliveryPointAddress>();
        stubAddresses = new ArrayList<StubDeliveryPointAddress>();
        for (DeliveryPointAddress address : addresses) {
            StubDeliveryPointAddress stubAddress = new StubDeliveryPointAddress(address);
            stubAddressMap.put(stubAddress, address);
            stubAddresses.add(stubAddress);
        }
    }

    private void mapDwellings(List<Dwelling> dwellings) {
        stubDwellingMap = new HashMap<StubDwelling, Dwelling>();
        stubDwellings  = new ArrayList<StubDwelling>();
        for (Dwelling dwelling : dwellings) {
            StubDwelling stubDwelling = new StubDwelling(dwelling);
            stubDwellingMap.put(stubDwelling, dwelling);
            stubDwellings.add(stubDwelling);
        }
    }
}



