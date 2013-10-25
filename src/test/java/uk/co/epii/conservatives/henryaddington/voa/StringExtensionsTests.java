package uk.co.epii.conservatives.henryaddington.voa;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * User: James Robinson
 * Date: 21/10/2013
 * Time: 23:53
 */
public class StringExtensionsTests {

    @Test
    public void commonTest1() {
        String result = StringExtensions.common("FLAT 1", "FLAT 2");
        String expected = "FLAT |";
        assertEquals(expected, result);
    }

    @Test
    public void commonTest2() {
        String result = StringExtensions.common("FLAT 4 AT 13B", "FLAT 2 AT 13B");
        String expected = "FLAT | AT 13B";
        assertEquals(expected, result);
    }

    @Test
    public void commonTest3() {
        String result = StringExtensions.common("FLAT 1ST FLR AT 31", "FLAT 2ND FLR AT 31");
        String expected = "FLAT | FLR AT 31";
        assertEquals(expected, result);
    }

    @Test
    public void commonTest4() {
        String result = StringExtensions.common("1", "2");
        String expected = null;
        assertEquals(expected, result);
    }

    @Test
    public void commonTest5() {
        String result = StringExtensions.common("FLAT 151A FLR AT 31", "FLAT 151B FLR AT 31");
        String expected = "FLAT | FLR AT 31";
        assertEquals(expected, result);
    }

    @Test
    public void commonTest6() {
        String result = StringExtensions.common("FLAT 1001 FLR AT 31", "FLAT 1002 FLR AT 31");
        String expected = "FLAT | FLR AT 31";
        assertEquals(expected, result);
    }

    @Test
    public void differenceTest1() {
        String result = StringExtensions.difference("FLAT |", "FLAT 2");
        String expected = "2";
        assertEquals(expected, result);
    }

    @Test
    public void differenceTest2() {
        String result = StringExtensions.difference("FLAT | AT 13B", "FLAT 2 AT 13B");
        String expected = "2";
        assertEquals(expected, result);
    }
    @Test
    public void differenceTest3() {
        String result = StringExtensions.difference("FLAT | FLR AT 31", "FLAT 2ND FLR AT 31");
        String expected = "2ND";
        assertEquals(expected, result);
    }

    @Test
    public void differenceTest4() {
        String result = StringExtensions.difference(null, "2");
        String expected = "2";
        assertEquals(expected, result);
    }

}
