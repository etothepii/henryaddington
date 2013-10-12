package uk.co.epii.conservatives.henryaddington.voa;

import uk.co.epii.spencerperceval.FileLineIterable;

import java.io.File;

/**
 * User: James Robinson
 * Date: 12/10/2013
 * Time: 11:26
 */
public class VOAUploader {

    private File dwellingsFolder;
    private File postcodesFolder;

    public File getDwellingsFolder() {
        return dwellingsFolder;
    }

    public void setDwellingsFolder(String dwellingsFolder) {
        this.dwellingsFolder = new File(dwellingsFolder);
    }

    public File getPostcodesFolder() {
        return postcodesFolder;
    }

    public void setPostcodesFolder(String postcodesFolder) {
        this.postcodesFolder = new File(postcodesFolder);
    }

    public void processDwellings() {
        for (File file : dwellingsFolder.listFiles()) {
            processDwellings(new FileLineIterable(file));
        }
    }

    public void processDwellings(Iterable<String> lines) {

    }
}
