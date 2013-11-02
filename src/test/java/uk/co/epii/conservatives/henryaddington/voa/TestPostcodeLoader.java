package uk.co.epii.conservatives.henryaddington.voa;

import uk.co.epii.conservatives.williamcavendishbentinck.tables.Postcode;
import uk.co.epii.spencerperceval.data.PostcodeMatcher;

import java.util.HashMap;
import java.util.Map;

/**
 * User: James Robinson
 * Date: 26/10/2013
 * Time: 23:31
 */
public class TestPostcodeLoader implements PostcodeLoader {

    private Map<String, Postcode> postcodes = new HashMap<String, Postcode>();
    private PostcodeMatcher postcodeMatcher = new PostcodeMatcher();

    public void setPostcodes(Map<String, Postcode> postcodes) {
        this.postcodes = postcodes;
    }

    @Override
    public Postcode getPostcode(String postcode) {
        return postcodes.get(postcode);
    }

    @Override
    public void loadPostcode(String postcode) {}

    @Override
    public String extractPostcode(String line) {
        return postcodeMatcher.extractPostcode(line);
    }
}
