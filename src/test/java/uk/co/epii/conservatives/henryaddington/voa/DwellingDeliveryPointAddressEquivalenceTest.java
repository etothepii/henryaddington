package uk.co.epii.conservatives.henryaddington.voa;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.co.epii.conservatives.williamcavendishbentinck.DatabaseSession;
import uk.co.epii.conservatives.williamcavendishbentinck.DatabaseSessionImpl;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.DeliveryPointAddress;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.Dwelling;

import java.util.*;

import static junit.framework.Assert.*;

/**
 * User: James Robinson
 * Date: 14/10/2013
 * Time: 19:45
 */
public class DwellingDeliveryPointAddressEquivalenceTest {

    private static DwellingDeliveryPointAddressEquivalence instance;
    private static DatabaseSession databaseSession;

    @BeforeClass
    public static void beforeClass() {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        instance = (DwellingDeliveryPointAddressEquivalence)context.getBean("dwellingDeliveryPointAddressEquivalence");
        databaseSession = (DatabaseSession)context.getBean("databaseSession");
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
        assertEquals("Size", 4, idMatches.size());
        assertEquals("100802149", (Long)6133071l, idMatches.get("100802149"));
        assertEquals("100802151", (Long)6133072l, idMatches.get("100802151"));
        assertEquals("100802153", (Long)6133073l, idMatches.get("100802153"));
        assertEquals("100802155", (Long)6133074l, idMatches.get("100802155"));
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
        assertEquals("100802149", (Long) 6133071l, idMatches.get("100802149"));
        assertEquals("100802151", (Long) 6133072l, idMatches.get("100802151"));
        assertEquals("100802153", (Long) 6133073l, idMatches.get("100802153"));
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
            if (entry.getKey() == null) continue;
            idMatches.put(entry.getKey().getLarn(), entry.getValue() == null ? null : entry.getValue().getUprn());
        }
        assertEquals("Size", 1, idMatches.size());
        assertEquals("100400102", (Long) 6064892l, idMatches.get("100400102"));
    }

    @Test
    public void matchesWithRandomUnitTest() {
        List<Dwelling> dwellings = Arrays.asList(new Dwelling[] {
                new Dwelling("1, ABBOTT HOUSE, SMYTHE STREET, LONDON", "E14 0HD", 'C', "100400102", null, null),
                new Dwelling("2, ABBOTT HOUSE, SMYTHE STREET, LONDON", "E14 0HD", 'C', "100400209", null, null),
                new Dwelling("3, ABBOTT HOUSE, SMYTHE STREET, LONDON", "E14 0HD", 'C', "100400306", null, null),
                new Dwelling("4, ABBOTT HOUSE, SMYTHE STREET, LONDON", "E14 0HD", 'C', "100400403", null, null),
                new Dwelling("5, ABBOTT HOUSE, SMYTHE STREET, LONDON", "E14 0HD", 'C', "100400500", null, null),
                new Dwelling("10, ABBOTT HOUSE, SMYTHE STREET, LONDON", "E14 0HD", 'C', "100400501", null, null),
                new Dwelling("11, ABBOTT HOUSE, SMYTHE STREET, LONDON", "E14 0HD", 'C', "100400502", null, null),
        });
        List<DeliveryPointAddress> addresses = Arrays.asList(new DeliveryPointAddress[]{
                new DeliveryPointAddress(0, null, 0, 6064892, null, 0, null, null,
                        "FLAT 1", "ABBOTT HOUSE", null, null, "SMYTHE STREET", null, null, "LONDON", "E14 0HD",
                        null, null, null, null, null, null, null, null, null, null, null, null),
                new DeliveryPointAddress(0, null, 0, 6064893, null, 0, null, null,
                        "FLAT 2", "ABBOTT HOUSE", null, null, "SMYTHE STREET", null, null, "LONDON", "E14 0HD",
                        null, null, null, null, null, null, null, null, null, null, null, null),
                new DeliveryPointAddress(0, null, 0, 6064894, null, 0, null, null,
                        "FLAT 3", "ABBOTT HOUSE", null, null, "SMYTHE STREET", null, null, "LONDON", "E14 0HD",
                        null, null, null, null, null, null, null, null, null, null, null, null),
                new DeliveryPointAddress(0, null, 0, 6064895, null, 0, null, null,
                        "FLAT 4", "ABBOTT HOUSE", null, null, "SMYTHE STREET", null, null, "LONDON", "E14 0HD",
                        null, null, null, null, null, null, null, null, null, null, null, null),
                new DeliveryPointAddress(0, null, 0, 6064896, null, 0, null, null,
                        "FLAT 5", "ABBOTT HOUSE", null, null, "SMYTHE STREET", null, null, "LONDON", "E14 0HD",
                        null, null, null, null, null, null, null, null, null, null, null, null),
                new DeliveryPointAddress(0, null, 0, 6064901, null, 0, null, null,
                        "UNIT 10", "ABBOTT HOUSE", null, null, "SMYTHE STREET", null, null, "LONDON", "E14 0HD",
                        null, null, null, null, null, null, null, null, null, null, null, null),
                new DeliveryPointAddress(0, null, 0, 6064902, null, 0, null, null,
                        "UNIT 11", "ABBOTT HOUSE", null, null, "SMYTHE STREET", null, null, "LONDON", "E14 0HD",
                        null, null, null, null, null, null, null, null, null, null, null, null)
        });
        Map<String, Long> idMatches = new HashMap<String, Long>();
        for (Map.Entry<Dwelling, DeliveryPointAddress> entry : instance.match(dwellings, addresses).entrySet()) {
            idMatches.put(entry.getKey() == null ? null : entry.getKey().getLarn(),
                    entry.getValue() == null ? null : entry.getValue().getUprn());
        }
        assertEquals("Size", 7, idMatches.size());
        assertEquals("100400102", (Long)6064892l, idMatches.get("100400102"));
        assertEquals("100400209", (Long)6064893l, idMatches.get("100400209"));
        assertEquals("100400306", (Long)6064894l, idMatches.get("100400306"));
        assertEquals("100400403", (Long)6064895l, idMatches.get("100400403"));
        assertEquals("100400500", (Long)6064896l, idMatches.get("100400500"));
        assertEquals("100400501", (Long)6064901l, idMatches.get("100400501"));
        assertEquals("100400502", (Long)6064902l, idMatches.get("100400502"));
    }

    @Test
    public void matchesWithBuildingBLPU() {
        List<Dwelling> dwellings = Arrays.asList(new Dwelling[] {
                new Dwelling("1, ABBOTT HOUSE 2, SMYTHE STREET, LONDON", "E14 0HD", 'C', "100400102", null, null),
                new Dwelling("2, ABBOTT HOUSE 2, SMYTHE STREET, LONDON", "E14 0HD", 'C', "100400209", null, null),
                new Dwelling("3, ABBOTT HOUSE 2, SMYTHE STREET, LONDON", "E14 0HD", 'C', "100400306", null, null),
                new Dwelling("4, ABBOTT HOUSE 2, SMYTHE STREET, LONDON", "E14 0HD", 'C', "100400403", null, null),
                new Dwelling("5, ABBOTT HOUSE 2, SMYTHE STREET, LONDON", "E14 0HD", 'C', "100400500", null, null),
                new Dwelling("10, ABBOTT HOUSE 2, SMYTHE STREET, LONDON", "E14 0HD", 'C', "100400500", null, null),
        });
        List<DeliveryPointAddress> addresses = Arrays.asList(new DeliveryPointAddress[]{
                new DeliveryPointAddress(0, null, 0, 6064892, null, 0, null, null,
                        null, null, 2, null, "SMYTHE STREET", null, null, "LONDON", "E14 0HD",
                        null, null, null, null, null, null, null, null, null, null, null, null)
        });
        Map<String, Long> idMatches = new HashMap<String, Long>();
        for (Map.Entry<Dwelling, DeliveryPointAddress> entry : instance.match(dwellings, addresses).entrySet()) {
            idMatches.put(entry.getKey() == null ? null : entry.getKey().getLarn(),
                    entry.getValue() == null ? null : entry.getValue().getUprn());
        }
        assertEquals("Size", 5, idMatches.size());
        assertEquals("100400102", (Long)6064892l, idMatches.get("100400102"));
        assertEquals("100400209", (Long)6064892l, idMatches.get("100400209"));
        assertEquals("100400306", (Long)6064892l, idMatches.get("100400306"));
        assertEquals("100400403", (Long)6064892l, idMatches.get("100400403"));
        assertEquals("100400500", (Long)6064892l, idMatches.get("100400500"));
    }

    @Test
    public void testPostcode() {
        Session session = databaseSession.getSessionFactory().openSession();
        String postcode = "E1 4PE";
        String addressSql = "SELECT * FROM DeliveryPointAddress WHERE POSTCODE = :postcode";
        SQLQuery query = session.createSQLQuery(addressSql);
        query.addEntity(DeliveryPointAddress.class);
        query.setParameter("postcode", postcode);
        List<DeliveryPointAddress> addressList = query.list();
        String dwellingSql = "SELECT * FROM Dwelling WHERE POSTCODE = :postcode";
        query = session.createSQLQuery(dwellingSql);
        query.addEntity(Dwelling.class);
        query.setParameter("postcode", postcode);
        List<Dwelling> dwellingList = query.list();
        instance.match(dwellingList, addressList);
    }
}
