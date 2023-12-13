package BattleshipAi.Bots;

import StandardClasses.Vector2L;

import java.util.Arrays;

//63.700794 MPG
public class RandomBot implements BattleshipBot {
    Boolean[][] board;
    final int WIDTH;
    final int HEIGHT;

    public RandomBot(final int width, final int height, int... shipLengths) {
        board = new Boolean[width][height];
        WIDTH = width;
        HEIGHT = height;
    }

    @Override
    public Vector2L getMove() {
        Vector2L result;
        while (true) {
            result = new Vector2L((int) (Math.random() * WIDTH), (int) (Math.random() * HEIGHT));
            if (board[(int) result.getX()][(int) result.getY()] == null) {
                return result;
            }
        }
    }

    @Override
    public void moveResult(final Vector2L pos, final boolean attack) {
        board[(int) pos.getX()][(int) pos.getY()] = attack;
    }

    @Override
    public void reset() {
        for (final Boolean[] booleans : board) {
            Arrays.fill(booleans, null);
        }
    }
}