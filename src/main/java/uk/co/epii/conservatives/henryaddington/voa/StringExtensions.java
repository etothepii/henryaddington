package uk.co.epii.conservatives.henryaddington.voa;

/**
 * User: James Robinson
 * Date: 21/10/2013
 * Time: 23:46
 */
public class StringExtensions {

    public static String common(String a, String b) {
        if (a == null || b == null) {
            return null;
        }
        if (a.equals(b)) {
            return a;
        }
        if (a.contains("|")) {
            String[] split = a.split("\\|");
            if ((split.length == 1 && b.startsWith(split[0])) ||
                    (split.length == 2 && b.startsWith(split[0]) && b.endsWith(split[1]))) {
                return a;
            }
            return commonWord(b, split[0] + (split.length == 1 ? "" : split[1]));
        }
        if (b.contains("|")) {
            String[] split = b.split("\\|");
            if ((split.length == 1 && a.startsWith(split[0])) ||
                    (split.length == 2 && a.startsWith(split[0]) && a.endsWith(split[1]))) {
                return b;
            }
            return commonWord(a, split[0] + (split.length == 1 ? "" : split[1]));
        }
        if (a.length() < b.length()) {
            return commonWord(b, a);
        }
        return commonWord(a, b);
    }

    private static String commonWord(String a, String b) {
        for (int i = 1; i < b.length(); i++) {
            for (int j = 0; j <= b.length() - i; j++) {
                String begin = b.substring(0, j);
                String end = b.substring(j + i);
                if (a.startsWith(begin) && a.endsWith(end)) {
                    return expandWildToWholeWords(String.format("%s|%s", begin, end));
                }
            }
        }
        return null;
    }

    private static String expandWildToWholeWords(String common) {
        common = common.replaceAll("[^ ]*\\|[^ ]*", "|");
        return common.equals("|") ? null : common;
    }

    public static String difference(String common, String different) {
        if (common == null) {
            return different;
        }
        String[] split = common.split("\\|");
        if (split.length == 1) {
            return different.substring(split[0].length());
        }
        return different.substring(
                split[0].length(), different.length() - split[1].length());
    }
}
