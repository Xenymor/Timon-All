package BattleshipAi.Bots;

import StandardClasses.Vectors.Vector2I;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//63.725366 MPG
public class HuntTargetSmartBot implements BattleshipBot {
    final Boolean[][] board;
    final int WIDTH;
    final int HEIGHT;

    public HuntTargetSmartBot(final int width, final int height, int... shipLengths) {
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
                    boolean known = false;
                    boolean isHorizontal = true;
                    for (final Vector2I adjacentPosition : adjacentPositions) {
                        final Boolean isShip = board[adjacentPosition.getX()][adjacentPosition.getY()];
                        if (isShip != null && isShip) {
                            known = true;
                            if (adjacentPosition.getX() != x)
                                isHorizontal = true;
                            else if (adjacentPosition.getY() != y)
                                isHorizontal = false;
                            else
                                throw new RuntimeException();
                        }
                    }
                    if (known)
                        adjacentPositions = getAdjacentPositions(x, y, isHorizontal);
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

    private Vector2I[] getAdjacentPositions(final int x, final int y, final boolean isHorizontal) {
        List<Vector2I> result = new ArrayList<>();
        for (int delta = -1; delta < 2; delta++) {
                final int currX = x + (isHorizontal ? delta : 0);
                final int currY = y + (isHorizontal ? 0 : delta);
                if (currX >= 0 && currX < WIDTH && currY >= 0 && currY < WIDTH) {
                    result.add(new Vector2I(currX, currY));
                }
        }
        return result.toArray(new Vector2I[0]);
    }

    private Vector2I[] getAdjacentPositions(final int x, final int y) {
        List<Vector2I> result = new ArrayList<>();
        for (int dX = -1; dX < 2; dX++) {
            for (int dY = -1; dY < 2; dY++) {
                if (Math.abs(dX) + Math.abs(dY) == 2 || dX == dY)
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
