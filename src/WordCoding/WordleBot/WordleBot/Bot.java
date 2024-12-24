package WordCoding.WordleBot.WordleBot;

import WordCoding.WordleBot.Wordle.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

//Average guesses: 3.7790697
public class Bot {
    public static final int COMBINATION_COUNT = 243;
    public static final double INFORMATION_FACTOR = (1 / Math.log(2));
    public static final int WRONG = 0;
    public static final int WRONG_PLACE = 1;
    public static final int CORRECT = 2;
    public static final String FREQUENCY_PATH = "src/WordCoding/WordleBot/WordleBot/wordFrequencies.csv";
    public static final double FREQUENCY_WIDTH = 30;
    private static final double CUTOFF_PERCENTAGE = 0.23153507756425098402407964806668;

    final List<String> originalWords;
    final List<String> possibleWords;
    final Set<String> possibleWordsSet;

    final Set<Character>[] possibilities;
    final Map<Character, Integer> mustHave;
    final List<Character> charList = new ArrayList<>();

    final Map<String, Map<Character, Integer>> counts = new HashMap<>();
    final Map<String, Map<Integer, List<String>>> results = new HashMap<>();

    final Map<String, Double> probabilities = new HashMap<>();

    public Bot(final List<String> possibleWords) {
        this.possibleWords = possibleWords;
        possibleWordsSet = new HashSet<>(possibleWords);
        originalWords = new ArrayList<>(possibleWords);
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        for (char c : chars) {
            charList.add(c);
        }

        setupCounts(possibleWords, chars);
        setupResults(possibleWords);

        mustHave = new HashMap<>();

        possibilities = setupPossibilities();

        setupProbabilities();
    }

    private void setupProbabilities() {
        List<String> lines = loadFrequencies();

        List<Map.Entry<String, Double>> wordProbabilities = generateProbabilities(lines);

        rescaleProbabilities(wordProbabilities);
    }

    private void rescaleProbabilities(final List<Map.Entry<String, Double>> wordProbabilities) {
        final double count = wordProbabilities.size();
        final double cutoff = wordProbabilities.size() * CUTOFF_PERCENTAGE;

        for (Map.Entry<String, Double> pair : wordProbabilities) {
            final double value = sigmoid(pair.getValue(), FREQUENCY_WIDTH, count, cutoff);
            probabilities.put(pair.getKey(), value);
        }
    }

    private List<Map.Entry<String, Double>> generateProbabilities(final List<String> lines) {
        List<Map.Entry<String, Double>> wordProbabilities = new ArrayList<>(lines.size());
        for (String line : lines) {
            String[] parts = line.split(", ");
            wordProbabilities.add(Map.entry(parts[0], Double.parseDouble(parts[1])));
        }
        wordProbabilities.sort(Comparator.comparingDouble(Map.Entry::getValue));
        return wordProbabilities;
    }

    public double sigmoid(double value, double width, double count, double cutoff) {
        return 1 / (1 + Math.pow(Math.E, -((value - cutoff) * width / count)));
    }

    private List<String> loadFrequencies() {
        List<String> lines;
        try {
            lines = Files.readAllLines(Path.of(FREQUENCY_PATH));
        } catch (IOException e) {
            e.printStackTrace();
            lines = new ArrayList<>();
        }
        return lines;
    }

    private Set<Character>[] setupPossibilities() {
        final Set<Character>[] possibilities = new HashSet[5];
        for (int i = 0; i < possibilities.length; i++) {
            possibilities[i] = new HashSet<>(charList);
        }
        return possibilities;
    }

    private void setupResults(final List<String> possibleWords) {
        for (String word : possibleWords) {
            final HashMap<Integer, List<String>> value = new HashMap<>();
            results.put(word, value);
            for (String other : possibleWords) {
                int result = getResult(word, other, counts.get(other));
                if (!value.containsKey(result)) {
                    value.put(result, new ArrayList<>());
                }
                value.get(result).add(other);
            }
        }
    }

    private void setupCounts(final List<String> possibleWords, final char[] chars) {
        for (String word : possibleWords) {
            final HashMap<Character, Integer> curr = new HashMap<>();
            counts.put(word, curr);
            for (char c : chars) {
                curr.put(c, getCount(c, word));
            }
        }
    }

    private int getResult(final String guess, final String answer, final Map<Character, Integer> counts) {
        int result = 0;

        // Step 1: First pass - mark CORRECT matches
        for (int i = 0; i < guess.length(); i++) {
            char guessChar = guess.charAt(i);
            if (guessChar == answer.charAt(i)) {
                result = setValue(2, i, result);
            }
        }

        // Step 2: Second pass - mark WRONG_PLACE and WRONG
        for (int i = 0; i < guess.length(); i++) {
            if (getValue(i, result) != CORRECT) {
                char guessChar = guess.charAt(i);
                if (counts.getOrDefault(guessChar, 0) > 0) {
                    result = setValue(1, i, result);
                } else {
                    result = setValue(0, i, result);
                }
            }
        }

        return result;
    }

