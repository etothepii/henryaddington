package uk.co.epii.conservatives.henryaddington.voa;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.spencerperceval.FileLineIterable;
import uk.co.epii.spencerperceval.tuple.Duple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    private VOAUploader voaUploader = new VOAUploader();
    private List<String> dwellings = new ArrayList<String>();
    private TestDwellingLoader testDwellingLoader = new TestDwellingLoader();

    @Before
    public void setUp() {
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
        List<Duple<String, String>> list = new ArrayList<Duple<String, String>>();
        list.add(new Duple<String, String>("101, ZETLAND STREET, LONDON, E14 6PR", "6085480"));
        list.add(new Duple<String, String>("103, ZETLAND STREET, LONDON, E14 6PR", "6085481"));
        list.add(new Duple<String, String>("105, ZETLAND STREET, LONDON, E14 6PR", "6085482"));
        list.add(new Duple<String, String>("107, ZETLAND STREET, LONDON, E14 6PR", "6085483"));
        testDwellingLoader.addAll("E14 6PR", list);
        list = new ArrayList<Duple<String, String>>();
        list.add(new Duple<String, String>("FLAT 5, PRIESTMAN POINT, 2, RAINHILL WAY, LONDON, E3 3EY", "6167205"));
        list.add(new Duple<String, String>("FLAT 7, PRIESTMAN POINT, 2, RAINHILL WAY, LONDON, E3 3EY", "6167207"));
        list.add(new Duple<String, String>("FLAT 8, PRIESTMAN POINT, 2, RAINHILL WAY, LONDON, E3 3EY", "6167208"));
        list.add(new Duple<String, String>("FLAT 9, PRIESTMAN POINT, 2, RAINHILL WAY, LONDON, E3 3EY", "6167209"));
        testDwellingLoader.addAll("E3 3EY", list);
        list = new ArrayList<Duple<String, String>>();
        list.add(new Duple<String, String>("11, HARFORD STREET, LONDON, E1 4PQ", "6048547"));
        testDwellingLoader.addAll("E1 4PQ", list);

    }

    @Test
    public void processDwellingsTest() {
        List<Duple<String, String>> result = voaUploader.processDwellings(dwellings);
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

    @Test
    public void removeFlatTest() {
        String result = voaUploader.removeFlat("FLAT 1ST FLR 1, HARFORD STREET, LONDON");
        String expected = "11, HARFORD STREET, LONDON";
        assertEquals(expected, result);
    }

    @Test
    public void processDwellingsTestSpoof() {
        voaUploader.setDwellingLoader(new DwellingLoaderImpl());
        voaUploader.processDwellings(new FileLineIterable(
                String.format("%s/frederickNorth/Data/dwellings/TOWER HAMLETS.txt", System.getProperty("user.home"))));
    }

    public void assertCollection(Collection<?> expectedCollection, Collection<?> actualCollection) {
        assertNotNull(expectedCollection);
        assertNotNull(actualCollection);
        assertEquals(expectedCollection.size(), actualCollection.size());
        assertTrue(expectedCollection.containsAll(actualCollection));
        assertTrue(actualCollection.containsAll(expectedCollection));
    }


}
