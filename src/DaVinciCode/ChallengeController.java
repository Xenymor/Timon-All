package DaVinciCode;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ChallengeController {

    static final Scanner userInput = new Scanner(System.in);

    public static void main(String[] args) throws IOException, InterruptedException {

        if (args.length == 2) {
            System.out.println(playGame(args[0], args[1], false) ? "Player 1 won" : "Player 2 won");
        } else if (args.length == 3) {
            runChallenge(args[0], args[1], Integer.parseInt(args[2]));
        } else {
            throw new IllegalArgumentException("Wrong number of parameters " + Arrays.toString(args));
        }
    }

    private static void runChallenge(final String startCommand1, final String startCommand2, final int gameCount) throws InterruptedException {
        ExecutorService threads = Executors.newFixedThreadPool(8);

        AtomicInteger winCount1 = new AtomicInteger(0);
        AtomicInteger winCount2 = new AtomicInteger(0);
        CountDownLatch finishLatch = new CountDownLatch(gameCount);
        for (int i = 0; i < gameCount; i++) {
            final int finalI = i;
            threads.submit(() -> {
                try {
                    if (finalI % 2 == 0) {
                        try {
                            boolean result = playGame(startCommand1, startCommand2, true);
                            if (result) {
                                winCount1.incrementAndGet();
                            } else {
                                winCount2.incrementAndGet();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            boolean result = playGame(startCommand2, startCommand1, true);
                            if (result) {
                                winCount2.incrementAndGet();
                            } else {
                                winCount1.incrementAndGet();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                } finally {
                    finishLatch.countDown();
                }
            });
        }
        int count = 9;
        long latchCount;
        do {
            latchCount = finishLatch.getCount();
            if (latchCount <= count / 10.0 * gameCount) {
                final int count1 = winCount1.get();
                final int count2 = winCount2.get();
                System.out.println("GameCount: " + (count1 + count2) + "\tPlayer1: " + count1 + ";\tPlayer2: " + count2 + ";\tRatio: " + count1 * 1.0 / count2);
                count--;
            }
        } while (!finishLatch.await(500, TimeUnit.MILLISECONDS));
        final int count1 = winCount1.get();
        final int count2 = winCount2.get();
        System.out.println("GameCount: " + (count1 + count2) + "\tPlayer1: " + count1 + ";\tPlayer2: " + count2 + ";\tRatio: " + count1 * 1.0 / count2);
        threads.shutdown();
    }

    private static boolean playGame(final String process1StartCommand, final String process2StartCommand, final boolean hideOutputs) throws IOException {

        Game game = new Game(12);
        Process process1 = Runtime.getRuntime().exec(new String[]{process1StartCommand});
        Player player1 = new Player(process1, hideOutputs);
        Process process2 = Runtime.getRuntime().exec(new String[]{process2StartCommand});
        Player player2 = new Player(process2, hideOutputs);

        prepareGame(game, player1, player2);

        boolean switchedPlayer = true;
        while (!game.isFinished()) {
            Player current = getCurrentPlayer(game, player1, player2);
            Player opponent = getCurrentPlayer(game, player2, player1);

            if (switchedPlayer) {
                switchedPlayer = false;
                if (game.canDraw()) {
                    drawCard(game, current, opponent);
                } else {
                    game.couldDraw = false;
                }
            }
            boolean correct = guess(game, current, opponent);
            if (correct) {
                if (current.shouldPass()) {
                    game.pass();
                    switchedPlayer = true;
                }
            } else {
                game.pass();
                switchedPlayer = true;
            }
        }

        process1.destroy();
        process2.destroy();

        return game.getWinner();
    }

    private static boolean guess(final Game game, final Player current, final Player opponent) {
        final Move guess = current.guess();
        opponent.enemyGuess(guess);
        boolean correct = game.guess(guess);
        current.guessResult(correct);
        if (!correct && game.couldDraw) {
            opponent.revealCard(game.lastDrawn);
        }
        return correct;
    }

    private static Player getCurrentPlayer(final Game game, final Player player1, final Player player2) {
        return game.playerToMove ? player1 : player2;
    }

    private static void prepareGame(final Game game, final Player player1, final Player player2) {
        for (int i = 0; i < 4; i++) {
            drawCard(game, player1, player2);
            game.pass();

            drawCard(game, player2, player1);
            game.pass();
        }
    }

    private static void drawCard(final Game game, final Player currentPlayer, final Player opponent) {
        Card drawn = game.draw(currentPlayer.shouldDrawWhite());
        currentPlayer.drawn(drawn);
        opponent.enemyDrawn(drawn);
    }
}