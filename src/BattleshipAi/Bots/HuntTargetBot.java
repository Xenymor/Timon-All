package BattleshipAi.Bots;

import StandardClasses.Vector2I;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//74.102823 MPG
public class HuntTargetBot implements BattleshipBot{
    Boolean[][] board;
    final int WIDTH;
    final int HEIGHT;

    public HuntTargetBot(final int width, final int height, int... shipLengths) {
        board = new Boolean[width][height];
        WIDTH = width;
        HEIGHT = height;
    }

    @Override
    public Vector2I getMove() {
        Vector2I result;
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                if (board[x][y] != null && board[x][y]) {
                    Vector2I[] adjacentPositions = getAdjacentPositions(x, y);
                    for (final Vector2I adjacentPosition : adjacentPositions) {
                        if (board[adjacentPosition.getX()][adjacentPosition.getY()] == null) {
                            return adjacentPosition;
                        }
                    }
                }
            }
        }
        while (true) {
            result = new Vector2I((int) (Math.random() * WIDTH), (int) (Math.random() * HEIGHT));
            if (board[result.getX()][result.getY()] == null) {
                return result;
            }
        }
    }

    private Vector2I[] getAdjacentPositions(final int x, final int y) {
        List<Vector2I> result = new ArrayList<>();
        for (int dX = -1; dX < 2; dX++) {
            for (int dY = -1; dY < 2; dY++) {
                if (Math.abs(dX) + Math.abs(dY) == 2)
                    continue;
                final int currX = x + dX;
                final int currY = y + dY;
                if (currX >= 0 && currX < WIDTH && currY >= 0 && currY < WIDTH) {
                    result.add(new Vector2I(currX, currY));
                }
            }
        }
        return result.toArray(new Vector2I[0]);
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
