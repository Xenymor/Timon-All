package NeuralNetworkProjects.LanguageClassification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeldomnessChecker {

    private static final int THRESHOLD = 5000;
    private final Map<Integer, MI> counts = new HashMap<>();

    public SeldomnessChecker(List<String> input) {
        for (String l : input) {
            add(l);
        }
    }

    private void add(final String l) {
        for (int i = 1; i < l.length(); i++) {
            add(
                    l.charAt(i-1),
                    l.charAt(i)
            );
        }
    }

    public boolean isSeldom(String s) {
        Integer key = getKey(s); 
        final MI count = counts.get(key);
        return count == null || count.v < THRESHOLD;
    }

    private void add(final char charAt, final char charAt1) {
        final int key = getKey(charAt, charAt1);
        counts.computeIfAbsent(key, k -> new MI()).v++;
    }

    private int getKey(String s) {
        return getKey(s.charAt(0), s.charAt(1));
    }

    private int getKey(final char charAt, final char charAt1) {
        return (Character.toLowerCase(charAt) << 16) | Character.toLowerCase(charAt1);
    }

    private static class MI {
        int v;
    }
}
