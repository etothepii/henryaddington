package uk.co.epii.conservatives.henryaddington.voa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;

/**
 * User: James Robinson
 * Date: 06/10/2013
 * Time: 19:34
 */
public class VOAMerger {

    private static final Logger LOG = LoggerFactory.getLogger(VOAMerger.class);

    private final File dirFrom;
    private final File dirTo;

    public VOAMerger(File dirFrom, File dirTo) {
        this.dirFrom = dirFrom;
        this.dirTo = dirTo;
    }

    public void merge() throws IOException {
        File[] files = dirFrom.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File file, File file2) {
                return file.toString().compareTo(file2.toString());
            }
        });
        String previousName = null;
        FileWriter fileWriter = null;
        PrintWriter printWriter = null;
        for (File file : files) {
            String name = file.getName();
            if (!name.endsWith(".txt")) {
                continue;
            }
            String stem = name.substring(0, name.length() - 6);
            if (previousName == null || !previousName.equals(stem)) {
                previousName = stem;
                String out = String.format("%s/%s.txt", dirTo.getAbsolutePath(), stem);
                LOG.debug("Writing to: {}", out);
                if (printWriter != null) {
                    printWriter.flush();
                    printWriter.close();
                }
                if (fileWriter != null) {
                    fileWriter.close();
                }
                fileWriter = new FileWriter(out, false);
                printWriter = new PrintWriter(fileWriter, true);
            }
            LOG.debug("Reading from: {}", name);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String in;
            while ((in = bufferedReader.readLine()) != null) {
                printWriter.println(in);
            }
        }
        printWriter.flush();
        printWriter.close();
    }
}
