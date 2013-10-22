package uk.co.epii.conservatives.henryaddington.voa;

import uk.co.epii.conservatives.williamcavendishbentinck.tables.DeliveryPointAddress;
import uk.co.epii.spencerperceval.tuple.Duple;
import uk.co.epii.spencerperceval.util.NeverEmptyHashMap;

import java.util.*;

/**
 * User: James Robinson
 * Date: 13/10/2013
 * Time: 02:02
 */
public class TestDwellingLoader implements DwellingLoader {

    Map<String, List<DeliveryPointAddress>> maps = new NeverEmptyHashMap<String, List<DeliveryPointAddress>>() {
        @Override
        protected List<DeliveryPointAddress> create() {
            return new ArrayList<DeliveryPointAddress>();
        }
    };

    @Override public List<DeliveryPointAddress> getAddresses(String postcode) {
        return maps.get(postcode);
    }

    public void addAll(String postcode, Collection<DeliveryPointAddress> collection) {
        maps.get(postcode).addAll(collection);
    }
}
