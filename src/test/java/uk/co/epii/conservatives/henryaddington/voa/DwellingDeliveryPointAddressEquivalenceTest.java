package uk.co.epii.conservatives.henryaddington.voa;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.co.epii.conservatives.henryaddington.DatabaseUploader;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.DeliveryPointAddress;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.Dwelling;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * User: James Robinson
 * Date: 14/10/2013
 * Time: 19:45
 */
public class DwellingDeliveryPointAddressEquivalenceTest {

    private static DwellingDeliveryPointAddressEquivalence instance;

    @BeforeClass
    public static void beforeClass() {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        instance = (DwellingDeliveryPointAddressEquivalence)context.getBean("dwellingDeliveryPointAddressEquivalence");
    }

    @Test
    public void matchesAbbotAndAbbottTest() {
        List<Dwelling> dwellings = Arrays.asList(new Dwelling[] {
                new Dwelling("1, ABBOTT HOUSE, SMYTHE STREET, LONDON", "E14 0HD", 'C', "100400102", null, null),
                new Dwelling("2, ABBOTT HOUSE, SMYTHE STREET, LONDON", "E14 0HD", 'C', "100400209", null, null),
                new Dwelling("3, ABBOTT HOUSE, SMYTHE STREET, LONDON", "E14 0HD", 'C', "100400306", null, null),
                new Dwelling("4, ABBOTT HOUSE, SMYTHE STREET, LONDON", "E14 0HD", 'C', "100400403", null, null),
                new Dwelling("5, ABBOTT HOUSE, SMYTHE STREET, LONDON", "E14 0HD", 'C', "100400500", null, null),
                new Dwelling("10, ABBOTT HOUSE, SMYTHE STREET, LONDON", "E14 0HD", 'C', "100401004", null, null),
        });
        List<DeliveryPointAddress> addresses = Arrays.asList(new DeliveryPointAddress[]{
                new DeliveryPointAddress(0, null, 0, 6064892, null, 0, null, null,
                        "FLAT 1", "ABBOT HOUSE", null, null, "SMYTHE STREET", null, null, "LONDON", "E14 0HD",
                        null, null, null, null, null, null, null, null, null, null, null, null),
                new DeliveryPointAddress(0, null, 0, 6064893, null, 0, null, null,
                        "FLAT 2", "ABBOT HOUSE", null, null, "SMYTHE STREET", null, null, "LONDON", "E14 0HD",
                        null, null, null, null, null, null, null, null, null, null, null, null),
                new DeliveryPointAddress(0, null, 0, 6064894, null, 0, null, null,
                        "FLAT 3", "ABBOT HOUSE", null, null, "SMYTHE STREET", null, null, "LONDON", "E14 0HD",
                        null, null, null, null, null, null, null, null, null, null, null, null),
                new DeliveryPointAddress(0, null, 0, 6064895, null, 0, null, null,
                        "FLAT 4", "ABBOT HOUSE", null, null, "SMYTHE STREET", null, null, "LONDON", "E14 0HD",
                        null, null, null, null, null, null, null, null, null, null, null, null),
                new DeliveryPointAddress(0, null, 0, 6064896, null, 0, null, null,
                        "FLAT 5", "ABBOT HOUSE", null, null, "SMYTHE STREET", null, null, "LONDON", "E14 0HD",
                        null, null, null, null, null, null, null, null, null, null, null, null),
                new DeliveryPointAddress(0, null, 0, 6064901, null, 0, null, null,
                        "FLAT 10", "ABBOT HOUSE", null, null, "SMYTHE STREET", null, null, "LONDON", "E14 0HD",
                        null, null, null, null, null, null, null, null, null, null, null, null)
        });
        Map<String, Long> idMatches = new HashMap<String, Long>();
        for (Map.Entry<Dwelling, DeliveryPointAddress> entry : instance.match(dwellings, addresses).entrySet()) {
            idMatches.put(entry.getKey() == null ? null : entry.getKey().getLarn(),
                    entry.getValue() == null ? null : entry.getValue().getUprn());
        }
        assertEquals("Size", 6, idMatches.size());
        assertEquals("100400102", (Long)6064892l, idMatches.get("100400102"));
        assertEquals("100400209", (Long)6064893l, idMatches.get("100400209"));
        assertEquals("100400306", (Long)6064894l, idMatches.get("100400306"));
        assertEquals("100400403", (Long)6064895l, idMatches.get("100400403"));
        assertEquals("100400500", (Long)6064896l, idMatches.get("100400500"));
        assertEquals("100401004", (Long)6064901l, idMatches.get("100401004"));
    }

