package WordCoding.WordsWithSameLetters;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class WordsWithSameLetters {
    public static void main(String[] args) throws IOException {
        //createPairFile();
        //parseGerman();
        getTopPairs();
    }

    private static void parseGerman() throws IOException {
        List<String> commons = Files.readAllLines(Path.of("src/WordCoding/WordsWithSameLetters/FrequenciesGerman.txt"));
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < commons.size(); i++) {
            String curr = commons.get(i).split("\t")[0];
            if (curr.length() <= 1) {
                commons.remove(i);
                i--;
                continue;
            } else {
                commons.set(i, curr);
            }
            stringBuilder.append(curr).append("\n");
        }
        final Path path = Path.of("src/WordCoding/WordsWithSameLetters/wiki-100k-ger.txt");
        try {
            Files.createFile(path);
        } catch (FileAlreadyExistsException ignored) {
        }
        Files.writeString(path, stringBuilder.toString(), StandardCharsets.ISO_8859_1);
    }

    private static void getTopPairs() throws IOException {
        List<String> pairs = Files.readAllLines(Path.of("src/WordCoding/WordsWithSameLetters/pairs.txt"), StandardCharsets.ISO_8859_1);
        List<String> linesEn = prepareCommons(Files.readAllLines(Path.of("src/WordCoding/WordFrequencies/FrequenciesEn.csv")));
        List<String> linesDe = prepareCommons(Files.readAllLines(Path.of("src/WordCoding/WordFrequencies/FrequenciesDe.csv"), StandardCharsets.ISO_8859_1));
        Map<String, Double> commonsEn = formatData(linesEn);
        Map<String, Double> commonsDe = formatData(linesDe);
        List<String> commonPairs = new ArrayList<>();
        for (String pair : pairs) {
            String word = pair.split(",")[0].toLowerCase();
            if (commonsEn.containsKey(word) && commonsDe.containsKey(word)) {
                commonPairs.add(word);
            }
        }
        commonPairs.sort((o1, o2) -> Double.compare(Math.min(commonsEn.get(o2), commonsDe.get(o2)), Math.min(commonsEn.get(o1), commonsDe.get(o1))));
        commonPairs = commonPairs.subList(0, 100);
        for (String word : commonPairs) {
            System.out.println(word);
        }
    }

    private static List<String> prepareCommons(final List<String> strings) {
        strings.removeFirst();
        strings.removeIf(entry -> Double.parseDouble(entry.split(",")[1]) < 3.840009830425166E-6);
        return strings;
    }

    private static Map<String, Double> formatData(final List<String> commonsEn) {
        Map<String, Double> result = new HashMap<>();
        for (String s : commonsEn) {
            final String[] strings = s.split(",");
            final String word = strings[0];
            final Double percentage = Double.parseDouble(strings[1]);
            result.put(word, percentage);
        }
        return result;
    }

    private static void fix() throws IOException {
        List<String> germanWords = Files.readAllLines(Path.of("src/WordCoding/WordsWithSameLetters/german.txt"), StandardCharsets.ISO_8859_1);
        final Path pairsPath = Path.of("src/WordCoding/WordsWithSameLetters/pairs.txt");
        List<String> pairs = Files.readAllLines(pairsPath, StandardCharsets.ISO_8859_1);
        StringBuilder stringBuilder = new StringBuilder();
        for (String curr : pairs) {
            final String[] split = curr.split(",");
            final String append = germanWords.get(Integer.parseInt(split[1]));
            stringBuilder.append(split[0]).append(",").append(append).append("\n");
        }
        Files.writeString(pairsPath, stringBuilder.toString(), StandardCharsets.ISO_8859_1);
    }

    private static void createPairFile() throws IOException {
        List<String> englishWords = Files.readAllLines(Path.of("src/WordCoding/WordsWithSameLetters/english.txt"));
        List<String> germanWords = Files.readAllLines(Path.of("src/WordCoding/WordsWithSameLetters/german.txt"), StandardCharsets.ISO_8859_1);
        System.out.println("Loaded: En:" + englishWords.size() + "; De:" + germanWords.size());
        List<Pair> pairs = WordsWithSameLetters.findWords(englishWords, germanWords);
        StringBuilder stringBuilder = new StringBuilder();
        for (Pair pair : pairs) {
            //System.out.println(englishWords.get(pair.i1) + " : " + germanWords.get(pair.i2));
            stringBuilder.append(englishWords.get(pair.i1)).append(",").append(germanWords.get(pair.i2)).append("\n");
        }
        final Path path = Path.of("src/WordCoding/WordsWithSameLetters/pairs.txt");
        try {
            Files.createFile(path);
        } catch (FileAlreadyExistsException ignored) {
        }
        Files.writeString(path, stringBuilder.toString(), StandardCharsets.ISO_8859_1);
    }

    private static List<Pair> findWords(final List<String> words1, final List<String> words2) {
        List<Pair> result = new ArrayList<>();
        /*List<String> words2Sorted = new ArrayList<>();
        for (String word : words2) {
            words2Sorted.add(sort(word));
        }*/
        final int size = words1.size();
        for (int j = 0; j < size; j++) {
            final String word1 = words1.get(j);
            if (j % 10_000 == 0) {
                System.out.println(j + "/" + size + " done");
            }
            for (int i = 0; i < words2.size(); i++) {
                if (word1.equalsIgnoreCase(words2.get(i))) {
                    result.add(new Pair(j, i));
                    //System.out.println(word1 + " : " + words2.get(i));
                }
            }
        }
        return result;
    }

    private static String sort(final String word) {
        final char[] arr = word.toCharArray();
        Arrays.sort(arr);
        return new String(arr);
    }

    private static class Pair {
        int i1;
        int i2;

        public Pair(final int i1, final int i2) {
            this.i1 = i1;
            this.i2 = i2;
        }

        public int getI1() {
            return i1;
        }

        public void setI1(final int i1) {
            this.i1 = i1;
        }

        public int getI2() {
            return i2;
        }

        public void setI2(final int i2) {
            this.i2 = i2;
        }
    }
}
