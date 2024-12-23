package WordCoding.WordleBot.Wordle;/*
 * Wordle.java
 *
 * An console-based implementation of a popular word-guessing game
 *
 * starter code: Computer Science 112 staff (cs112-staff@cs.bu.edu)
 *
 * completed by:
 */

import java.io.IOException;
import java.util.*;

public class Wordle {
    // the name of a file containing a collection of English words, one word per line
    public static final String SOLUTIONS_PATH = "src/WordCoding/WordleBot/Wordle/solutions.txt";
    public static final String WORDS_PATH = "src/WordCoding/WordleBot/Wordle/words.txt";

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
     * readGuess - reads a single guess from the user and returns it
     * inputs:
     *   guessNum - the number of the guess (1, 2, ..., 6) that is being read
     *   console - the Scanner object that will be used to get the user's inputs
     */
    public static String readGuess(int guessNum, Scanner console, Set<String> words) {
        String guess;
        do {
            System.out.print("guess " + guessNum + ": ");
            guess = console.next();
        } while (!Game.isValidGuess(guess, words));

        return guess.toLowerCase();
    }


    public static void main(String[] args) throws IOException {
        Scanner console = new Scanner(System.in);

        printWelcome();

        // Create the WordList object for the collection of possible solutions.
        WordList solutions = Game.initSolutionList(args.length > 0 ? Integer.parseInt(args[0]) : -1);
        Set<String> possibleWords = Game.getPossibilities(WORDS_PATH);

        while (true) {
            // Choose one of the solutions as the mystery word.
            String mystery = solutions.getRandomWord();

            int i;
            for (i = 0; i < 6; i++) {
                final GuessResult result = Game.processGuess(readGuess(i + 1, console, possibleWords), mystery);
                System.out.println(result);
                if (result.isCorrect) {
                    System.out.println("Congrats! You guessed it!");
                    break;
                }
            }
            if (i == 6) {
                System.out.println("Sorry! Better luck next time!");
                System.out.println("The word was " + mystery + ".");
            }
        }
    }

}