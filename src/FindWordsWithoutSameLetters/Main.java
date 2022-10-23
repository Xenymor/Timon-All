package FindWordsWithoutSameLetters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        new Main().run();
    }

    List<String> words = new ArrayList<>();

    private void run() throws IOException {
        words = Files.readAllLines(Path.of("src/FindWordsWithoutSameLetters/words.txt"));
        removeWordsWithDoubleLetters();
        System.out.println(words.size());
        Collections.sort(words);
        findCombinations(0, 5, new HashSet<>(), new ArrayList<>(), 0);
    }

    private void removeWordsWithDoubleLetters() {
        char[] word;
        HashSet<Character> used = new HashSet<>();
        for (int i = words.size() - 1; i >= 0; i--) {
            word = words.get(i).toCharArray();
            for (char c : word) {
                if (used.contains(c)) {
                    words.remove(i);
                    used.clear();
                    break;
                } else {
                    used.add(c);
                }
            }
            used.clear();
        }
    }

    private void findCombinations(int start, int wordsToFind, HashSet<Character> currentChars, List<Integer> current, int vocalsUsed) {
        if (wordsToFind <= 0) {
            printResults(current);
            return;
        }
        if (6 - vocalsUsed < wordsToFind) {
            return;
        }
        //System.out.println(start + ", " + wordsToFind);
        char[] word;
        for (int i = start; i < words.size(); i++) {
            word = words.get(i).toCharArray();
            if (!hasCommon(word, currentChars)) {
                addChars(currentChars, word);
                current.add(i);
                int vocalCount = getVocalCount(word);
                vocalsUsed += vocalCount;
                findCombinations(i + 1, wordsToFind - 1, currentChars, current, vocalsUsed);
                removeChars(currentChars, word);
                current.remove((Integer) i);
                vocalsUsed -= vocalCount;
            }
        }
    }

    private int getVocalCount(char[] word) {
        int count = 0;
        for (char c : word) {
            if (isVocal(c)) {
                count++;
            }
        }
        return count;
    }

    private boolean isVocal(char c) {
        return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' || c == 'y';
    }

    private void printResults(List<Integer> current) {
        for (Integer integer : current) {
            System.out.print(words.get(integer) + ", ");
        }
        System.out.print("\n");
    }

    private void removeChars(HashSet<Character> currentChars, char[] word) {
        for (char c : word) {
            currentChars.remove(c);
        }
    }

    private boolean hasCommon(char[] word, HashSet<Character> currentChars) {
        for (char c : word) {
            if (currentChars.contains(c)) {
                return true;
            }
        }
        return false;
    }

    private void addChars(HashSet<Character> currentChars, char[] chars) {
        for (char aChar : chars) {
            currentChars.add(aChar);
        }
    }
}
