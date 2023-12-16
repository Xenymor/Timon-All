package BattleshipAi;

import StandardClasses.Vector2I;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BattleshipBoard {
    private final int WIDTH;
    private final int HEIGHT;
    Field[][] board;
    private final int[] SHIP_LENGTHS;

    public BattleshipBoard(int width, int height, final int[] shipLengths) {
        WIDTH = width;
        HEIGHT = height;
        board = new Field[width][height];
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                board[x][y] = new Field();
            }
        }
        SHIP_LENGTHS = shipLengths;
    }

    public void initializeRandomBoats() {
        Vector2I[] shipPositions = getShipPositions();
        for (Vector2I pos : shipPositions) {
            board[pos.getX()][pos.getY()].setShip(true);
        }
    }

    public boolean attack(Vector2I pos) {
        final int x = pos.getX();
        final int y = pos.getY();
        board[x][y].setHit(true);
        return board[x][y].isShip();
    }

    public void undoMove(Vector2I pos) {
        board[pos.getX()][pos.getY()].setHit(false);
    }

    public boolean isWon() {
        for (final Field[] fields : board) {
            for (final Field field : fields) {
                if (field.isShip() && !field.isHit())
                    return false;
            }
        }
        return true;
    }


    public Vector2I[] getShipPositions() {
        List<List<Vector2I>> shipPositions = new ArrayList<>();
        Set<Vector2I> occupiedPositions = new HashSet<>();
        List<Vector2I> currentShipPositions = new ArrayList<>();

        int[] counterArr = new int[SHIP_LENGTHS.length];
        for (int j = 0; j < SHIP_LENGTHS.length; j++) {
            final int shipLength = SHIP_LENGTHS[j];
            boolean vertical = Math.random() < 0.5;
            while (true) {
                Vector2I startingPosition = getRandomPosition();

                boolean validPlacement = true;

                for (int i = 0; i < shipLength; i++) {
                    Vector2I currentPosition = getCurrentPosition(vertical, startingPosition, i);

                    validPlacement = isValidPlacement(occupiedPositions, currentPosition);

                    if (!validPlacement) {
                        break;
                    }

                    currentShipPositions.add(currentPosition);
                }

                if (validPlacement) {
                    shipPositions.add(currentShipPositions);
                    occupiedPositions.addAll(currentShipPositions);
                    currentShipPositions = new ArrayList<>();
                    break;
                } else {
                    currentShipPositions.forEach(occupiedPositions::remove);
                    currentShipPositions = new ArrayList<>();
                }
                counterArr[j]++;
                if (counterArr[j] > 100_000) {
                    counterArr[j - 1]++;
                    final int index = shipPositions.size() - 1;
                    if (index >= 0) {
                        shipPositions.get(index).forEach(occupiedPositions::remove);
                        shipPositions.remove(index);
                    }
                }
            }
        }
        List<Vector2I> result = new ArrayList<>();
        shipPositions.forEach(result::addAll);
        return result.toArray(new Vector2I[0]);
    }

    private boolean isValidPlacement(final Set<Vector2I> occupiedPositions, final Vector2I currentPosition) {
        if (currentPosition.getX() < 0 || currentPosition.getX() >= WIDTH ||
                currentPosition.getY() < 0 || currentPosition.getY() >= HEIGHT ||
                occupiedPositions.contains(currentPosition)) {
            return false;
        }

        for (Vector2I shipPosition : occupiedPositions) {
            if (currentPosition.getDist(shipPosition) <= 1.1) {
                return false;
            }
        }
        return true;
    }

    private Vector2I getCurrentPosition(final boolean vertical, final Vector2I startingPosition, final int i) {
        Vector2I currentPosition;
        if (vertical) {
            currentPosition = new Vector2I(startingPosition.getX(), startingPosition.getY() + i);
        } else {
            currentPosition = new Vector2I(startingPosition.getX() + i, startingPosition.getY());
        }
        return currentPosition;
    }

    private Vector2I getRandomPosition() {
        int randomX = (int) (Math.random() * WIDTH);
        int randomY = (int) (Math.random() * HEIGHT);

        return new Vector2I(randomX, randomY);
    }
}
