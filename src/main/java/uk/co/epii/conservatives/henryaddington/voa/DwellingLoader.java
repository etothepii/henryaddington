package uk.co.epii.conservatives.henryaddington.voa;

import uk.co.epii.politics.williamcavendishbentinck.tables.DeliveryPointAddress;
import uk.co.epii.politics.williamcavendishbentinck.tables.Postcode;

import java.awt.geom.Point2D;
import java.util.List;

/**
 * User: James Robinson
 * Date: 13/10/2013
 * Time: 00:03
 */
public interface DwellingLoader {

    public List<DeliveryPointAddress> getAddresses(Postcode postcode);
    public List<DeliveryPointAddress> getAddresses(Point2D.Float location, float radius);

}
