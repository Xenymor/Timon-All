package BattleshipAi.Bots;

import StandardClasses.Vector2I;

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
    public Vector2I getMove() {
        Vector2I result;
        while (true) {
            result = new Vector2I((int) (Math.random() * WIDTH), (int) (Math.random() * HEIGHT));
            if (board[result.getX()][result.getY()] == null) {
                return result;
            }
        }
    }

    @Override
    public void moveResult(final Vector2I pos, final boolean attack) {
        board[pos.getX()][pos.getY()] = attack;
    }

    @Override
    public void reset() {
        for (final Boolean[] booleans : board) {
            Arrays.fill(booleans, null);
        }
    }
}