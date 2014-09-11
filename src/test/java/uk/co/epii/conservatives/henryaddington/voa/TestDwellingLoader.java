package uk.co.epii.conservatives.henryaddington.voa;

import uk.co.epii.politics.williamcavendishbentinck.tables.DeliveryPointAddress;
import uk.co.epii.politics.williamcavendishbentinck.tables.Postcode;
import uk.co.epii.spencerperceval.util.NeverEmptyHashMap;

import java.awt.geom.Point2D;
import java.util.*;

/**
 * User: James Robinson
 * Date: 13/10/2013
 * Time: 02:02
 */
public class TestDwellingLoader implements DwellingLoader {

    Map<Postcode, List<DeliveryPointAddress>> maps = new NeverEmptyHashMap<Postcode, List<DeliveryPointAddress>>() {
        @Override
        protected List<DeliveryPointAddress> create() {
            return new ArrayList<DeliveryPointAddress>();
        }
    };

    @Override public List<DeliveryPointAddress> getAddresses(Postcode postcode) {
        return maps.get(postcode);
    }

    @Override
    public List<DeliveryPointAddress> getAddresses(Point2D.Float location, float radius) {
        List<DeliveryPointAddress> addresses = new ArrayList<DeliveryPointAddress>();
        Set<Postcode> postcodes = maps.keySet();
        for (Postcode postcode : postcodes) {
            double dx = Math.abs(location.getX() - postcode.getXCoordinate());
            double dy = Math.abs(location.getY() - postcode.getYCoordinate());
            if (dx * dx + dy * dy <= radius * (double)radius) {
                addresses.addAll(maps.get(postcode));
            }
        }
        return addresses;
    }

    public void addAll(Postcode postcode, Collection<DeliveryPointAddress> collection) {
        maps.get(postcode).addAll(collection);
    }
}
