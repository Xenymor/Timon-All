package WordCoding.WordleBot.Wordle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static WordCoding.WordleBot.Wordle.Result.*;

public class Game {
    final WordList solutions;
    final Set<String> possibleWords;
    String currWord;
    int wordCount;

    public Game() throws IOException {
        solutions = initSolutionList(-1);
        possibleWords = getPossibilities(Wordle.WORDS_PATH);
    }

    public Game(int seed, String solutionPath, String wordsPath) throws IOException {
        solutions = new WordList(solutionPath, seed);
        possibleWords = getPossibilities(wordsPath);
    }

    public String getCurrWord() {
        return currWord;
    }

    public List<String> getSolutions() {
        return new ArrayList<>(solutions.getPossibleWords());
    }

    public List<String> getPossibleWords() {
        return new ArrayList<>(possibleWords);
    }

    /*
     * initSolutionList - creates the WordList object that will be used to select
     * the mystery work. Takes the array of strings passed into main(),
     * since that array may contain a random seed specified by the user
     * from the command line.
     */
    public static WordList initSolutionList(int seed) {

        return new WordList(Wordle.SOLUTIONS_PATH, seed);
    }

    public static boolean includes(String s, char c) {
        return s.contains(Character.toString(c));
    }

    public static boolean isAlpha(String s) {
        for (char c : s.toCharArray()) {
            if (!Character.isAlphabetic(c)) {
                return false;
            }
        }
        return true;
    }

    public static int numOccur(char c, String s) {
        int result = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c)
                result += 1;
        }
        return result;
    }

    /**
     * takes three parameters: a single char c followed by two
     * String objects s1 and s2 that you may assume have the same
     * length. The method should count and return the number of times
     * that c occurs in the same position in both s1 and s2.
     */
    public static int numInSamePosn(char c, String s1, String s2) {

        int result = 0;
        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) == c && c == s2.charAt(i)) {
                result += 1;
            }
        }
        return result;
    }

    /*
     * TASK 2: Implement this method
     *
     * isValidGuess -  takes an arbitrary string guess and returns true
     * if it is a valid guess for Wordle, and false otherwise
     */
    public static boolean isValidGuess(String guess, Set<String> words) {
        if (guess.length() == 5 && isAlpha(guess)) {
            if (words.contains(guess)) {
                return true;
            } else {
                System.out.println("Your guess must be a word. ");
            }
        } else if (guess.length() != 5) {
            System.out.println("Your guess must be 5 letters long. ");
        } else if (!isAlpha(guess)) {
            System.out.println("Your guess must only contain letters of the alphabet. ");
        }
        return false;
    }

    /**** ADD YOUR METHOD FOR TASKS 3 and 5 HERE. ****/
    public static GuessResult processGuess(String guess, String mystery) {
        GuessResult returnValue = new GuessResult(guess);
        HashMap<Character, Integer> mysteryCharCounts = new HashMap<>();

        // Count character occurrences in the mystery word
        for (char c : mystery.toCharArray()) {
            mysteryCharCounts.put(c, mysteryCharCounts.getOrDefault(c, 0) + 1);
        }

        int correctCount = 0;
        // Step 1: First pass - mark CORRECT matches
        for (int i = 0; i < guess.length(); i++) {
            char guessChar = guess.charAt(i);
            if (guessChar == mystery.charAt(i)) {
                returnValue.results[i] = CORRECT;
                mysteryCharCounts.put(guessChar, mysteryCharCounts.get(guessChar) - 1);
                correctCount++;
            }
        }

        // Step 2: Second pass - mark WRONG_PLACE and WRONG
        for (int i = 0; i < guess.length(); i++) {
            if (returnValue.results[i] != CORRECT) {
                char guessChar = guess.charAt(i);
                if (mysteryCharCounts.getOrDefault(guessChar, 0) > 0) {
                    returnValue.results[i] = WRONG_PLACE;
                    mysteryCharCounts.put(guessChar, mysteryCharCounts.get(guessChar) - 1);
                } else {
                    returnValue.results[i] = WRONG;
                }
            }
        }

        // Determine if the guess is correct
        returnValue.isCorrect = correctCount == 5;

        return returnValue;
    }

    public static Set<String> getPossibilities(final String path) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(path));
        return new HashSet<>(lines);
    }


    public void nextWord() {
        currWord = solutions.getRandomWord();
        wordCount++;
    }

    public boolean hasFinished() {
        return wordCount >= solutions.getWordCount();
    }

    public GuessResult guess(final String guess) {
        return processGuess(guess, currWord);
    }
}
