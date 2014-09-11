package uk.co.epii.conservatives.henryaddington.voa;

import uk.co.epii.politics.williamcavendishbentinck.tables.Postcode;

/**
 * User: James Robinson
 * Date: 26/10/2013
 * Time: 22:55
 */

public interface PostcodeLoader {

    public Postcode getPostcode(String postcode);
    public void loadPostcode(String postcode);
    public String extractPostcode(String line);

}
