package uk.co.epii.conservatives.henryaddington;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: James Robinson
 * Date: 18/08/2013
 * Time: 02:48
 */
public class ImageSpliter {

    private static final Logger LOG = LoggerFactory.getLogger(ImageSpliter.class);

    private final Pattern regex = Pattern.compile("(.*)\\.([^\\.]*)$");
    private final int rows;
    private final int columns;
    private final String imageFormat;

    private String fileStem;
    private BufferedImage workingImage;
    private File workingFile;
    int width;
    int height;

    public ImageSpliter(String imageFormat, int columns, int rows) {
        this.imageFormat = imageFormat;
        this.columns = columns;
        this.rows = rows;
    }

    public void splitImage(File file) {
        loadImage(file);
        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < rows; y++) {
                File outputFile = getOutputFile(x, y);
                Rectangle rectangle = getRectangle(x, y);
                LOG.debug("{} ==> {}", new Object[] {rectangle, outputFile});
                BufferedImage childImage = createSubImage(rectangle);
                writeImage(childImage, outputFile);
            }
        }
    }

    private void writeImage(BufferedImage childImage, File outputFile) {
        try {
            ImageIO.write(childImage, imageFormat, outputFile);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedImage createSubImage(Rectangle rectangle) {
        return workingImage.getSubimage(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    Rectangle getRectangle(int x, int y) {
        return new Rectangle(x * width, (rows - 1 - y) * height, width, height);
    }

    private File getOutputFile(int x, int y) {
        return new File(String.format("%s%d%d.%s", new Object[] {fileStem, x, y, imageFormat}));
    }

    private void loadImage(File file) {
        try {
            LOG.debug("Loading {}", file);
            workingFile = file;
            Matcher matcher = regex.matcher(file.toString());
            if (!matcher.matches()) {
                throw new IllegalArgumentException("All files must have an extension");
            }
            fileStem = String.format("%s%s", matcher.group(1), "_");
            workingImage = ImageIO.read(workingFile);
            if (workingImage == null) {
                LOG.error("Supported File Formats");
                for (String fileFormat : ImageIO.getReaderFormatNames()) {
                    LOG.error("{}", fileFormat);
                }
                RuntimeException toThrow = new IllegalArgumentException("This file type is not supported");
                LOG.error(toThrow.getMessage(), toThrow);
                throw toThrow;

            }
            if (workingImage.getWidth() % columns != 0) {
                throw new IllegalArgumentException("The width of this image is not a multiple pf the number of desired columns");
            }
            if (workingImage.getHeight() % rows != 0) {
                throw new IllegalArgumentException("The height of this image is not a multiple pf the number of desired rows");
            }
            width = workingImage.getWidth() / columns;
            height = workingImage.getHeight() / rows;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
