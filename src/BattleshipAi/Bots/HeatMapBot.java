package BattleshipAi.Bots;

import StandardClasses.Vector2L;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//50.524007 MPG
public class HeatMapBot implements BattleshipBot {
    Boolean[][] board;
    final int WIDTH;
    final int HEIGHT;
    private final int[] SHIP_LENGTHS;

    public HeatMapBot(final int width, final int height, int... shipLengths) {
        board = new Boolean[width][height];
        WIDTH = width;
        HEIGHT = height;
        SHIP_LENGTHS = shipLengths;
    }

    @Override
    public Vector2L getMove() {
        int[][] heatMap = getHeatMap();
        int highest = Integer.MIN_VALUE;
        Vector2L highestPos = new Vector2L(0, 0);
        for (int x = 0; x < heatMap.length; x++) {
            for (int y = 0; y < heatMap[x].length; y++) {
                final int heat = heatMap[x][y];
                if (board[x][y] == null && heat > highest) {
                    highest = heat;
                    highestPos.setX(x);
                    highestPos.setY(y);
                }
            }
        }
        return highestPos;
    }

    private int[][] getHeatMap() {
        int[][] heatMap = new int[WIDTH][HEIGHT];
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                final Boolean value = board[x][y];
                Vector2L[] adjacentPositions = getAdjacentPositions(x, y);
                boolean isKnown = false;
                boolean isHorizontal = true;
                if (value != null && value) {
                    for (final Vector2L adjacentPosition : adjacentPositions) {
                        final Boolean isShip = board[(int) adjacentPosition.getX()][(int) adjacentPosition.getY()];
                        if (isShip != null && isShip) {
                            isKnown = true;
                            if (adjacentPosition.getY() != y)
                                isHorizontal = false;
                            break;
                        }
                    }
                    increaseAdjacentHeatMap(heatMap, x, y, isHorizontal);
                    if (!isKnown) {
                        increaseAdjacentHeatMap(heatMap, x, y, false);
                    }
                }
                int[][] toAdd = new int[WIDTH][HEIGHT];
                for (int shipLength : SHIP_LENGTHS) {
                    do {
                        for (final int[] ints : toAdd) {
                            Arrays.fill(ints, 0);
                        }
                        if (!isPossiblePlacement(x, y, isHorizontal, shipLength, toAdd)) {
                            isHorizontal = !isHorizontal;
                            if (isKnown) {
                                break;
                            }
                            continue;
                        }
                        increaseHeatMap(heatMap, toAdd);
                        isHorizontal = !isHorizontal;
                        if (isKnown) {
                            break;
                        }
                    } while (!isHorizontal);
                }
            }
        }
        return heatMap;
    }

    private boolean isPossiblePlacement(final int x, final int y, final boolean isHorizontal, final int shipLength, final int[][] toAdd) {
        for (int delta = 0; delta < shipLength; delta++) {
            int currentX = x + (isHorizontal ? delta : 0);
            int currentY = y + (isHorizontal ? 0 : delta);
            if ((currentX >= 0 && currentY >= 0 && currentX < WIDTH && currentY < HEIGHT) && (board[currentX][currentY] == null || board[currentX][currentY])) {
                toAdd[currentX][currentY]++;
            } else {
                return false;
            }
        }
        return true;
    }

    private void increaseHeatMap(final int[][] heatMap, final int[][] toAdd) {
        for (int localX = 0; localX < toAdd.length; localX++) {
            for (int localY = 0; localY < toAdd[localX].length; localY++) {
                heatMap[localX][localY] += toAdd[localX][localY];
            }
        }
    }

    private void increaseAdjacentHeatMap(final int[][] heatMap, final int x, final int y, final boolean isHorizontal) {
        Vector2L[] adjacentPositions;
        adjacentPositions = getAdjacentPositions(x, y, isHorizontal);
        for (final Vector2L adjacentPosition : adjacentPositions) {
            final int x1 = (int) adjacentPosition.getX();
            final int y1 = (int) adjacentPosition.getY();
            final Boolean isShip = board[x1][y1];
            if (isShip == null || isShip) {
                heatMap[x1][y1] += 100;
            }
        }
    }

    private Vector2L[] getAdjacentPositions(final int x, final int y, final boolean isHorizontal) {
        List<Vector2L> result = new ArrayList<>();
        for (int delta = -1; delta < 2; delta++) {
            final int currX = x + (isHorizontal ? delta : 0);
            final int currY = y + (isHorizontal ? 0 : delta);
            if (currX >= 0 && currX < WIDTH && currY >= 0 && currY < WIDTH) {
                result.add(new Vector2L(currX, currY));
            }
        }
        return result.toArray(new Vector2L[0]);
    }

    private Vector2L[] getAdjacentPositions(final int x, final int y) {
        List<Vector2L> result = new ArrayList<>();
        for (int dX = -1; dX < 2; dX++) {
            for (int dY = -1; dY < 2; dY++) {
                if (Math.abs(dX) + Math.abs(dY) == 2 || dX == dY)
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
}