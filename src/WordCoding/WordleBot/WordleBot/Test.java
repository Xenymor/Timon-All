package WordCoding.WordleBot.WordleBot;

import WordCoding.WordleBot.Wordle.Game;
import WordCoding.WordleBot.Wordle.GuessResult;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Test {

    public static final int LOG_PAUSE = 2;
    public static final int THREAD_COUNT = 5;
    static final ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
    public static final String SOLUTIONS_PATH = "src/WordCoding/WordleBot/Wordle/solutions.txt";
    public static final String WORDS_PATH = "src/WordCoding/WordleBot/Wordle/words.txt";

    public static void main(String[] args) throws IOException {
        AtomicInteger guessCount = new AtomicInteger(0);
        AtomicInteger wordCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        final List<String> possibleWords = Files.readAllLines(Path.of(WORDS_PATH));
        AtomicInteger counter = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream("src/WordCoding/WordleBot/WordleBot/guesses.txt"));
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int finalI = i;
            pool.submit(() ->
                    {
                        try {
                            simulateGame(guessCount, wordCount, failCount, possibleWords, finalI, counter, latch, writer);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
            );
        }
        writer.flush();
        //finished.await();
		/*Files.writeString(Path.of("src/WordCoding/WordleBot/WordleBot/guesses.txt"), guesses.stream()
				.sorted(Entry.comparingByKey())
				.map(Entry::getValue)
				.collect(Collectors.joining("\n")));*/
    }

    private static void simulateGame(
            final AtomicInteger guessCount,
            final AtomicInteger wordCount,
            final AtomicInteger failCount,
            final List<String> possibleWords,
            final int threadIndex,
            final AtomicInteger counter,
            final CountDownLatch latch,
            final BufferedOutputStream writer
    ) throws IOException, InterruptedException {
        int currGuessCount = 0;
        int localWordCount = 0;

        Game game = new Game(0, SOLUTIONS_PATH, WORDS_PATH);
        while (counter.get() < threadIndex) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Bot bot = new Bot(possibleWords, true);
        List<String> words = Files.readAllLines(Path.of("src/WordCoding/WordleBot/Wordle/solutions.txt"));
        bot.setSolutions(words);
        int v = wordCount.getAndIncrement();
        while (localWordCount <= v) {
            game.nextWord();
            localWordCount++;
        }
        counter.incrementAndGet();
        latch.countDown();
        System.out.println("Finished setup");
        latch.await();
        System.out.println(game.getCurrWord());
        StringBuilder guessBuilder = new StringBuilder();
        while (!game.hasFinished()) {
            //final long startTime = System.nanoTime();
            String guess = bot.guess(false);
            //System.out.println("Time: " + (System.nanoTime() - startTime) / 1_000_000F + "ms");
            //System.out.println("Guess " + currGuessCount + ": " + guess);
            GuessResult guessResult = game.guess(guess);
            if (!guessResult.isCorrect()) {
                guessBuilder.append(guess).append(",");
            } else {
                guessBuilder.append(guess).append("\n");
                synchronized (writer) {
                    writer.write(guessBuilder.toString().getBytes(StandardCharsets.UTF_8));
                    if (wordCount.get() % 100 == 0) {
                        writer.flush();
                    }
                }
                guessBuilder.delete(0, guessBuilder.length());
            }
            //System.out.println(guessResult.toString());
            guessCount.incrementAndGet();
            currGuessCount++;
            if (guessResult.isCorrect()) {
                if (wordCount.get() % LOG_PAUSE == 0) {
                    System.out.println("Word was " + guessResult.getGuess() + ";\tAverage guesses: " + (guessCount.get() / ((float) wordCount.get())) + ";\tWordCount: " + wordCount + ";\tFailed: " + failCount);
                }
                bot.reset();
                currGuessCount = 0;
                if (wordCount.get() % 100 == 0) {
                    System.out.println("Saving data ...");
                    String[] data = bot.dataToString();
                    Files.write(Path.of("src/WordCoding/WordleBot/Wordle/data.txt"), List.of(data));
                    System.out.println("Data saved");
                }
                v = wordCount.getAndIncrement();
                while (localWordCount <= v) {
                    game.nextWord();
                    localWordCount++;
                }
                //System.out.println(game.getCurrWord());
                //scanner.nextLine();
            } else if (currGuessCount > 6) {
                bot.reset();
                currGuessCount = 0;
                game.nextWord();
                failCount.incrementAndGet();
            } else {
                bot.update(guess, guessResult.getResults());
            }
        }
        writer.close();
    }
}
