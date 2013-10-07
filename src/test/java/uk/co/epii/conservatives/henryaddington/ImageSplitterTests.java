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
public class ImageSplitterTests {

    private static ImageSplitter imageSplitter;

    @BeforeClass
    public static void setUpImageSpliter() {
        imageSplitter = new ImageSplitter(null, 10, 5);
        imageSplitter.width = 20;
        imageSplitter.height = 40;
    }

    @Test
    public void getRectangleTest1() {
        Rectangle result = imageSplitter.getRectangle(0, 0);
        Rectangle expected = new Rectangle(0, 160, 20, 40);
        assertEquals(expected, result);
    }

    @Test
    public void getRectangleTest2() {
        Rectangle result = imageSplitter.getRectangle(3, 2);
        Rectangle expected = new Rectangle(60, 80, 20, 40);
        assertEquals(expected, result);
    }

    @Test
    public void getRectangleTest3() {
        Rectangle result = imageSplitter.getRectangle(9, 4);
        Rectangle expected = new Rectangle(180, 0, 20, 40);
        assertEquals(expected, result);
    }

}
