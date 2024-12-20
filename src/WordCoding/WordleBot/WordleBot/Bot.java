package WordCoding.WordleBot.WordleBot;

import WordCoding.WordleBot.Wordle.Result;

import java.util.*;

import static WordCoding.WordleBot.Wordle.Result.WRONG;

//Average guesses: 4.6728606
public class Bot {
    final List<String> originalWords;
    final List<String> possibleWords;
    final Map<Character, Integer> frequencies;

    final Set<Character>[] possibilities;
    final Map<Character, Integer> mustHave;
    final List<Character> charList = new ArrayList<>();

    public Bot(final List<String> possibleWords) {
        this.possibleWords = possibleWords;
        originalWords = new ArrayList<>(possibleWords);
        frequencies = new HashMap<>();

        for (String word : possibleWords) {
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
        for (int i = 0; i < possibleWords.size(); i++) {
            final String word = possibleWords.get(i);
            int score = getScore(word);
            if (score > bestScore) {
                bestScore = score;
                bestIndex = i;
            }
        }
        return possibleWords.get(bestIndex);
    }

    private int getScore(final String word) {
        int score = 0;
        final char[] chars = word.toCharArray();
        Arrays.sort(chars);
        for (int i = 0; i < chars.length; i++) {
            final char c = chars[i];
            if (i != 0 && c == chars[i - 1]) {
                continue;
            }
            score += frequencies.get(c);
        }
        return score;
    }

    public void reset() {
        possibleWords.clear();
        possibleWords.addAll(originalWords);

        for (final Set<Character> possibility : possibilities) {
            possibility.clear();
            possibility.addAll(charList);
        }

        mustHave.clear();
    }

    final Map<Character, Integer> currCounts = new HashMap<>();

    public void update(final String guess, final Result[] results) {
        updateRequirements(guess, results, possibilities, mustHave);

        updatePossibleWords(possibleWords);
    }

    private void updatePossibleWords(List<String> possibleWords) {
        outer:
        for (int i = possibleWords.size() - 1; i >= 0; i--) {
            final String word = possibleWords.get(i);
            final char[] chars = word.toCharArray();

            for (char c : mustHave.keySet()) {
                if (getCount(word, c) < mustHave.get(c)) {
                    possibleWords.remove(i);
                    continue outer;
                }
            }

            for (int j = 0; j < chars.length; j++) {
                char c = chars[j];
                if (!possibilities[j].contains(c)) {
                    possibleWords.remove(i);
                    continue outer;
                }
            }
        }
    }

    private void updateRequirements(final String guess, final Result[] results, Set<Character>[] possibilities, Map<Character, Integer> mustHave) {
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
                    if (currCounts.get(c) == 1) {
                        for (final Set<Character> possibility : possibilities) {
                            possibility.remove(c);
                        }
                    } else {
                        List<Integer> indices = getIndices(c, guess);
                        boolean allWrong = true;
                        for (Integer index : indices) {
                            if (results[index] != WRONG) {
                                allWrong = false;
                                break;
                            }
                        }
                        if (allWrong) {
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
    }

    private List<Integer> getIndices(final char c, final String guess) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < guess.length(); i++) {
            if (guess.charAt(i) == c) {
                result.add(i);
            }
        }
        return result;
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
