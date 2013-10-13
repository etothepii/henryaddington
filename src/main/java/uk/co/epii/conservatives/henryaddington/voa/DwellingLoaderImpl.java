package uk.co.epii.conservatives.henryaddington.voa;

import uk.co.epii.conservatives.williamcavendishbentinck.DatabaseSession;
import uk.co.epii.conservatives.williamcavendishbentinck.extensions.DeliveryPointAddressExtensions;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.BLPU;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.DeliveryPointAddress;
import uk.co.epii.spencerperceval.tuple.Duple;

import java.util.ArrayList;
import java.util.List;

/**
 * User: James Robinson
 * Date: 13/10/2013
 * Time: 00:08
 */
public class DwellingLoaderImpl implements DwellingLoader {

    private DatabaseSession databaseSession;

    public DwellingLoaderImpl() {
        databaseSession = new DatabaseSession();
    }

    @Override
    public List<DeliveryPointAddress> getAddresses(String postcode) {
        List<Duple<BLPU, DeliveryPointAddress>> dwellings = databaseSession.getHouses(postcode);
        List<DeliveryPointAddress> addresses = new ArrayList<DeliveryPointAddress>(dwellings.size());
        for (Duple<BLPU, DeliveryPointAddress> blpuAddress : dwellings) {
            addresses.add(blpuAddress.getSecond());
        }
        return addresses;
    }
}
