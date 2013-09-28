package uk.co.epii.conservatives.henryaddington;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.co.epii.conservatives.henryaddington.hibernate.HibernateBuilder;
import uk.co.epii.conservatives.henryaddington.hibernate.HibernatePrinter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

/**
 * User: James Robinson
 * Date: 18/08/2013
 * Time: 02:47
 */
public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private static String imageFormat = "tif";
    private static int columns;
    private static int rows;
    private static String[][] splitDescription;

    public static void main(String[] args) {
        if (args.length > 3 && args[0].equals("NUMERIC_IMAGE_SPLIT")) {
            columns = Integer.parseInt(args[1]);
            rows = Integer.parseInt(args[2]);
            imageSplit(Arrays.copyOfRange(args, 3, args.length));
        }
        else if (args.length > 3 && args[0].equals("DESCRIBED_IMAGE_SPLIT")) {
            splitDescription = ImageSpliter.getSplitDescription(new File(args[1]), args[2], Integer.parseInt(args[3]));
            imageSplit(Arrays.copyOfRange(args, 4, args.length));
        }
        else if (args.length > 0 && args[0].equals("DATABASE")) {
            ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
            DatabaseUploader databaseUploader = (DatabaseUploader)context.getBean("databaseUploader");
            if (databaseUploader.cleanDatabase()) {
                LOG.info("Successfully Cleaned Database");
                databaseUploader.upload();
            }
            else {
                LOG.info("Failed to Cleaned Database");
            }
            HibernateBuilder hibernateBuilder = (HibernateBuilder)context.getBean("hibernateBuilder");
            hibernateBuilder.process();
        }
    }

    private static void imageSplit(String[] files) {
        ImageSpliter imageSpliter = splitDescription == null ?
                new ImageSpliter(imageFormat, columns, rows) : new ImageSpliter(imageFormat, splitDescription);
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
