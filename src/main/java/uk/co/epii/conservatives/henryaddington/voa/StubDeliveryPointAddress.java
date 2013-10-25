package uk.co.epii.conservatives.henryaddington.voa;

import uk.co.epii.conservatives.williamcavendishbentinck.tables.DeliveryPointAddress;
import uk.co.epii.spencerperceval.util.Groupable;

/**
 * User: James Robinson
 * Date: 15/10/2013
 * Time: 22:18
 */
public class StubDeliveryPointAddress implements Groupable<StubDeliveryPointAddress> {

    private static final int LINES = 9;

    private final String[] address;

    public StubDeliveryPointAddress(DeliveryPointAddress address) {
        this.address = new String[] {
                address.getDepartmentName(),
                address.getOrganisationName(),
                address.getSubBuildingName(),
                address.getBuildingName() == null ?
                        address.getBuildingNumber() == null ? null : address.getBuildingNumber() + "" :
                        address.getBuildingName() + address.getBuildingNumber() == null ?
                                "" : (" " + address.getBuildingName()),
                address.getDependentThoroughfareName(),
                address.getThoroughfareName(),
                address.getDoubleDependentLocality(),
                address.getDependentLocality(),
                address.getPostTown()
        };
    }

    public StubDeliveryPointAddress(String[] address) {
        this.address = address;
    }

    @Override
    public StubDeliveryPointAddress getCommon(StubDeliveryPointAddress other) {
        String[] address = new String[LINES];
        for (int i = 0; i < LINES; i++) {
            address[i] = StringExtensions.common(this.address[i], other.address[i]);
        }
        return new StubDeliveryPointAddress(address);
    }

    @Override
    public int d(StubDeliveryPointAddress other) {
        return ArrayExtensions.d(address, other.address);
    }

    @Override
    public String getDifference(StubDeliveryPointAddress other) {
        for (int i = 0; i < address.length; i++) {
            if (other.address[i] == null && address[i] == null) {
                continue;
            }
            if (other.address[i] != null && address[i] == null) {
                return other.address[i];
            }
            else if (address[i] != null && other.address[i] == null) {
                return address[i];
            }
            else if (address[i].contains("|")) {
                return StringExtensions.difference(address[i], other.address[i]);
            }
            else if (other.address[i].contains("|")) {
                return StringExtensions.difference(other.address[i], address[i]);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String addressLine : address) {
            if (addressLine == null || addressLine.contains("|")) {
                continue;
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(addressLine);
        }
        return stringBuilder.toString();
    }
}
