package uk.co.epii.conservatives.henryaddington.voa;

import uk.co.epii.conservatives.williamcavendishbentinck.tables.DeliveryPointAddress;
import uk.co.epii.spencerperceval.tuple.Duple;

import java.util.List;

/**
 * User: James Robinson
 * Date: 13/10/2013
 * Time: 00:03
 */
public interface DwellingLoader {

    public List<DeliveryPointAddress> getAddresses(String postcode);

}
