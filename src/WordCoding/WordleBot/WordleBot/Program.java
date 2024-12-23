package WordCoding.WordleBot.WordleBot;

import WordCoding.WordleBot.Wordle.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import static WordCoding.WordleBot.Wordle.Result.*;

public class Program {
    public static void main(String[] args) throws IOException {
        //TODO remove non 5 letter words;
        final List<String> possibleWords = Files.readAllLines(Path.of("src/WordCoding/WordleBot/Wordle/words.txt"));
        System.out.println("Setting up ...");
        Bot bot = new Bot(possibleWords);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Finished Setup");

        while (true) {
            String guess = bot.guess();
            System.out.println("Guess: " + guess);
            String answer = scanner.nextLine();
            final String[] split = answer.split(" ");
            if (split[0].equalsIgnoreCase("guess")) {
                guess = split[1];
                answer = scanner.nextLine();
            }
            Result[] results = parseAnswer(answer);
            if (isCorrect(results)) {
                bot.reset();
                System.out.println("Congratulations!");
            } else {
                bot.update(guess, results);
            }
        }
    }

    private static boolean isCorrect(final Result[] results) {
        for (Result result : results) {
            if (result != CORRECT) {
                return false;
            }
        }
        return true;
    }

    private static Result[] parseAnswer(final String answer) {
        char[] chars = answer.toCharArray();
        Result[] results = new Result[5];
        for (int i = 0; i < results.length; i++) {
            switch (chars[i]) {
                case 'w' -> {
                    results[i] = WRONG;
                }
                case 'p' -> {
                    results[i] = WRONG_PLACE;
                }
                case 'c' -> {
                    results[i] = CORRECT;
                }
            }
        }
        return results;
    }
}
