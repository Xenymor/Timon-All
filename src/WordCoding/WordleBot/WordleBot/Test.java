package WordCoding.WordleBot.WordleBot;

import WordCoding.WordleBot.Wordle.Game;
import WordCoding.WordleBot.Wordle.GuessResult;

import java.io.IOException;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) throws IOException {
        Game game = new Game(6969, "src/WordCoding/WordleBot/Wordle/solutions.txt", "src/WordCoding/WordleBot/Wordle/words.txt");
        Bot bot = new Bot(game.getPossibleWords(), game.getSolutions());
        Scanner scanner = new Scanner(System.in);
        int guessCount = 0;
        int wordCount = 0;

        game.nextWord();
        System.out.println(game.getCurrWord());
        while (!game.hasFinished()) {
            String guess = bot.guess();
            //System.out.println("Guess: " + guess);
            GuessResult guessResult = game.guess(guess);
            //System.out.println(guessResult.toString());
            guessCount++;
            if (guessResult.isCorrect()) {
                wordCount++;
                if (wordCount % 1061 == 0)
                    System.out.println("Word was " + guessResult.getGuess() + ";\tAverage guesses: " + (guessCount / ((float) wordCount)) + ";\tWordCount: " + wordCount + "\n");
                game.nextWord();
                //System.out.println(game.getCurrWord());
                bot.reset();
                //scanner.nextLine();
            } else {
                bot.update(guess, guessResult.getResults());
            }
        }
    }
}
