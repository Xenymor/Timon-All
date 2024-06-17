package Minesweeper;

import StandardClasses.Random;

public class Board {
    Field[][] board;
    int width;
    int height;
    int mineCount;

    public Board(final int width, final int height, final int mineCount) {
        board = new Field[width][height];
        this.width = width;
        this.height = height;
        initializeFields();
        placeMines(width, height, mineCount);
        this.mineCount = mineCount;
    }

    private void placeMines(final int width, final int height, final int mineCount) {
        for (int i = 0; i < mineCount; i++) {
            int x = Random.randomIntInRange(0, width - 1);
            int y = Random.randomIntInRange(0, height - 1);
            if (board[x][y].isMine()) {
                i--;
            } else {
                board[x][y].setMine(true);
            }
        }
    }


    private void placeMines(final int width, final int height, final int mineCount, final int x, final int y) throws CouldNotCreateBoardException {
        int counter = 0;
        for (int i = 0; i < mineCount; i++) {
            int x1 = Random.randomIntInRange(0, width - 1);
            int y1 = Random.randomIntInRange(0, height - 1);
            if (board[x1][y1].isMine() || areNeighbours(x, y, x1, y1)) {
                i--;
                counter++;
                if (counter >= 100) {
                    throw new CouldNotCreateBoardException();
                }
            } else {
                board[x1][y1].setMine(true);
            }
        }
    }

    private boolean areNeighbours(final int x, final int y, final int x1, final int y1) {
        return Math.abs(x - x1) <= 1 && Math.abs(y - y1) <= 1;
    }

    private void initializeFields() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = new Field(false);
            }
        }
    }

    public boolean isMine(final int x, final int y) {
        return board[x][y].isMine();
    }

    public void setMine(final int x, final int y, final boolean isMine) {
        board[x][y].setMine(isMine);
    }

    public void explore(final int x, final int y) {
        board[x][y].setExplored(true);
    }

    public boolean isExplored(final int x, final int y) {
        return board[x][y].isExplored();
    }

    public int getNeighbourCount(final int x, final int y) {
        int count = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (((dx == 0) && (dy == 0))) {
                    continue;
                }
                int x1 = x + dx;
                int y1 = y + dy;
                if ((x1 >= 0 && x1 < width)
                        && (y1 >= 0 && y1 < height)
                        && (board[x1][y1].isMine()))
                    count++;
            }
        }
        return count;
    }

    public boolean allFieldsExplored() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final Field field = board[x][y];
                if (!field.isMine() && !field.isExplored()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean mineFound() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final Field field = board[x][y];
                if (field.isMine() && field.isExplored()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setMineCount(final int mineCount) {
        this.mineCount = mineCount;
    }

    public void reset() {
        initializeFields();
        placeMines(width, height, mineCount);
    }

    public void reset(final int x, final int y) throws CouldNotCreateBoardException {
        initializeFields();
        placeMines(width, height, mineCount, x, y);
    }

    public boolean noFieldsExplored() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (board[x][y].isExplored()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void mark(final int x, final int y) {
        board[x][y].setMarked(true);
    }

    public boolean isMarked(final int x, final int y) {
        return board[x][y].isMarked();
    }

    public void toggleMarked(final int x, final int y) {
        final Field field = board[x][y];
        field.setMarked(!field.isMarked());
    }

    public class CouldNotCreateBoardException extends Throwable {
    }
}
