package uk.co.epii.conservatives.henryaddington.voa;

import org.junit.Test;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.Dwelling;

import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertEquals;

/**
 * User: James Robinson
 * Date: 18/10/2013
 * Time: 22:13
 */
public class StubDwellingTests {

    @Test
    public void linedUpTest1() {
        StubDwelling a = new StubDwelling(new Dwelling("1ST FLR 137, BRICK LANE, LONDON",
                null, 'A', null, null, null));
        StubDwelling b = new StubDwelling(new Dwelling("FLAT 2, WOLF COURT 546, ROMAN ROAD, LONDON",
                null, 'A', null, null, null));
        String[][] linedUp = a.getLinedUp(b);
        assertEquals(4, linedUp[0].length);
        assertEquals(4, linedUp[1].length);
        assertNull(linedUp[0][0]);
        assertEquals("1ST FLR 137", linedUp[0][1]);
        assertEquals("BRICK LANE", linedUp[0][2]);
        assertEquals("LONDON", linedUp[0][3]);
        assertEquals("FLAT 2", linedUp[1][0]);
        assertEquals("WOLF COURT 546", linedUp[1][1]);
        assertEquals("ROMAN ROAD", linedUp[1][2]);
        assertEquals("LONDON", linedUp[1][3]);
    }

}