    private int setValue(final int value, final int index, final int result) {
        return (result & (~(3 << (index * 2)))) + (value << (index * 2));
    }

    private int getValue(final int i, final int result) {
        return (result >> (i * 2)) & 3;
    }

    public String guess() {
        if (possibleWords.size() <= 4) {
            String bestWord = null;
            double highestProbability = Double.NEGATIVE_INFINITY;
            for (String word : possibleWords) {
                final Double probability = probabilities.get(word);
                if (probability > highestProbability) {
                    highestProbability = probability;
                    bestWord = word;
                }
            }
            return bestWord;
        }

        final int size = originalWords.size();
        double bestScore = Double.NEGATIVE_INFINITY;
        int bestIndex = -1;
        List<String> buff = new ArrayList<>();

        double probabilitySum = 0;
        for (String word : possibleWords) {
            probabilitySum += probabilities.get(word);
        }

        for (int i = 0; i < size; i++) {
            //System.out.println(i);
            String guess = originalWords.get(i);
            double sum = 0;
            int combination = 0;
            for (int j = 0; j < COMBINATION_COUNT; j++) {
                final List<String> currPossibleWords = results.get(guess).get(combination);
                if (currPossibleWords != null) {
                    buff.clear();
                    buff.addAll(currPossibleWords);
                    buff.retainAll(possibleWordsSet);
                    if (buff.size() != 0) {
                        sum += getScore(buff, probabilitySum);
                    }
                }
                combination = nextPossibility(combination);
            }
            if (sum > bestScore) {
                bestScore = sum;
                bestIndex = i;
            }
        }

        return originalWords.get(bestIndex);
    }

    private int nextPossibility(int combination) {
        for (int i = 0; i < 5; i++) {
            final int value = getValue(i, combination);
            final int index = (value + 1) % 3;
            combination = setValue(index, i, combination);
            if (index != 0) {
                break;
            }
        }
        return combination;
    }

    private double getScore(final List<String> newPossibleWords, final double probabilitySum) {
        double probability = 0;
        for (String word : newPossibleWords) {
            probability += getProbability(word, probabilitySum);
        }
        return probability * getInformation(probability);
    }

    private double getInformation(final double probability) {
        return -Math.log(probability) * INFORMATION_FACTOR;
    }

    private double getProbability(final String word, final double probabilitySum) {
        return probabilities.get(word) / probabilitySum;
    }

    public void reset() {
        possibleWords.clear();
        possibleWords.addAll(originalWords);
        possibleWordsSet.addAll(possibleWords);

        for (final Set<Character> possibility : possibilities) {
            possibility.clear();
            possibility.addAll(charList);
        }

        mustHave.clear();
    }

    final Map<Character, Integer> currCounts = new HashMap<>();

    public void update(final String guess, final Result[] results) {
        int[] parsed = new int[5];
        for (int i = 0; i < results.length; i++) {
            parsed[i] = results[i].ordinal();
        }
        updateRequirements(guess, parsed, possibilities, mustHave);

        final Set<Character> characterSet = mustHave.keySet();

        outer:
        for (int i = possibleWords.size() - 1; i >= 0; i--) {
            final String word = possibleWords.get(i);
            final char[] chars = word.toCharArray();

            final Map<Character, Integer> countMap = counts.get(word);
            for (char c : characterSet) {
                if (countMap.get(c) < mustHave.get(c)) {
                    possibleWords.remove(i);
                    possibleWordsSet.remove(word);
                    continue outer;
                }
            }

            for (int j = 0; j < chars.length; j++) {
                if (!possibilities[j].contains(chars[j])) {
                    possibleWords.remove(i);
                    possibleWordsSet.remove(word);
                    continue outer;
                }
            }
        }
    }

    Map<Character, Integer> currMustHave = new HashMap<>();

    private void updateRequirements(final String guess, final int[] results, Set<Character>[] possibilities, Map<Character, Integer> mustHave) {
        currCounts.clear();
        currMustHave.clear();
        char[] charArray = guess.toCharArray();

        for (final char value : charArray) {
            currCounts.put(value, currCounts.containsKey(value) ? currCounts.get(value) + 1 : 1);
        }


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

    private int getCount(final char c, final String word) {
        int appearances = 0;
        for (char curr : word.toCharArray()) {
            if (curr == c) {
                appearances++;
            }
        }
        return appearances;
    }
}
