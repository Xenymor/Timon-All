package WordCoding.WordleBot.WordleBot;

import StandardClasses.Vectors.Vector2;
import WordCoding.WordleBot.Wordle.Result;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

//Average guesses: 3.7319589;	WordCount: 194;	Failed: 0
public class Bot {
    public static final int COMBINATION_COUNT = 243;
    public static final double INFORMATION_FACTOR = (1 / Math.log(2));
    public static final int WRONG = 0;
    public static final int WRONG_PLACE = 1;
    public static final int CORRECT = 2;
    public static final String FREQUENCY_PATH = "src/WordCoding/WordleBot/WordleBot/wordFrequencies.csv";
    public static final double FREQUENCY_WIDTH = 30;
    private static final double CUTOFF_PERCENTAGE = 0.3;
    private static final String SAVE_PATH = "src/WordCoding/WordleBot/WordleBot/saveData.sav";

    final List<String> originalWords;
    final List<Integer> possibleWords;
    final BitSet possibleWordsSet;

    final Set<Character>[] possibilities;
    final Map<Character, Integer> mustHave;
    final List<Character> charList = new ArrayList<>();

    final List<Map<Character, Integer>> counts = new ArrayList<>();
    final List<Map<Integer, List<Integer>>> results;

    static List<Map<Integer, List<Integer>>> resultsCache;
    static long staticOriginalWordsHash;

    final Map<Integer, Double> probabilities = new HashMap<>();

    public static final List<Vector2> data = Collections.synchronizedList(new ArrayList<>());

    private final double[] currEntropies = new double[6];

    private List<String> solutions = new ArrayList<>();

    int guessCount = 0;

    boolean setSolutions = false;