    @Test
    public void matchesAbbottRoadTest() {
        List<Dwelling> dwellings = Arrays.asList(new Dwelling[] {
                new Dwelling("59, ABBOTT ROAD, LONDON", "E14 0GP", 'E', "100802159", null, null),
                new Dwelling("55, ABBOTT ROAD, LONDON", "E14 0GP", 'D', "100802155", null, null),
                new Dwelling("53, ABBOTT ROAD, LONDON", "E14 0GP", 'D', "100802153", null, null),
                new Dwelling("51, ABBOTT ROAD, LONDON", "E14 0GP", 'D', "100802151", null, null),
                new Dwelling("49, ABBOTT ROAD, LONDON", "E14 0GP", 'E', "100802149", null, null),
        });
        List<DeliveryPointAddress> addresses = Arrays.asList(new DeliveryPointAddress[]{
                new DeliveryPointAddress(0, null, 0, 6133071, null, 0, null, null,
                        null, null, 49, null, "ABBOTT ROAD", null, null, "LONDON", "E14 0GP",
                        null, null, null, null, null, null, null, null, null, null, null, null),
                new DeliveryPointAddress(0, null, 0, 6133072, null, 0, null, null,
                        null, null, 51, null, "ABBOTT ROAD", null, null, "LONDON", "E14 0GP",
                        null, null, null, null, null, null, null, null, null, null, null, null),
                new DeliveryPointAddress(0, null, 0, 6133073, null, 0, null, null,
                        null, null, 53, null, "ABBOTT ROAD", null, null, "LONDON", "E14 0GP",
                        null, null, null, null, null, null, null, null, null, null, null, null),
                new DeliveryPointAddress(0, null, 0, 6133074, null, 0, null, null,
                        null, null, 55, null, "ABBOTT ROAD", null, null, "LONDON", "E14 0GP",
                        null, null, null, null, null, null, null, null, null, null, null, null),
                new DeliveryPointAddress(0, null, 0, 6133076, null, 0, null, null,
                        null, null, 59, null, "ABBOTT ROAD", null, null, "LONDON", "E14 0GP",
                        null, null, null, null, null, null, null, null, null, null, null, null),
        });
        Map<String, Long> idMatches = new HashMap<String, Long>();
        for (Map.Entry<Dwelling, DeliveryPointAddress> entry : instance.match(dwellings, addresses).entrySet()) {
            idMatches.put(entry.getKey() == null ? null : entry.getKey().getLarn(),
                    entry.getValue() == null ? null : entry.getValue().getUprn());
        }
        assertEquals("Size", 5, idMatches.size());
        assertEquals("100802149", (Long)6133071l, idMatches.get("100802149"));
        assertEquals("100802151", (Long)6133072l, idMatches.get("100802151"));
        assertEquals("100802153", (Long)6133073l, idMatches.get("100802153"));
        assertEquals("100802155", (Long)6133074l, idMatches.get("100802155"));
        assertEquals("100802159", (Long)6133076l, idMatches.get("100802159"));
    }

