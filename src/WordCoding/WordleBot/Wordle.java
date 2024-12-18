package WordCoding.WordleBot;/*
 * Wordle.java
 *
 * An console-based implementation of a popular word-guessing game
 *
 * starter code: Computer Science 112 staff (cs112-staff@cs.bu.edu)
 *
 * completed by:
 */

import java.util.HashMap;
import java.util.Scanner;

import static WordCoding.WordleBot.Result.*;

public class Wordle {
    // the name of a file containing a collection of English words, one word per line
    public static final String WORD_FILE = "src/WordCoding/WordleBot/words.txt";

    /*
     * printWelcome - prints the message that greets the user at the beginning of the game
     */
    public static void printWelcome() {
        System.out.println();
        System.out.println("Welcome to Wordle!");
        System.out.println("The mystery word is a 5-letter English word.");
        System.out.println("You have 6 chances to guess it.");
        System.out.println();
    }

    /*
     * initWordList - creates the WordList object that will be used to select
     * the mystery work. Takes the array of strings passed into main(),
     * since that array may contain a random seed specified by the user
     * from the command line.
     */
    public static WordList initWordList(String[] args) {
        int seed = -1;
        if (args.length > 0) {
            seed = Integer.parseInt(args[0]);
        }

        return new WordList(WORD_FILE, seed);
    }

    /*
     * readGuess - reads a single guess from the user and returns it
     * inputs:
     *   guessNum - the number of the guess (1, 2, ..., 6) that is being read
     *   console - the Scanner object that will be used to get the user's inputs
     */
    public static String readGuess(int guessNum, Scanner console) {
        String guess;
        do {
            System.out.print("guess " + guessNum + ": ");
            guess = console.next();
        } while (!isValidGuess(guess));

        return guess.toLowerCase();
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
    public static boolean isValidGuess(String guess) {
        boolean result = false;
        if (guess.length() == 5 && isAlpha(guess)) {
            result = true;
        } else if (guess.length() != 5) {
            System.out.println("Your guess must be 5 letters long. ");
        } else if (!isAlpha(guess)) {
            System.out.println("Your guess must only contain letters of the alphabet. ");
        }
        return result;
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


    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);

        printWelcome();

        // Create the WordList object for the collection of possible words.
        WordList words = initWordList(args);

        // Choose one of the words as the mystery word.
        String mystery = words.getRandomWord();

        int i;
        for (i = 0; i < 6; i++) {
            final GuessResult result = processGuess(readGuess(i + 1, console), mystery);
            System.out.println(result.toString());
            if (result.isCorrect) {
                System.out.println("Congrats! You guessed it!");
                break;
            }
        }
        if (i == 6) {
            System.out.println("Sorry! Better luck next time!");
            System.out.println("The word was " + mystery + ".");
        }

        console.close();
    }
}