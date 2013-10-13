package uk.co.epii.conservatives.henryaddington.voa;

import uk.co.epii.spencerperceval.tuple.Duple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * User: James Robinson
 * Date: 13/10/2013
 * Time: 02:02
 */
public class TestDwellingLoader implements DwellingLoader {

    HashMap<String, List<Duple<String, String>>> maps = new HashMap<String, List<Duple<String, String>>>();

    @Override
    public List<Duple<String, String>> getAddresses(String postcode) {
        return maps.get(postcode);
    }

    public void addAll(String postcode, Collection<Duple<String, String>> collection) {
        List<Duple<String, String>> list = maps.get(postcode);
        if (list == null) {
            list = new ArrayList<Duple<String, String>>();
            maps.put(postcode, list);
        }
        list.addAll(collection);
    }
}