    @Test
    public void matchesAnExtraDwellingTest() {
        List<Dwelling> dwellings = Arrays.asList(new Dwelling[] {
                new Dwelling("59, ABBOTT ROAD, LONDON", "E14 0GP", 'E', "100802159", null, null),
                new Dwelling("55, ABBOTT ROAD, LONDON", "E14 0GP", 'D', "100802155", null, null),
                new Dwelling("53, ABBOTT ROAD, LONDON", "E14 0GP", 'D', "100802153", null, null),
                new Dwelling("51, ABBOTT ROAD, LONDON", "E14 0GP", 'D', "100802151", null, null),
                new Dwelling("49, ABBOTT ROAD, LONDON", "E14 0GP", 'E', "100802149", null, null),
        });
        List<DeliveryPointAddress> addresses = Arrays.asList(new DeliveryPointAddress[]{
                new DeliveryPointAddress(0, null, 0, 6133071, null, 0, null, null,
                        null, null, 49, null, "ABBOTT ROAD", null, null, "LONDON", "E14 0GP",
                        null, null, null, null, null, null, null, null, null, null, null, null),
                new DeliveryPointAddress(0, null, 0, 6133072, null, 0, null, null,
                        null, null, 51, null, "ABBOTT ROAD", null, null, "LONDON", "E14 0GP",
                        null, null, null, null, null, null, null, null, null, null, null, null),
                new DeliveryPointAddress(0, null, 0, 6133073, null, 0, null, null,
                        null, null, 53, null, "ABBOTT ROAD", null, null, "LONDON", "E14 0GP",
                        null, null, null, null, null, null, null, null, null, null, null, null),
                new DeliveryPointAddress(0, null, 0, 6133074, null, 0, null, null,
                        null, null, 55, null, "ABBOTT ROAD", null, null, "LONDON", "E14 0GP",
                        null, null, null, null, null, null, null, null, null, null, null, null),
        });
        Map<String, Long> idMatches = new HashMap<String, Long>();
        for (Map.Entry<Dwelling, DeliveryPointAddress> entry : instance.match(dwellings, addresses).entrySet()) {
            idMatches.put(entry.getKey() == null ? null : entry.getKey().getLarn(),
                    entry.getValue() == null ? null : entry.getValue().getUprn());
        }
        assertEquals("Size", 5, idMatches.size());
        assertEquals("100802149", (Long)6133071l, idMatches.get("100802149"));
        assertEquals("100802151", (Long)6133072l, idMatches.get("100802151"));
        assertEquals("100802153", (Long)6133073l, idMatches.get("100802153"));
        assertEquals("100802155", (Long)6133074l, idMatches.get("100802155"));
        assertTrue("100802159", idMatches.containsKey("100802159"));
        assertNull("100802159", idMatches.get("100802159"));
    }
    @Test
    public void matchesAnExtraAddressTest() {
        List<Dwelling> dwellings = Arrays.asList(new Dwelling[] {
                new Dwelling("53, ABBOTT ROAD, LONDON", "E14 0GP", 'D', "100802153", null, null),
                new Dwelling("51, ABBOTT ROAD, LONDON", "E14 0GP", 'D', "100802151", null, null),
                new Dwelling("49, ABBOTT ROAD, LONDON", "E14 0GP", 'E', "100802149", null, null),
        });
        List<DeliveryPointAddress> addresses = Arrays.asList(new DeliveryPointAddress[]{
                new DeliveryPointAddress(0, null, 0, 6133071, null, 0, null, null,
                        null, null, 49, null, "ABBOTT ROAD", null, null, "LONDON", "E14 0GP",
                        null, null, null, null, null, null, null, null, null, null, null, null),
                new DeliveryPointAddress(0, null, 0, 6133072, null, 0, null, null,
                        null, null, 51, null, "ABBOTT ROAD", null, null, "LONDON", "E14 0GP",
                        null, null, null, null, null, null, null, null, null, null, null, null),
                new DeliveryPointAddress(0, null, 0, 6133073, null, 0, null, null,
                        null, null, 53, null, "ABBOTT ROAD", null, null, "LONDON", "E14 0GP",
                        null, null, null, null, null, null, null, null, null, null, null, null),
                new DeliveryPointAddress(0, null, 0, 6133074, null, 0, null, null,
                        null, null, 55, null, "ABBOTT ROAD", null, null, "LONDON", "E14 0GP",
                        null, null, null, null, null, null, null, null, null, null, null, null),
        });
        Map<String, Long> idMatches = new HashMap<String, Long>();
        for (Map.Entry<Dwelling, DeliveryPointAddress> entry : instance.match(dwellings, addresses).entrySet()) {
            idMatches.put(entry.getKey() == null ? null : entry.getKey().getLarn(),
                    entry.getValue() == null ? null : entry.getValue().getUprn());
        }
        assertEquals("Size", 3, idMatches.size());
        assertEquals("100802149", (Long)6133071l, idMatches.get("100802149"));
        assertEquals("100802151", (Long)6133072l, idMatches.get("100802151"));
        assertEquals("100802153", (Long)6133073l, idMatches.get("100802153"));
    }

    @Test
    public void matchesSingleTest() {
        List<Dwelling> dwellings = Arrays.asList(new Dwelling[] {
                new Dwelling("1, ABBOTT HOUSE, SMYTHE STREET, LONDON", "E14 0HD", 'C', "100400102", null, null)
        });
        List<DeliveryPointAddress> addresses = Arrays.asList(new DeliveryPointAddress[]{
                new DeliveryPointAddress(0, null, 0, 6064892, null, 0, null, null,
                        "FLAT 1", "ABBOT HOUSE", null, null, "SMYTHE STREET", null, null, "LONDON", "E14 0HD",
                        null, null, null, null, null, null, null, null, null, null, null, null)
        });
        Map<String, Long> idMatches = new HashMap<String, Long>();
        for (Map.Entry<Dwelling, DeliveryPointAddress> entry : instance.match(dwellings, addresses).entrySet()) {
            idMatches.put(entry.getKey() == null ? null : entry.getKey().getLarn(),
                    entry.getValue() == null ? null : entry.getValue().getUprn());
        }
        assertEquals("Size", 1, idMatches.size());
        assertEquals("100400102", (Long)6064892l, idMatches.get("100400102"));
    }

    public void testE146JL() {

    }
}
