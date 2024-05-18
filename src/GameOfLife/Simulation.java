package GameOfLife;

public class Simulation {
    private Board board;

    public Simulation(final Board board) {
        this.board = board;
    }

    public void setBoard(final Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    public void printState() {
        for (int y = 0; y < board.height; y++) {
            for (int x = 0; x < board.width; x++) {
                System.out.print(board.getState(x, y) ? "0" : "-");
            }
            System.out.println();
        }
        System.out.println();
    }

    public void nextStep() {
        final long start = System.nanoTime();
        for (int y = 0; y < board.height; y++) {
            for (int x = 0; x < board.width; x++) {
                int neighbours = board.getNeighbourCount(x, y);
                if (neighbours < 2 || neighbours > 3) {
                    board.setState(x, y, false);
                } else if (neighbours == 3) {
                    board.setState(x, y, true);
                } else {
                    board.setState(x, y, board.getState(x, y));
                }
            }
        }
        board.update();
        System.out.println("Duration: " + (System.nanoTime() - start)/1_000_000F + "ms");
    }
}
