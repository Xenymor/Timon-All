package WordCoding.WordFrequencies;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class FrequencyCounter {
    public static void main(String[] args) {
        countAndSaveFrequencies("src/WordCoding/WordFrequencies/TextsEn.txt",
                "src/WordCoding/WordFrequencies/FrequenciesEn.csv");
        countAndSaveFrequencies("src/WordCoding/WordFrequencies/TextsDe.txt",
                "src/WordCoding/WordFrequencies/FrequenciesDe.csv");
    }

    private static void countAndSaveFrequencies(final String pathToSrcFile, final String pathToSaveFile) {
        List<Map.Entry<String, Long>> sorted = countFrequencies(pathToSrcFile).toList();
        saveFrequencies(pathToSaveFile, sorted);
    }

    private static void saveFrequencies(final String pathToSaveFile, final List<Map.Entry<String, Long>> sorted) {
        final Path path = Path.of(pathToSaveFile);
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        StringBuilder builder = new StringBuilder();
        int sum = 0;
        for (var entry : sorted) {
            sum += entry.getValue();
        }
        builder.append("Word,Percentage\n");
        for (var entry : sorted) {
            builder.append(entry.getKey()).append(",").append((entry.getValue() / (double) sum)*100).append("%\n");
        }
        try {
            Files.writeString(path, builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Stream<Map.Entry<String, Long>> countFrequencies(final String pathToSrcFile) {
        Stream<Map.Entry<String, Long>> sorted = null;
        try {
            List<String> srcLines = Files.readAllLines(Path.of(pathToSrcFile));
            HashMap<String, Long> wordCount = new HashMap<>();
            for (String line : srcLines) {
                String[] words = line.split("[^a-zA-Z0-9äöüÄÖÜß]");
                for (String word : words) {
                    if (!word.isEmpty()) {
                        word = word.toLowerCase();
                        if (wordCount.containsKey(word)) {
                            wordCount.put(word, wordCount.get(word) + 1);
                        } else {
                            wordCount.put(word, 1L);
                        }
                    }
                }
            }
            sorted =
                    wordCount.entrySet().stream()
                            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sorted;
    }
}
