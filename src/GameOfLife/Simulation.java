package GameOfLife;

import StandardClasses.MyArrays;

public class Simulation {
    private Board board;
    private boolean changed = true;

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
        changed = false;
        for (int y = 0; y < board.height; y++) {
            for (int x = 0; x < board.width; x++) {
                int neighbours = board.getNeighbourCount(x, y);
                if (neighbours < 2) {
                    if (board.getState(x, y)) {
                        changed = true;
                    }
                    board.setState(x, y, false);
                } else if (neighbours < 4) {
                    if (neighbours == 3) {
                        if (!board.getState(x, y)) {
                            changed = true;
                        }
                        board.setState(x, y, true);
                    }
                } else {
                    if (board.getState(x, y)) {
                        changed = true;
                    }
                    board.setState(x, y, false);
                }
            }
        }
        board.update();
    }

    public boolean changed() {
        return changed;
    }
}
