package GameOfLife;

public class Board {
    public int height;
    public int width;
    boolean[][] board;
    private boolean[][] change;

    private final Object lock = new Object();

    public Board(final int width, final int height) {
        this.width = width;
        this.height = height;
        board = new boolean[width][height];
        change = new boolean[width][height];
    }

    public Board(final boolean[][] board) {
        width = board.length;
        height = board[0].length;
        this.board = board;
        change = new boolean[width][height];
    }

    public boolean getState(final int x, final int y) {
        return board[x][y];
    }

    public void setState(final int x, final int y, final boolean b) {
        change[x][y] = b;
    }

    public void executeWithLock(final Runnable runnable) {
            runnable.run();
    }

    public void update() {
        synchronized (lock) {
            final boolean[][] temp = board;
            board = change;
            change = temp;
        }
    }

    public int getNeighbourCount(final int x, final int y) {
        int count = 0;
        for (int dx = -1; dx < 2; dx++) {
            for (int dy = -1; dy < 2; dy++) {
                if (!(dx == 0 && dy == 0)) {
                    int x1 = x + dx;
                    int y1 = y + dy;
                    if (x1 < 0) {
                        x1 += width;
                    } else if (x1 >= width) {
                        x1 -= width;
                    }
                    if (y1 < 0) {
                        y1 += height;
                    } else if (y1 >= height) {
                        y1 -= height;
                    }
                    if (getState(x1, y1)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
