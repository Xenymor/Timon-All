package Minesweeper;

import StandardClasses.Vector2I;

import java.util.*;

public class Minesweeper {
    Board board;

    public Minesweeper(final int width, final int height, final int mineCount) {
        board = new Board(width, height, mineCount);
    }

    public static void main(String[] args) {
        Minesweeper minesweeper = new Minesweeper(12, 12, 20);
        Scanner scanner = new Scanner(System.in);
        while (!minesweeper.isFinished()) {
            minesweeper.printBoard();
            System.out.println("What do you want to play?");
            int[] coords = Arrays.stream(scanner.nextLine().split(",")).mapToInt(((Integer::parseInt))).toArray();
            if (coords.length >= 2)
                minesweeper.makeMove(coords[0], coords[1]);
            else
                System.out.println("Not a valid move");
        }
        if (minesweeper.isWon()) {
            System.out.println("You won");
        } else {
            System.out.println("You lost");
        }
    }

    private boolean isWon() {
        return board.allFieldsExplored() && !board.mineFound();
    }

    private void makeMove(final int x, final int y) {
        Set<Vector2I> toExplore = exploreRecursively(x, y, new HashSet<>());
        for (Vector2I field : toExplore) {
            board.explore(field.getX(), field.getY());
        }
    }

    private Set<Vector2I> exploreRecursively(final int x, final int y, final Set<Vector2I> toExpand) {
        Vector2I toAdd = new Vector2I(x, y);
        if (toExpand.contains(toAdd))
            return toExpand;
        toExpand.add(toAdd);
        if (board.getNeighbourCount(x, y) > 0) {
            return toExpand;
        }
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }
                int x1 = x + dx;
                int y1 = y + dy;
                if ((x1 >= 0 && x1 < board.width)
                        && (y1 >= 0 && y1 < board.height)) {
                    exploreRecursively(x1, y1, toExpand);
                }

            }
        }
        return toExpand;
    }

    private void printBoard() {
        StringBuilder builder = new StringBuilder();
        for (int y = 0; y < board.height; y++) {
            for (int x = 0; x < board.width; x++) {
                if (board.isDiscovered(x, y)) {
                    final int neighbourCount = board.getNeighbourCount(x, y);
                    if (neighbourCount == 0) {
                        builder.append(" ");
                    } else {
                        builder.append(neighbourCount);
                    }
                } else {
                    builder.append("-");
                }
            }
            builder.append("\n");
        }
        System.out.println(builder);
    }

    private boolean isFinished() {
        return board.allFieldsExplored() || board.mineFound();
    }
}
