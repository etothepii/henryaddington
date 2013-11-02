package uk.co.epii.conservatives.henryaddington.voa;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * User: James Robinson
 * Date: 22/10/2013
 * Time: 08:30
 */
public class ArrayExtensionsTests {

    @Test
    public void dTest1() {
        String[] a = new String[] {null, null, "FLAT |", "A STREET", "B TOWN"};
        String[] b = new String[] {null, null, "FLAT 1", "A STREET", "B TOWN"};
        int result = ArrayExtensions.d(a, b);
        int expected = 1;
        assertEquals(expected, result);
    }

    @Test
    public void dTest2() {
        String[] a = new String[] {null, null, "FLAT |", "A STREET", "B TOWN"};
        String[] b = new String[] {null, null, "UNIT 1", "A STREET", "B TOWN"};
        int result = ArrayExtensions.d(a, b);
        int expected = 2;
        assertEquals(expected, result);
    }

    @Test
    public void dTest3() {
        String[] a = new String[] {null, null, null, "A STREET", "B TOWN"};
        String[] b = new String[] {null, null, "UNIT 1", "A STREET", "B TOWN"};
        int result = ArrayExtensions.d(a, b);
        int expected = 1;
        assertEquals(expected, result);
    }

}
