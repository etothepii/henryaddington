package uk.co.epii.conservatives.henryaddington;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.co.epii.conservatives.henryaddington.hibernate.HibernateBuilder;
import uk.co.epii.conservatives.henryaddington.voa.VOADownloader;
import uk.co.epii.conservatives.henryaddington.voa.VOAMerger;
import uk.co.epii.conservatives.henryaddington.voa.VOAUploader;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

/**
 * User: James Robinson
 * Date: 18/08/2013
 * Time: 02:47
 */
public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private static ApplicationContext context;

    private static String imageFormat = "tif";
    private static int columns;
    private static int rows;
    private static String[][] splitDescription;

    public static void main(String[] args) {
        try {
            context = new ClassPathXmlApplicationContext("applicationContext.xml");
            if (args[0].equals("NUMERIC_IMAGE_SPLIT")) {
                numericImageSplit(args);
            }
            else if (args[0].equals("DESCRIBED_IMAGE_SPLIT")) {
                describedImageSplit(args);
            }
            else if (args[0].equals("DATABASE")) {
                database();
            }
            else if (args[0].equals("VOA_MERGE")) {
                voaMerge(args);
            }
            else if (args[0].equals("VOA")) {
                voa(args);
            }
            else if (args[0].equals("VOA_SINGLE_BAND")) {
                voaSingleBand(args);
            }
        }
        catch (Throwable t) {
            LOG.error(t.getMessage(), t);
        }
    }

    private static void voaSingleBand(String[] args) {
        VOADownloader downloader = (VOADownloader)context.getBean("voaDownloader");
        downloader.init();
        downloader.download(args[1], args[2]);
    }

    private static void voa(String[] args) {
        VOADownloader downloader = (VOADownloader)context.getBean("voaDownloader");
        downloader.init();
        if (args[1].equals("ALL")) {
            downloader.downloadAll();
        }
        else {
            for (int i = 1; i < args.length; i++) {
                    downloader.download(args[i]);
            }
        }
    }

    private static void voaMerge(String[] args) {
        VOAMerger voaMerger = new VOAMerger(new File(args[1]), new File(args[2]));
        try {
            voaMerger.merge();
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private static void database() {
        DatabaseUploader databaseUploader = (DatabaseUploader)context.getBean("databaseUploader");
        databaseUploader.init();
        if (databaseUploader.cleanDatabase()) {
            LOG.info("Successfully Cleaned Database");
            databaseUploader.upload();
          LOG.info("Loaded files specified in applicationContext.xml");
        }
        else {
            LOG.info("Failed to Cleaned Database");
        }
        HibernateBuilder hibernateBuilder = (HibernateBuilder)context.getBean("hibernateBuilder");
        hibernateBuilder.process();
    }

    private static void describedImageSplit(String[] args) {
        splitDescription = ImageSplitter.getSplitDescription(new File(args[1]), args[2], Integer.parseInt(args[3]));
        imageSplit(Arrays.copyOfRange(args, 4, args.length));
    }

    private static void numericImageSplit(String[] args) {
        columns = Integer.parseInt(args[1]);
        rows = Integer.parseInt(args[2]);
        imageSplit(Arrays.copyOfRange(args, 3, args.length));
    }

    private static void imageSplit(String[] files) {
        ImageSplitter imageSplitter = splitDescription == null ?
                new ImageSplitter(imageFormat, columns, rows) : new ImageSplitter(imageFormat, splitDescription);
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
                    imageSplitter.splitImage(child);
                }
            }
            else {
                imageSplitter.splitImage(file);
            }
        }
    }
}
