package GameOfLife;

public class Board {
    public int height;
    public int width;
    boolean[][] board;
    private final boolean[][] change;
    private final int[] columnCount;
    private final int[] chunkCount = new int[4096];

    public Board(final int width, final int height) {
        this.width = width;
        this.height = height;
        board = new boolean[width][height];
        change = new boolean[width][height];
        columnCount = new int[width];
    }

    public Board(final boolean[][] board) {
        width = board.length;
        height = board[0].length;
        this.board = new boolean[width][height];
        change = board;
        columnCount = new int[width];
        update();
    }

    public boolean getState(final int x, final int y) {
        return board[x][y];
    }

    public void setState(final int x, final int y, final boolean b) {
        if (getState(x, y) != b) {
            change[x][y] = true;
        }
    }

    private int getChunkId(int x, int y) {
        return (x + y) >>> 6;
    }

    private int getChunkIdSafe(int x, int y) {
        int xx = x & 63;
        int yy = y & 63;
        return xx == 0 || yy == 0 || xx == 63 || yy == 63 ? -1 : (x + y) >>> 6;
    }

    public void update() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (change[x][y]) {
                    final boolean newState = !getState(x, y);
                    if (newState) {
                        columnCount[x]++;
                        chunkCount[getChunkId(x, y)]++;
                    } else {
                        columnCount[x]--;
                        chunkCount[getChunkId(x, y)]--;
                    }
                    board[x][y] = newState;
                    change[x][y] = false;
                }
            }
        }
    }

    public int getNeighbourCount(final int x, final int y) {
        int chunkIdSafe = getChunkIdSafe(x, y);
        if (chunkIdSafe >= 0 && chunkCount[chunkIdSafe] == 0) {
            return 0;
        }
        int count = 0;
        for (int dx = -1; dx < 2; dx++) {
            int x1 = x + dx;
            if (x1 < 0) {
                x1 += width;
            } else if (x1 >= width) {
                x1 -= width;
            }
            if (columnCount[x1] > 0) {
                for (int dy = -1; dy < 2; dy++) {
                    if (!(dx == 0 && dy == 0)) {
                        int y1 = y + dy;
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
        }
        return count;
    }
}
