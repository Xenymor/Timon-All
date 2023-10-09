package CodeWars.SimplePigLatin;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SimplePigLatin {
    public static String pigIt(String str) {
        return Arrays.stream(str.split(" ")).map(SimplePigLatin::changeWord).collect(Collectors.joining(" "));
    }

    private static String changeWord(String a) {
        if (a.contains(".") || a.contains("!") || a.contains(",") || a.contains("?")) {
            return a;
        }
        char firstChar = a.charAt(0);
        a = a.substring(1);
        a = a + firstChar + "ay";
        return a;
    }
}
