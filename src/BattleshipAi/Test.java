package BattleshipAi;

import BattleshipAi.Bots.*;
import StandardClasses.Vectors.Vector2I;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Test {
    private static final int WIDTH = 10;
    private static final int HEIGHT = 10;
    private static final int ZOOM = 100;
    private static final int GAME_COUNT = 1_000_000;
    private static final int[] SHIP_LENGTHS = new int[]{2, 3, 3, 4, 5};
    private static final int THREAD_COUNT = 10;

    static final AtomicInteger moveCounter = new AtomicInteger(0);
    static final AtomicInteger gameCounter = new AtomicInteger(0);
    static final Collection<Integer> gameLengths = Collections.synchronizedCollection(new ArrayList<>());

    public static void main(String[] args) {
        //showBot(new HeatMapBot4(WIDTH, HEIGHT, SHIP_LENGTHS));
        testBotMultiThreaded();
    }

    private static void testBotMultiThreaded() {
        for (int i = 0; i < THREAD_COUNT; i++) {
            new Thread(() -> testBot(new HeatMapBot4(WIDTH, HEIGHT, SHIP_LENGTHS))).start();
        }
    }

    private static void showBot(BattleshipBot bot) throws InterruptedException {
        BattleshipBoard board = new BattleshipBoard(WIDTH, HEIGHT, SHIP_LENGTHS);
        board.initializeRandomBoats();
        BoardUI boardUI = new BoardUI(board, ZOOM);
        boardUI.setSize(WIDTH * ZOOM, HEIGHT * ZOOM);
        boardUI.setUndecorated(true);
        boardUI.setVisible(true);
        bot.reset();
        int counter = 0;
        while (!board.isWon()) {
            Vector2I move = bot.getMove();
            bot.moveResult(move, board.attack(move));
            counter++;
            Thread.sleep(500);
        }
        System.out.println(counter + " moves");
    }

    private static void testBot(BattleshipBot bot) {
        while (gameCounter.get() < GAME_COUNT) {
            final BattleshipBoard board = new BattleshipBoard(WIDTH, HEIGHT, SHIP_LENGTHS);
            board.initializeRandomBoats();
            bot.reset();
            int counter = 0;
            while (!board.isWon()) {
                Vector2I move = bot.getMove();
                bot.moveResult(move, board.attack(move));
                counter++;
            }
            moveCounter.getAndAdd(counter);
            gameCounter.getAndAdd(1);
            gameLengths.add(counter);
            final int gameCount = gameCounter.get();
            if (gameCount % 10_000 == 0) {
                final int moveCount = moveCounter.get();
                printStanding(gameCount, moveCount);
            }
        }
        final int gameCount = gameCounter.get();
        final int moveCount = moveCounter.get();
        printStanding(gameCount, moveCount);
    }

    private static void printStanding(final int gameCount, final int moveCount) {
        synchronized (gameLengths) {
            final List<Integer> sorted = gameLengths.stream().sorted().toList();
            final Integer median = sorted.get(sorted.size() / 2);
            final double average = moveCount / (double) gameCount;
            System.out.println("Games: " + gameCount + "\tAvg MPG: " + average + "\tMed MPG: " + median);
        }
    }
}
