package uk.co.epii.conservatives.henryaddington.voa;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.politics.williamcavendishbentinck.tables.DeliveryPointAddress;
import uk.co.epii.politics.williamcavendishbentinck.tables.Dwelling;
import uk.co.epii.politics.williamcavendishbentinck.tables.Postcode;
import uk.co.epii.spencerperceval.tuple.Duple;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;


/**
 * User: James Robinson
 * Date: 13/10/2013
 * Time: 01:37
 */
public class VOAUploaderTests {

    private static final Logger LOG = LoggerFactory.getLogger(VOAUploaderTests.class);
    private TestPostcodeLoader postcodeLoader = new TestPostcodeLoader();
    private VOAUploader voaUploader = new VOAUploader();
    private List<String> dwellings = new ArrayList<String>();
    private TestDwellingLoader testDwellingLoader = new TestDwellingLoader();

    @Before
    public void setUp() {
        Map<String, Postcode> postcodes = new HashMap<String, Postcode>();
        postcodes.put("E1 4PQ", new Postcode("E1 4PQ", 10, 536121f, 182267f));
        postcodes.put("E14 6PR", new Postcode("E1 4PQ", 10, 538020f, 181688f));
        postcodes.put("E3 3EY", new Postcode("E1 4PQ", 10, 537539f, 182580f));
        postcodeLoader.setPostcodes(postcodes);
        voaUploader.setPostcodeLoader(postcodeLoader);
        dwellings.add("101, ZETLAND STREET, LONDON,  E14 6PR~C~NO~898406502");
        dwellings.add("103, ZETLAND STREET, LONDON,  E14 6PR~C~NO~898406609");
        dwellings.add("105, ZETLAND STREET, LONDON,  E14 6PR~C~NO~898406706");
        dwellings.add("107, ZETLAND STREET, LONDON,  E14 6PR~C~NO~898406803");
        dwellings.add("5, PRIESTMAN POINT, 2 RAINHILL WAY, LONDON,  E3 3EY~D~NO~671210009");
        dwellings.add("7, PRIESTMAN POINT, 2 RAINHILL WAY, LONDON,  E3 3EY~D~NO~671210013");
        dwellings.add("8, PRIESTMAN POINT, 2 RAINHILL WAY, LONDON,  E3 3EY~D~NO~671210015");
        dwellings.add("9, PRIESTMAN POINT, 2 RAINHILL WAY, LONDON,  E3 3EY~D~NO~671210017");
        dwellings.add("FLAT 1ST FLR 1, HARFORD STREET, LONDON,  E1 4PQ~B~NO~123456789");
        voaUploader.setDwellingLoader(testDwellingLoader);
        List<DeliveryPointAddress> list = new ArrayList<DeliveryPointAddress>();
        list.add(new DeliveryPointAddress(0, null, 0, 6085480, null, 0, null, null, null, null, 101, null,
                "ZETLAND STREET", null, null, "LONDON", "E14 6PR", null, null, null, null, null, null, null,
                null, null, null, null, null));
        list.add(new DeliveryPointAddress(0, null, 0, 6085481, null, 0, null, null, null, null, 103, null,
                "ZETLAND STREET", null, null, "LONDON", "E14 6PR", null, null, null, null, null, null, null,
                null, null, null, null, null));
        list.add(new DeliveryPointAddress(0, null, 0, 6085482, null, 0, null, null, null, null, 105, null,
                "ZETLAND STREET", null, null, "LONDON", "E14 6PR", null, null, null, null, null, null, null,
                null, null, null, null, null));
        list.add(new DeliveryPointAddress(0, null, 0, 6085483, null, 0, null, null, null, null, 107, null,
                "ZETLAND STREET", null, null, "LONDON", "E14 6PR", null, null, null, null, null, null, null,
                null, null, null, null, null));
        testDwellingLoader.addAll(postcodeLoader.getPostcode("E14 6PR"), list);
        list = new ArrayList<DeliveryPointAddress>();
        list.add(new DeliveryPointAddress(0, null, 0, 6167205, null, 0, null, null, "FLAT 5", "PRIESTMAN POINT",
                2, null, "RAINHILL WAY", null, null, "LONDON", "E3 3EY", null, null, null, null, null, null, null,
                null, null, null, null, null));
        list.add(new DeliveryPointAddress(0, null, 0, 6167207, null, 0, null, null, "FLAT 7", "PRIESTMAN POINT",
                2, null, "RAINHILL WAY", null, null, "LONDON", "E3 3EY", null, null, null, null, null, null, null,
                null, null, null, null, null));
        list.add(new DeliveryPointAddress(0, null, 0, 6167208, null, 0, null, null, "FLAT 8", "PRIESTMAN POINT",
                2, null, "RAINHILL WAY", null, null, "LONDON", "E3 3EY", null, null, null, null, null, null, null,
                null, null, null, null, null));
        list.add(new DeliveryPointAddress(0, null, 0, 6167209, null, 0, null, null, "FLAT 9", "PRIESTMAN POINT",
                2, null, "RAINHILL WAY", null, null, "LONDON", "E3 3EY", null, null, null, null, null, null, null,
                null, null, null, null, null));
        testDwellingLoader.addAll(postcodeLoader.getPostcode("E3 3EY"), list);
        list = new ArrayList<DeliveryPointAddress>();
        list.add(new DeliveryPointAddress(0, null, 0, 6048547, null, 0, null, null, null, null,
                11, null, "HARFORD STREET", null, null, "LONDON", "E1 4PQ", null, null, null, null, null, null, null,
                null, null, null, null, null));
        testDwellingLoader.addAll(postcodeLoader.getPostcode("E1 4PQ"), list);
        voaUploader.setEquivalence(new DwellingDeliveryPointAddressEquivalence());
    }

    @Test
    public void processDwellingsTest() {
        List<Duple<String, String>> result = new ArrayList<Duple<String, String>>();
        for (Dwelling dwelling : voaUploader.processDwellings(dwellings)) {
            result.add(new Duple<String, String>(dwelling.getUprn() + "", dwelling.getCouncilTaxBand() + ""));
        }
        List<Duple<String, String>> expected = new ArrayList<Duple<String, String>>(8);
        expected.add(new Duple<String, String>("6167205", "D"));
        expected.add(new Duple<String, String>("6167207", "D"));
        expected.add(new Duple<String, String>("6167208", "D"));
        expected.add(new Duple<String, String>("6167209", "D"));
        expected.add(new Duple<String, String>("6085480", "C"));
        expected.add(new Duple<String, String>("6085481", "C"));
        expected.add(new Duple<String, String>("6085482", "C"));
        expected.add(new Duple<String, String>("6085483", "C"));
        expected.add(new Duple<String, String>("6048547", "B"));
        assertCollection(expected, result);
    }

    public void assertCollection(Collection<?> expectedCollection, Collection<?> actualCollection) {
        assertNotNull(expectedCollection);
        assertNotNull(actualCollection);
        assertEquals(expectedCollection.size(), actualCollection.size());
        assertTrue(expectedCollection.containsAll(actualCollection));
        assertTrue(actualCollection.containsAll(expectedCollection));
    }


}
