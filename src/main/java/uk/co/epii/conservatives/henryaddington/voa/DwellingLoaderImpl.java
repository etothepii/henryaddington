package uk.co.epii.conservatives.henryaddington.voa;

import uk.co.epii.conservatives.williamcavendishbentinck.DatabaseSession;
import uk.co.epii.conservatives.williamcavendishbentinck.extensions.DeliveryPointAddressExtensions;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.BLPU;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.DeliveryPointAddress;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.Postcode;
import uk.co.epii.spencerperceval.tuple.Duple;

import java.awt.*;
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
        List<Duple<DeliveryPointAddress, BLPU>> dwellings = databaseSession.fromPostcode(postcode.getPostcode(),
                DeliveryPointAddress.class, BLPU.class, "UPRN", "UPRN");
        List<DeliveryPointAddress> addresses = new ArrayList<DeliveryPointAddress>(dwellings.size());
        for (Duple<DeliveryPointAddress, BLPU> blpuAddress : dwellings) {
            addresses.add(blpuAddress.getFirst());
        }
        return addresses;
    }

    @Override
    public List<DeliveryPointAddress> getAddresses(Point2D.Float location, float radius) {
        List<Duple<BLPU, DeliveryPointAddress>> dwellings = databaseSession.containedWithin(
                new Rectangle(
                        (int)Math.floor(location.getX() - radius),
                        (int)Math.floor(location.getY() - radius),
                        (int)Math.ceil(radius * 2),
                        (int)Math.ceil(radius * 2)),
                BLPU.class, DeliveryPointAddress.class, "UPRN", "UPRN");
        List<DeliveryPointAddress> addresses = new ArrayList<DeliveryPointAddress>(dwellings.size());
        for (Duple<BLPU, DeliveryPointAddress> blpuAddress : dwellings) {
            BLPU blpu = blpuAddress.getFirst();
            if (blpu == null) {
                continue;
            }
            if (Math.pow(blpu.getXCoordinate() - location.getX(), 2) +
                    Math.pow(blpu.getYCoordinate() - location.getY(), 2) <= Math.pow(radius, 2)) {
                addresses.add(blpuAddress.getSecond());
            }
        }
        return addresses;
    }
}
