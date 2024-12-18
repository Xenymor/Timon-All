package WordCoding.WordleBot.WordleBot;

import WordCoding.WordleBot.Wordle.Result;

import java.util.*;

//Average guesses: 5.735223
public class Bot {
    final List<String> originalWords;
    final List<String> originalSolutions;
    final List<String> possibleWords;
    final List<String> possibleSolutions;
    final Map<Character, Integer> frequencies;

    final Set<Character>[] possibilities;
    final Map<Character, Integer> mustHave;
    final List<Character> charList = new ArrayList<>();

    public Bot(final List<String> possibleWords, final List<String> possibleSolutions) {
        this.possibleWords = possibleWords;
        this.possibleSolutions = possibleSolutions;
        originalWords = new ArrayList<>(possibleWords);
        originalSolutions = new ArrayList<>(possibleSolutions);
        frequencies = new HashMap<>();

        for (String word : possibleSolutions) {
            for (char c : word.toCharArray()) {
                frequencies.put(c, frequencies.getOrDefault(c, 0) + 1);
            }
        }

        mustHave = new HashMap<>();

        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        for (char c : chars) {
            charList.add(c);
        }

        possibilities = new HashSet[5];
        for (int i = 0; i < possibilities.length; i++) {
            possibilities[i] = new HashSet<>(charList);
        }
    }

    public String guess() {
        int bestScore = Integer.MIN_VALUE;
        int bestIndex = -1;
        for (int i = 0; i < possibleSolutions.size(); i++) {
            final String word = possibleSolutions.get(i);
            int score = getScore(word);
            if (score > bestScore) {
                bestScore = score;
                bestIndex = i;
            }
        }
        return possibleSolutions.get(bestIndex);
    }

    private int getScore(final String word) {
        int score = 0;
        final char[] chars = word.toCharArray();
        Arrays.sort(chars);
        for (int i = 0; i < chars.length; i++) {
            final char c = chars[i];
            if (i != 0 && c == chars[i-1]) {
                continue;
            }
            score += frequencies.get(c);
        }
        return score;
    }

    public void reset() {
        possibleWords.clear();
        possibleWords.addAll(originalWords);
        possibleSolutions.clear();
        possibleSolutions.addAll(originalSolutions);

        for (final Set<Character> possibility : possibilities) {
            possibility.clear();
            possibility.addAll(charList);
        }

        mustHave.clear();
    }

    final Map<Character, Integer> currCounts = new HashMap<>();

    public void update(final String guess, final Result[] results) {
        currCounts.clear();
        char[] charArray = guess.toCharArray();

        for (final char value : charArray) {
            currCounts.put(value, currCounts.containsKey(value) ? currCounts.get(value) + 1 : 1);
        }

        Map<Character, Integer> currMustHave = new HashMap<>();

        for (int i = 0; i < charArray.length; i++) {
            final char c = charArray[i];
            switch (results[i]) {
                case CORRECT -> {
                    possibilities[i].clear();
                    possibilities[i].add(c);

                    currMustHave.put(c, currMustHave.containsKey(c) ? currMustHave.get(c) + 1 : 1);
                }
                case WRONG -> {
                    //TODO if two wrong chars remove the char
                    if (currCounts.get(c) == 1) {
                        for (final Set<Character> possibility : possibilities) {
                            possibility.remove(c);
                        }
                    } else {
                        int foundCount = 0;
                        boolean[] isFound = new boolean[5];
                        for (int j = 0; j < possibilities.length; j++) {
                            final Set<Character> possibility = possibilities[j];
                            if (possibility.size() == 1 && possibility.contains(c)) {
                                foundCount++;
                                isFound[j] = true;
                            } else {
                                isFound[j] = false;
                            }
                        }
                        if (foundCount == currCounts.get(c) - 1) {
                            for (int j = 0; j < possibilities.length; j++) {
                                if (!isFound[j]) {
                                    possibilities[j].remove(c);
                                }
                            }
                        } else {
                            possibilities[i].remove(c);
                        }
                    }
                }
                case WRONG_PLACE -> {
                    possibilities[i].remove(c);
                    currMustHave.put(c, currMustHave.containsKey(c) ? currMustHave.get(c) + 1 : 1);
                }
            }
        }

        for (Character c : currMustHave.keySet()) {
            mustHave.put(c, Math.max(currMustHave.get(c), mustHave.getOrDefault(c, 0)));
        }

        outer:
        for (int i = possibleSolutions.size() - 1; i >= 0; i--) {
            final String word = possibleSolutions.get(i);
            final char[] chars = word.toCharArray();

            for (char c : mustHave.keySet()) {
                if (getCount(word, c) < mustHave.get(c)) {
                    possibleSolutions.remove(i);
                    continue outer;
                }
            }

            for (int j = 0; j < chars.length; j++) {
                char c = chars[j];
                if (!possibilities[j].contains(c)) {
                    possibleSolutions.remove(i);
                    continue outer;
                }
            }
        }
    }

    private int getCount(final String word, final char c) {
        int appearances = 0;
        for (char curr : word.toCharArray()) {
            if (curr == c) {
                appearances++;
            }
        }
        return appearances;
    }
}
