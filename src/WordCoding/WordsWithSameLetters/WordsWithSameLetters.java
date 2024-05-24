package WordCoding.WordsWithSameLetters;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WordsWithSameLetters {
    public static void main(String[] args) throws IOException {
        //CreatePairFile();
        //ParseGerman();
        GetTopPairs();
    }

    private static void ParseGerman() throws IOException {
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

    private static void GetTopPairs() throws IOException {
        List<String> pairs = Files.readAllLines(Path.of("src/WordCoding/WordsWithSameLetters/pairs.txt"), StandardCharsets.ISO_8859_1);
        List<String> commons = Files.readAllLines(Path.of("src/WordCoding/WordsWithSameLetters/wiki-100k.txt"));
        List<String> commonsGer = Files.readAllLines(Path.of("src/WordCoding/WordsWithSameLetters/wiki-100k-ger.txt"), StandardCharsets.ISO_8859_1);
        commons = commons.subList(0, 1_250);
        commonsGer = commonsGer.subList(0, 1_250);
        for (int i = 0; i < commons.size(); i++) {
            final String curr = commons.get(i);
            commons.set(i, curr.toLowerCase());
        }
        for (int i = 0; i < commonsGer.size(); i++) {
            final String curr = commonsGer.get(i);
            commonsGer.set(i, curr.toLowerCase());
        }
        for (String pair : pairs) {
            String word = pair.split(",")[0].toLowerCase();
            if (word.length() >= 3 && commons.contains(word) && commonsGer.contains(word)) {
                System.out.println(pair);
            }
        }
    }

    private static void Fix() throws IOException {
        List<String> germanWords = Files.readAllLines(Path.of("src/WordCoding/WordsWithSameLetters/german.txt"), StandardCharsets.ISO_8859_1);
        List<String> pairs = Files.readAllLines(Path.of("src/WordCoding/WordsWithSameLetters/pairs.txt"), StandardCharsets.ISO_8859_1);
        StringBuilder stringBuilder = new StringBuilder();
        for (String curr : pairs) {
            final String[] split = curr.split(",");
            final String append = germanWords.get(Integer.parseInt(split[1]));
            stringBuilder.append(split[0]).append(",").append(append).append("\n");
        }
        final Path path = Path.of("src/WordCoding/WordsWithSameLetters/pairs.txt");
        Files.writeString(path, stringBuilder.toString(), StandardCharsets.ISO_8859_1);
    }

    private static void CreatePairFile() throws IOException {
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
