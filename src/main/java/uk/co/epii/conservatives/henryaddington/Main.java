package uk.co.epii.conservatives.henryaddington;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

/**
 * User: James Robinson
 * Date: 18/08/2013
 * Time: 02:47
 */
public class Main {

    private static String imageFormat = "tif";
    private static int columns;
    private static int rows;

    public static void main(String[] args) {
        if (args.length > 3 && args[0].equals("IMAGE_SPLIT")) {
            columns = Integer.parseInt(args[1]);
            rows = Integer.parseInt(args[2]);
            imageSplit(Arrays.copyOfRange(args, 3, args.length));
        }
    }

    private static void imageSplit(String[] files) {
        ImageSpliter imageSpliter = new ImageSpliter(imageFormat, columns, rows);
        FilenameFilter imageFiles = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(imageFormat);
            }
        };
        for (String fileString : files) {
            File file = new File(fileString);
            if (file.isDirectory()) {
                for (File child : file.listFiles(imageFiles)) {
                    imageSpliter.splitImage(child);
                }
            }
            else {
                imageSpliter.splitImage(file);
            }
        }
    }
}
