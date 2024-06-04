package Minesweeper;

import StandardClasses.Random;

public class Board {
    Field[][] board;
    int width;
    int height;

    public Board(final int width, final int height, final int mineCount) {
        board = new Field[width][height];
        this.width = width;
        this.height = height;
        initializeFields();
        placeMines(width, height, mineCount);
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
        board[x][y].setFound(true);
    }

    public boolean isDiscovered(final int x, final int y) {
        return board[x][y].isFound();
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
                if (!field.isMine() && !field.isFound()) {
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
                if (field.isMine() && field.isFound()) {
                    return true;
                }
            }
        }
        return false;
    }
}
