package WordCoding.WordleBot.WordleBot;

import WordCoding.WordleBot.Wordle.Game;
import WordCoding.WordleBot.Wordle.GuessResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Test {

    public static final int LOG_PAUSE = 2;
    public static final int THREAD_COUNT = 8;
    static final ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
    public static final String SOLUTIONS_PATH = "src/WordCoding/WordleBot/Wordle/solutions.txt";
    public static final String WORDS_PATH = "src/WordCoding/WordleBot/Wordle/words.txt";

    public static void main(String[] args) throws IOException {
        AtomicInteger guessCount = new AtomicInteger(0);
        AtomicInteger wordCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        final List<String> possibleWords = Files.readAllLines(Path.of(WORDS_PATH));
        for (int i = 0; i < THREAD_COUNT; i++) {
            pool.submit(() ->
                    {
                        try {
                            simulateGame(guessCount, wordCount, failCount, possibleWords);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            );
        }
    }

    private static void simulateGame(final AtomicInteger guessCount, final AtomicInteger wordCount, final AtomicInteger failCount, final List<String> possibleWords) throws IOException {
        int currGuessCount = 0;

        Game game = new Game(-1, SOLUTIONS_PATH, WORDS_PATH);
        Bot bot = new Bot(possibleWords);
        game.nextWord();
        System.out.println("Finished setup");
        System.out.println(game.getCurrWord());
        while (!game.hasFinished()) {
            //final long startTime = System.nanoTime();
            String guess = bot.guess();
            //System.out.println("Time: " + (System.nanoTime() - startTime) / 1_000_000F + "ms");
            //System.out.println("Guess " + currGuessCount + ": " + guess);
            GuessResult guessResult = game.guess(guess);
            //System.out.println(guessResult.toString());
            guessCount.incrementAndGet();
            currGuessCount++;
            if (guessResult.isCorrect()) {
                if (wordCount.incrementAndGet() % LOG_PAUSE == 0)
                    System.out.println("Word was " + guessResult.getGuess() + ";\tAverage guesses: " + (guessCount.get() / ((float) wordCount.get())) + ";\tWordCount: " + wordCount + ";\tFailed: " + failCount + "\n");
                game.nextWord();
                //System.out.println(game.getCurrWord());
                bot.reset();
                currGuessCount = 0;
                //scanner.nextLine();
            } else if (currGuessCount > 6) {
                bot.reset();
                currGuessCount = 0;
                game.nextWord();
                wordCount.incrementAndGet();
                failCount.incrementAndGet();
            } else {
                bot.update(guess, guessResult.getResults());
            }
        }
    }
}