    public Bot(final List<String> possibleWords) {
        this.originalWords = possibleWords;
        this.possibleWordsSet = new BitSet();
        this.possibleWords = new ArrayList<>();

        for (int i = 0; i < originalWords.size(); i++) {
            this.possibleWords.add(i);
            this.possibleWordsSet.set(i);
        }

        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        for (char c : chars) {
            charList.add(c);
        }

        setupCounts(possibleWords, chars);
        results = new ArrayList<>();

        if (staticOriginalWordsHash == originalWords.size()) {
            System.out.println("Using static cache ...");
            for (int i = 0; i < originalWords.size(); i++) {
                final Map<Integer, List<Integer>> map = new HashMap<>();
                final Map<Integer, List<Integer>> cached = resultsCache.get(i);
                for (Integer key : cached.keySet()) {
                    map.put(key, new ArrayList<>(cached.get(key)));
                }
                results.add(map);
            }
        } else {
            try {
                ObjectInputStream inputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(SAVE_PATH)));
                if (originalWords.hashCode() != inputStream.readInt()) {
                    throw new IOException("File changed");
                }
                System.out.println("Loading save file ...");

                loadResults(inputStream, results, originalWords.size());

                inputStream.close();
            } catch (IOException e) {
                System.out.println("Recalculating save file ...");
                setupResults(this.possibleWords);
                System.out.println("Saving data ...");
                saveResults();
            }
        }


        mustHave = new HashMap<>();

        possibilities = setupPossibilities();

        setupProbabilities();
    }

    public Bot(final List<String> possibleWords, boolean allowLoading) {
        this.originalWords = possibleWords;
        this.possibleWordsSet = new BitSet();
        this.possibleWords = new ArrayList<>();

        for (int i = 0; i < originalWords.size(); i++) {
            this.possibleWords.add(i);
            this.possibleWordsSet.set(i);
        }

        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        for (char c : chars) {
            charList.add(c);
        }

        setupCounts(possibleWords, chars);
        results = new ArrayList<>();

        if (staticOriginalWordsHash == originalWords.size()) {
            System.out.println("Using static cache ...");
            for (int i = 0; i < originalWords.size(); i++) {
                final Map<Integer, List<Integer>> map = new HashMap<>();
                final Map<Integer, List<Integer>> cached = resultsCache.get(i);
                for (Integer key : cached.keySet()) {
                    map.put(key, new ArrayList<>(cached.get(key)));
                }
                results.add(map);
            }
        } else {
            try {
                if (!allowLoading) {
                    throw new IOException("Loading not allowed");
                }
                ObjectInputStream inputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(SAVE_PATH)));
                if (originalWords.hashCode() != inputStream.readInt()) {
                    throw new IOException("File changed");
                }
                System.out.println("Loading save file ...");

                loadResults(inputStream, results, originalWords.size());

                inputStream.close();
            } catch (IOException e) {
                System.out.println("Recalculating save file ...");
                setupResults(this.possibleWords);
                if (allowLoading) {
                    System.out.println("Saving data ...");
                    saveResults();
                }
            }
        }

        mustHave = new HashMap<>();

        possibilities = setupPossibilities();

        setupProbabilities();
    }

    private static synchronized void loadResults(ObjectInputStream inputStream, List<Map<Integer, List<Integer>>> results, int wordCount) {
        resultsCache = new ArrayList<>();
        //TODO Improve hashcode
        staticOriginalWordsHash = wordCount;
        try {
            for (int i = 0; i < wordCount; i++) {
                final Map<Integer, List<Integer>> map = new HashMap<>();
                final Map<Integer, List<Integer>> cached = new HashMap<>();
                int buff = inputStream.readInt();
                while (buff != -1) {
                    final int key = buff;
                    final List<Integer> list = new ArrayList<>();
                    buff = inputStream.readInt();
                    while (buff != -1) {
                        list.add(buff);
                        buff = inputStream.readInt();
                    }
                    map.put(key, list);
                    cached.put(key, new ArrayList<>(list));
                    buff = inputStream.readInt();
                }
                results.add(map);
                resultsCache.add(cached);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveResults() {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(SAVE_PATH)));
            outputStream.writeInt(originalWords.hashCode());
            for (int i = 0; i < originalWords.size(); i++) {
                final Map<Integer, List<Integer>> map = results.get(i);
                for (Integer key : map.keySet()) {
                    outputStream.writeInt(key);
                    for (Integer content : map.get(key)) {
                        outputStream.writeInt(content);
                    }
                    outputStream.writeInt(-1);
                }
                outputStream.writeInt(-1);
            }
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupProbabilities() {
        List<String> lines = loadFrequencies();

        List<Map.Entry<Integer, Double>> wordProbabilities = generateProbabilities(lines);

        rescaleProbabilities(wordProbabilities);
    }

    private void rescaleProbabilities(final List<Map.Entry<Integer, Double>> wordProbabilities) {
        final double count = wordProbabilities.size();
        final double cutoff = wordProbabilities.size() * CUTOFF_PERCENTAGE;

        for (Map.Entry<Integer, Double> pair : wordProbabilities) {
            final double value = sigmoid(pair.getValue(), FREQUENCY_WIDTH, count, cutoff);
            probabilities.put(pair.getKey(), value);
        }
    }

    private List<Map.Entry<Integer, Double>> generateProbabilities(final List<String> lines) {
        List<Map.Entry<Integer, Double>> wordProbabilities = new ArrayList<>(lines.size());
        for (String line : lines) {
            String[] parts = line.split(", ");
            final String word = parts[0];
            final int index = originalWords.indexOf(word);
            if (index >= 0) {
                wordProbabilities.add(Map.entry(index, Double.parseDouble(parts[1])));
            }
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

    private void setupResults(final List<Integer> possibleWords) {
        for (Integer wordIndex : possibleWords) {
            final HashMap<Integer, List<Integer>> value = new HashMap<>();
            results.add(wordIndex, value);
            for (int i = 0; i < possibleWords.size(); i++) {
                final int otherIndex = possibleWords.get(i);
                int result = getResult(originalWords.get(wordIndex), originalWords.get(otherIndex), counts.get(otherIndex));
                if (!value.containsKey(result)) {
                    value.put(result, new ArrayList<>());
                }
                value.get(result).add(i);
            }
        }
    }

    private void setupCounts(final List<String> possibleWords, final char[] chars) {
        for (int i = 0; i < possibleWords.size(); i++) {
            final String word = possibleWords.get(i);
            final HashMap<Character, Integer> curr = new HashMap<>();
            counts.add(i, curr);
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
                result = setValue(CORRECT, i, result);
            }
        }

        // Step 2: Second pass - mark WRONG_PLACE and WRONG
        for (int i = 0; i < guess.length(); i++) {
            if (getValue(i, result) != CORRECT) {
                char guessChar = guess.charAt(i);
                if (counts.getOrDefault(guessChar, 0) > 0) {
                    result = setValue(WRONG_PLACE, i, result);
                } else {
                    result = setValue(WRONG, i, result);
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

    public String guess(final boolean verbose) {
        long time = System.nanoTime();

        guessCount++;
        //long start = System.nanoTime();

        if (possibleWords.size() <= 1) {
            return originalWords.get(possibleWords.getFirst());
        }

        /* TODO uncomment for better performance
            if (possibleWords.size() <= 4) {
            Integer bestWord = -1;
            double highestProbability = Double.NEGATIVE_INFINITY;
            for (Integer wordIndex : possibleWords) {
                final Double probability = probabilities.get(wordIndex);
                if (probability > highestProbability) {
                    highestProbability = probability;
                    bestWord = wordIndex;
                }
            }
            return originalWords.get(bestWord);
        }*/

        final int size = originalWords.size();
        double bestScore = Double.POSITIVE_INFINITY;
        int bestIndex = -1;
        List<Integer> buff = new ArrayList<>();

        double expectedEntropy = 0;

        double probabilitySum = 0;
        for (Integer wordIndex : possibleWords) {
            probabilitySum += probabilities.get(wordIndex);
        }
        double currEntropy = getEntropy(possibleWords, probabilitySum);
        currEntropies[guessCount - 1] = currEntropy;

        for (int i = 0; i < size; i++) {
            //System.out.println(i);
            double sum = 0;
            int combination = 0;
            final Map<Integer, List<Integer>> combinationMap = results.get(i);
            for (int j = 0; j < COMBINATION_COUNT; j++) {
                final List<Integer> currPossibleWords = combinationMap.get(combination);
                if (currPossibleWords != null) {
                    if (!setSolutions) {
                        buff.clear();
                        for (final var p : currPossibleWords) {
                            if (possibleWordsSet.get(p)) {
                                buff.add(p);
                            }
                        }
                        if (!buff.isEmpty()) {
                            sum += getScore(buff, probabilitySum);
                        }
                    } else {
                        int count = 0;
                        for (final var p : currPossibleWords) {
                            if (possibleWordsSet.get(p)) {
                                count++;
                            }
                        }
                        double p = (double) count / possibleWords.size();
                        sum += p * getInformation(p);
                    }
                }
                combination = nextPossibility(combination);
            }
            final double probability = possibleWordsSet.get(i) ? getProbability(i, probabilitySum) : 0;
            final double currExpectedEntropy = currEntropy - sum;
            double score = probability * guessCount + (1 - probability) * (guessCount + estimatedGuesses(currExpectedEntropy));
            if (score < bestScore) {
                bestScore = score;
                bestIndex = i;
                expectedEntropy = currExpectedEntropy;
            }
        }
        if (verbose) {
            //System.out.println("Calculation time: " + ((System.nanoTime() - start) / 1_000_000) + "ms");
            System.out.println("Expected guesses: " + bestScore + ", \tCurrent entropy: " + currEntropy + ", \tExpected entropy: " + expectedEntropy
                    + ", \tWord count: " + possibleWords.size() + ", \tGuess count: " + guessCount + ", \tTime: " + (System.nanoTime() - time) / 1_000_000_000F + "s");
        }
        return originalWords.get(bestIndex);
    }

    private double getEntropy(final List<Integer> possibleWords, final double probabilitySum) {
        double sum = 0;
        for (Integer wordIndex : possibleWords) {
            double probability = getProbability(wordIndex, probabilitySum);
            sum += probability * getInformation(probability);
        }
        return sum;
    }

    private double estimatedGuesses(final double entropy) {
        //y=−0.00781798508797881819x^2+0.30513580160862652235x+1.00957741161661118667
        return -0.00781798508797881819 * entropy * entropy + 0.30513580160862652235 * entropy + 1.00957741161661118667;

        //New version
        //y=−0.00686145502383406214x^2+0.29078274406154491771x+1.00847530362056225783
        //y=0.00197319808875073832x^3−0.05065885921394652769x^2+0.54741223930284377275x+0.67749990013237493258
        //final double v = entropy * entropy;
        //return 0.00197319808875073832 * v * entropy - 0.05065885921394652769 * v + 0.54741223930284377275 * entropy + 0.67749990013237493258;

        //y=1.10895344710314169845x0.45225664288777550626
        //return 1.10895344710314169845 * Math.pow(entropy, 0.45225664288777550626);
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

    private double getScore(final List<Integer> newPossibleWords, final double probabilitySum) {
        double probability = 0;
        for (int wordIndex : newPossibleWords) {
            probability += getProbability(wordIndex, probabilitySum);
        }
        return probability * getInformation(probability);
    }

    private double getInformation(final double probability) {
        final double v = -Math.log(probability) * INFORMATION_FACTOR;
        return Double.isNaN(v) || Double.isInfinite(v) ? 0 : v;
    }

    private double getProbability(final Integer wordIndex, final double probabilitySum) {
        return probabilities.get(wordIndex) / probabilitySum;
    }

    public void reset() {
        for (int i = 0; i < guessCount; i++) {
            final double entropy = currEntropies[i];
            data.add(new Vector2(entropy, guessCount - i));
        }

        guessCount = 0;

        possibleWords.clear();

        if (solutions == null || solutions.isEmpty()) {
            for (int i = 0; i < originalWords.size(); i++) {
                possibleWords.add(i);
                possibleWordsSet.set(i);
            }
        } else {
            setSolutions(solutions);
        }

        for (final Set<Character> possibility : possibilities) {
            possibility.clear();
            possibility.addAll(charList);
        }

        mustHave.clear();
    }


    public void update(final String guess, final Result[] results) {
        update(guess, results, possibleWords, possibleWordsSet, possibilities, mustHave);
    }

    private void update(final String guess, final Result[] results, final List<Integer> possibleWords, final BitSet possibleWordsSet, final Set<Character>[] possibilities, final Map<Character, Integer> mustHave) {
        int[] parsed = new int[5];
        for (int i = 0; i < results.length; i++) {
            parsed[i] = results[i].ordinal();
        }
        updateRequirements(guess, parsed, possibilities, mustHave);

        final Set<Character> characterSet = mustHave.keySet();

        outer:
        for (int i = possibleWords.size() - 1; i >= 0; i--) {
            final Integer wordIndex = possibleWords.get(i);
            final String word = originalWords.get(wordIndex);
            final char[] chars = word.toCharArray();

            final Map<Character, Integer> countMap = counts.get(wordIndex);
            for (char c : characterSet) {
                if (countMap.get(c) < mustHave.get(c)) {
                    possibleWords.remove(wordIndex);
                    possibleWordsSet.clear(wordIndex);
                    continue outer;
                }
            }

            for (int j = 0; j < chars.length; j++) {
                if (!possibilities[j].contains(chars[j])) {
                    possibleWords.remove(wordIndex);
                    possibleWordsSet.clear(wordIndex);
                    continue outer;
                }
            }
        }
    }

    final Map<Character, Integer> currCounts = new HashMap<>();
    final Map<Character, Integer> currMustHave = new HashMap<>();

    private void updateRequirements(final String guess, final int[] results, Set<Character>[] possibilities, final Map<Character, Integer> mustHave) {
        currCounts.clear();
        currMustHave.clear();
        char[] charArray = guess.toCharArray();

        // Count occurrences in guess
        for (final char value : charArray) {
            currCounts.put(value, currCounts.getOrDefault(value, 0) + 1);
        }

        // Count CORRECT and WRONG_PLACE for each letter
        Map<Character, Integer> correctCounts = new HashMap<>();
        Map<Character, Integer> wrongPlaceCounts = new HashMap<>();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (results[i] == CORRECT) {
                correctCounts.put(c, correctCounts.getOrDefault(c, 0) + 1);
            } else if (results[i] == WRONG_PLACE) {
                wrongPlaceCounts.put(c, wrongPlaceCounts.getOrDefault(c, 0) + 1);
            }
        }

        // First process CORRECT
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (results[i] == CORRECT) {
                possibilities[i].clear();
                possibilities[i].add(c);
                currMustHave.put(c, currMustHave.getOrDefault(c, 0) + 1);
            }
        }

        // Then process WRONG_PLACE
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (results[i] == WRONG_PLACE) {
                possibilities[i].remove(c);
                currMustHave.put(c, currMustHave.getOrDefault(c, 0) + 1);
            }
        }

        // Then process WRONG
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (results[i] == WRONG) {
                int mustHaveCount = correctCounts.getOrDefault(c, 0) + wrongPlaceCounts.getOrDefault(c, 0);
                int guessCount = currCounts.get(c);

                if (mustHaveCount == 0) {
                    // Letter does not appear in answer at all
                    for (final Set<Character> possibility : possibilities) {
                        possibility.remove(c);
                    }
                } else if (mustHaveCount < guessCount) {
                    // Remove from this position only
                    possibilities[i].remove(c);
                }
                // else: all occurrences are accounted for, do not remove from other positions
            }
        }

        // Update mustHave
        for (Character c : currMustHave.keySet()) {
            mustHave.put(c, Math.max(currMustHave.get(c), mustHave.getOrDefault(c, 0)));
        }
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

    public String[] dataToString() {
        StringBuilder entropies = new StringBuilder();
        StringBuilder guessesLeft = new StringBuilder();

        for (Vector2 dataPoint : data) {
            if (dataPoint.getX() == 0) {
                continue; // Skip zero entropies
            }
            entropies.append(dataPoint.getX()).append(" ");
            guessesLeft.append(dataPoint.getY()).append(" ");
        }
        return new String[]{entropies.toString(), guessesLeft.toString()};
    }

    void setSolutions(final List<String> words) {
        setSolutions = true;
        possibleWords.clear();
        possibleWordsSet.clear();

        for (String word : words) {
            int index = originalWords.indexOf(word);
            if (index < 0) {
                index = originalWords.size();
                originalWords.add(word);
            }
            possibleWords.add(index);
            possibleWordsSet.set(index);
        }

        this.solutions = words;
    }
}
