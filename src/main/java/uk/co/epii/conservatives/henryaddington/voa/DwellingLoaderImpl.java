package uk.co.epii.conservatives.henryaddington.voa;

import uk.co.epii.conservatives.williamcavendishbentinck.DatabaseSession;
import uk.co.epii.conservatives.williamcavendishbentinck.extensions.DeliveryPointAddressExtensions;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.BLPU;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.DeliveryPointAddress;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.Postcode;
import uk.co.epii.spencerperceval.tuple.Duple;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * User: James Robinson
 * Date: 13/10/2013
 * Time: 00:08
 */
public class DwellingLoaderImpl implements DwellingLoader {

    private DatabaseSession databaseSession;

    public DatabaseSession getDatabaseSession() {
        return databaseSession;
    }

    public void setDatabaseSession(DatabaseSession databaseSession) {
        this.databaseSession = databaseSession;
    }

    private DwellingLoaderImpl() {
    }

    @Override
    public List<DeliveryPointAddress> getAddresses(Postcode postcode) {
        List<Duple<BLPU, DeliveryPointAddress>> dwellings = databaseSession.getHouses(postcode.getPostcode());
        List<DeliveryPointAddress> addresses = new ArrayList<DeliveryPointAddress>(dwellings.size());
        for (Duple<BLPU, DeliveryPointAddress> blpuAddress : dwellings) {
            addresses.add(blpuAddress.getSecond());
        }
        return addresses;
    }

    @Override
    public List<DeliveryPointAddress> getAddresses(Point2D.Float location, float radius) {
        List<Duple<BLPU, DeliveryPointAddress>> dwellings = databaseSession.getHouses(location, radius);
        List<DeliveryPointAddress> addresses = new ArrayList<DeliveryPointAddress>(dwellings.size());
        for (Duple<BLPU, DeliveryPointAddress> blpuAddress : dwellings) {
            addresses.add(blpuAddress.getSecond());
        }
        return addresses;
    }
}
