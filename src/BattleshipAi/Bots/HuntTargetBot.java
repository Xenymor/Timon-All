package BattleshipAi.Bots;

import StandardClasses.Vector2L;

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
    public Vector2L getMove() {
        Vector2L result;
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                if (board[x][y] != null && board[x][y]) {
                    Vector2L[] adjacentPositions = getAdjacentPositions(x, y);
                    for (final Vector2L adjacentPosition : adjacentPositions) {
                        if (board[(int) adjacentPosition.getX()][(int) adjacentPosition.getY()] == null) {
                            return adjacentPosition;
                        }
                    }
                }
            }
        }
        while (true) {
            result = new Vector2L((int) (Math.random() * WIDTH), (int) (Math.random() * HEIGHT));
            if (board[(int) result.getX()][(int) result.getY()] == null) {
                return result;
            }
        }
    }

    private Vector2L[] getAdjacentPositions(final int x, final int y) {
        List<Vector2L> result = new ArrayList<>();
        for (int dX = -1; dX < 2; dX++) {
            for (int dY = -1; dY < 2; dY++) {
                if (Math.abs(dX) + Math.abs(dY) == 2)
                    continue;
                final int currX = x + dX;
                final int currY = y + dY;
                if (currX >= 0 && currX < WIDTH && currY >= 0 && currY < WIDTH) {
                    result.add(new Vector2L(currX, currY));
                }
            }
        }
        return result.toArray(new Vector2L[0]);
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
