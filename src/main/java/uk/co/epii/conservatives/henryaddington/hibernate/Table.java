package uk.co.epii.conservatives.henryaddington.hibernate;

import java.util.ArrayList;
import java.util.List;

/**
 * User: James Robinson
 * Date: 27/09/2013
 * Time: 22:09
 */
public class Table {

    public final String name;
    public final List<Field> fields;

    public Table(String name) {
        this.name = name;
        fields = new ArrayList<Field>();
    }

}
