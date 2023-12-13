package BattleshipAi;

import BattleshipAi.Bots.*;
import StandardClasses.Vector2L;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;

public class Test {
    private static final int WIDTH = 10;
    private static final int HEIGHT = 10;
    private static final int ZOOM = 100;
    private static final int GAME_COUNT = 1_000_000;
    private static final int[] SHIP_LENGTHS = new int[]{2, 3, 3, 4, 5};
    private static final int THREAD_COUNT = 10;

    static AtomicInteger moveCounter = new AtomicInteger(0);
    static AtomicInteger gameCounter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        //showBot(new HeatMapBot(WIDTH, HEIGHT, SHIP_LENGTHS));
        for (int i = 0; i < THREAD_COUNT; i++) {
            new Thread(() -> {
                try {
                    testBot(new HeatMapBot(WIDTH, HEIGHT, SHIP_LENGTHS));
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private static void showBot(BattleshipBot bot) throws InterruptedException {
        final BattleshipBoard board = new BattleshipBoard(WIDTH, HEIGHT, SHIP_LENGTHS);
        board.initializeRandomBoats();
        BoardUI boardUI = new BoardUI(board, ZOOM);
        boardUI.setSize(WIDTH * ZOOM, HEIGHT * ZOOM);
        boardUI.setUndecorated(true);
        boardUI.setVisible(true);
        bot.reset();
        while (!board.isWon()) {
            Vector2L move = bot.getMove();
            bot.moveResult(move, board.attack(move));
            Thread.sleep(500);
        }
    }

    private static void testBot(BattleshipBot bot) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        while (gameCounter.get() < GAME_COUNT) {
            final BattleshipBoard board = new BattleshipBoard(WIDTH, HEIGHT, SHIP_LENGTHS);
            board.initializeRandomBoats();
            bot.reset();
            while (!board.isWon()) {
                Vector2L move = bot.getMove();
                bot.moveResult(move, board.attack(move));
                moveCounter.getAndAdd(1);
                //Thread.sleep(500);
            }
            gameCounter.getAndAdd(1);
            if (gameCounter.get() % 10_000 == 0) {
                System.out.println("Games: " + gameCounter + "\tMoves/Game: " + moveCounter.get() / (double) gameCounter.get());
            }
        }
        System.out.println("Games: " + gameCounter + "\tMoves/Game: " + moveCounter.get() / (double) gameCounter.get());
    }
}
