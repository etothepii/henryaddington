package uk.co.epii.conservatives.henryaddington.voa;

import org.junit.Test;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.DeliveryPointAddress;

import static junit.framework.TestCase.assertEquals;

/**
 * User: James Robinson
 * Date: 16/10/2013
 * Time: 19:27
 */
public class StubDeliveryPointAddressTests {

    @Test
    public void dTest1() {
        StubDeliveryPointAddress a = new StubDeliveryPointAddress(new DeliveryPointAddress(0, null, 0, 0, null, 0,
                null, null, "FLAT 1", "ARCADIA COURT", 45, null, "OLD CASTLE STREET", null, null, "LONDON",
                null, null, null, null, null, null, null, null, null, null, null, null, null));
        StubDeliveryPointAddress b = new StubDeliveryPointAddress(new DeliveryPointAddress(0, null, 0, 0, null, 0,
                null, null, "FLAT 2", "ARCADIA COURT", 45, null, "OLD CASTLE STREET", null, null, "LONDON",
                null, null, null, null, null, null, null, null, null, null, null, null, null));
        int result = a.d(b);
        int expected = 1;
        assertEquals(expected, result);
    }

    @Test
    public void dTest2() {
        StubDeliveryPointAddress a = new StubDeliveryPointAddress(new DeliveryPointAddress(0, null, 0, 0, null, 0,
                null, null, null, "36A", null, "WESTCOTT HOUSE", "EAST INDIA DOCK ROAD", null, null, "LONDON",
                null, null, null, null, null, null, null, null, null, null, null, null, null));
        StubDeliveryPointAddress b = new StubDeliveryPointAddress(new DeliveryPointAddress(0, null, 0, 0, null, 0,
                null, null, "FLAT 2", "ARCADIA COURT", 45, null, "OLD CASTLE STREET", null, null, "LONDON",
                null, null, null, null, null, null, null, null, null, null, null, null, null));
        int result = a.d(b);
        int expected = 15;
        assertEquals(expected, result);
    }

    @Test
    public void dTest3() {
        StubDeliveryPointAddress a = new StubDeliveryPointAddress(new DeliveryPointAddress(0, null, 0, 0, null, 0,
                null, null, null, "36A", null, "WESTCOTT HOUSE", "EAST INDIA DOCK ROAD", null, null, "LONDON",
                null, null, null, null, null, null, null, null, null, null, null, null, null));
        StubDeliveryPointAddress b = new StubDeliveryPointAddress(new DeliveryPointAddress(0, null, 0, 0, null, 0,
                null, null, null, null, 40, "WESTCOTT HOUSE", "EAST INDIA DOCK ROAD", null, null, "LONDON",
                null, null, null, null, null, null, null, null, null, null, null, null, null));
        int result = a.d(b);
        int expected = 1;
        assertEquals(expected, result);
    }

}
