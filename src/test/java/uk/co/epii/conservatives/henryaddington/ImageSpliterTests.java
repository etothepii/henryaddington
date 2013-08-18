package uk.co.epii.conservatives.henryaddington;

import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.assertEquals;

/**
 * User: James Robinson
 * Date: 18/08/2013
 * Time: 03:31
 */
public class ImageSpliterTests {

    private static ImageSpliter imageSpliter;

    @BeforeClass
    public static void setUpImageSpliter() {
        imageSpliter = new ImageSpliter(null, 10, 5);
        imageSpliter.width = 20;
        imageSpliter.height = 40;
    }

    @Test
    public void getRectangleTest1() {
        Rectangle result = imageSpliter.getRectangle(0, 0);
        Rectangle expected = new Rectangle(0, 160, 20, 40);
        assertEquals(expected, result);
    }

    @Test
    public void getRectangleTest2() {
        Rectangle result = imageSpliter.getRectangle(3, 2);
        Rectangle expected = new Rectangle(60, 80, 20, 40);
        assertEquals(expected, result);
    }

    @Test
    public void getRectangleTest3() {
        Rectangle result = imageSpliter.getRectangle(9, 4);
        Rectangle expected = new Rectangle(180, 0, 20, 40);
        assertEquals(expected, result);
    }

}
