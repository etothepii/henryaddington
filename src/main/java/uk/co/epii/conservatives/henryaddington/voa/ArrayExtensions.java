package uk.co.epii.conservatives.henryaddington.voa;

/**
 * User: James Robinson
 * Date: 17/10/2013
 * Time: 18:58
 */
public class ArrayExtensions {

    public static int d(String[] a, String[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Arrays should be the same length.");
        }
        int d = 0;
        int increment = 1;
        int multiplicand = 1;
        for (int i = 0; i < a.length; i++) {
            if ((b[i] == null && a[i] != null) || (b[i] != null && !b[i].equals(a[i]))) {
                if (a[i] != null && b[i] != null) {
                    if ((a[i].contains("|") && a[i].length() > 2) ||
                            (b[i].contains("|") && b[i].length() > 2)) {
                        if (StringExtensions.common(a[i], b[i]) == null) {
                            increment *= 2;
                        }
                    }
                }
                d += increment;
                multiplicand = 2;
            }
            increment *= multiplicand;
        }
        return d;
    }
}
