package Minesweeper;

import StandardClasses.Vector2I;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.*;

public class Minesweeper {
    private static final int BLOCK_SIZE = 40;
    final static Vector2I DEFAULT_VECTOR = new Vector2I(-1, -1);
    private static final Color BACKGROUND_COLOR = new Color(56, 56, 56);
    private static final Color TEXT_COLOR = new Color(0, 206, 210);
    private static final Color UNDISCOVERED_COLOR = new Color(176, 176, 176);
    private static final Color MINE_COLOR = new Color(255, 0, 0);
    final Board board;
    final int width;
    final int height;
    int mineCount;
    volatile Vector2I clickPos = DEFAULT_VECTOR;

    public Minesweeper(final int width, final int height, final int mineCount) {
        board = new Board(width, height, mineCount);
        this.width = width;
        this.height = height;
        this.mineCount = mineCount;
    }

    public static void main(String[] args) {
        Minesweeper minesweeper = new Minesweeper(12, 12, 8);
        minesweeper.play();
        /*Scanner scanner = new Scanner(System.in);
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
        }*/
    }

    private void play() {
        JFrame myFrame = new MyFrame(board);
        myFrame.setUndecorated(true);
        myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        myFrame.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(final MouseEvent e) {
            }

            @Override
            public void mousePressed(final MouseEvent e) {
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                clickPos = new Vector2I(e.getX() - myFrame.getX(), e.getY() - myFrame.getY());
            }

            @Override
            public void mouseEntered(final MouseEvent e) {
            }

            @Override
            public void mouseExited(final MouseEvent e) {
            }
        });
        myFrame.setVisible(true);
        outer:
        while (true) {
            if (!clickPos.equals(DEFAULT_VECTOR)) {
                if (!isFinished()) {
                    final int x = clickPos.getX() / BLOCK_SIZE;
                    final int y = clickPos.getY() / BLOCK_SIZE;
                    if (board.noFieldsExplored() && (board.isMine(x, y) || board.getNeighbourCount(x, y) > 0)) {
                        for (int i = 0; i < 10; i++) {
                            try {
                                board.reset(x, y);
                                break;
                            } catch (Board.CouldNotCreateBoardException e) {
                                if (i == 9) {
                                    e.printStackTrace();
                                    mineCount--;
                                    continue outer;
                                }
                            }
                        }
                    }
                    makeMove(x, y);
                } else {
                    restart();
                }
                clickPos = DEFAULT_VECTOR;
            }
        }
    }

    private void restart() {
        if (isWon()) {
            mineCount++;
        }
        board.setMineCount(mineCount);
        board.reset();
    }

    public boolean isWon() {
        return board.allFieldsExplored() && !board.mineFound();
    }

    public void makeMove(final int x, final int y) {
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

    public void printBoard() {
        StringBuilder builder = new StringBuilder();
        for (int y = 0; y < board.height; y++) {
            for (int x = 0; x < board.width; x++) {
                if (board.isExplored(x, y)) {
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

    public boolean isFinished() {
        return board.allFieldsExplored() || board.mineFound();
    }

    private class MyFrame extends JFrame {
        Board board;
        BufferedImage buffer;

        public MyFrame(final Board board) {
            this.board = board;
            buffer = new BufferedImage(board.width * BLOCK_SIZE, board.height * BLOCK_SIZE, BufferedImage.TYPE_INT_RGB);
            setSize(board.width * BLOCK_SIZE, board.height * BLOCK_SIZE);
        }


        @Override
        public void paint(final Graphics g) {
            Graphics gfx = buffer.getGraphics();
            gfx.setColor(BACKGROUND_COLOR);
            gfx.fillRect(0, 0, getWidth(), getHeight());
            for (int x = 0; x < board.width; x++) {
                for (int y = 0; y < board.height; y++) {
                    if (board.isExplored(x, y)) {
                        if (!board.isMine(x, y)) {
                            if (board.getNeighbourCount(x, y) > 0) {
                                gfx.setColor(TEXT_COLOR);
                                gfx.drawString(Integer.toString(board.getNeighbourCount(x, y)),
                                        (int) (x * BLOCK_SIZE + BLOCK_SIZE * 0.4),
                                        (int) (y * BLOCK_SIZE + BLOCK_SIZE * 0.6));
                            }
                        } else {
                            gfx.setColor(MINE_COLOR);
                            gfx.fillOval(x * BLOCK_SIZE + 3, y * BLOCK_SIZE + 3, BLOCK_SIZE - 6, BLOCK_SIZE - 6);
                        }
                    } else {
                        gfx.setColor(UNDISCOVERED_COLOR);
                        gfx.fillRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                    }
                }
            }
            if (isFinished()) {
                gfx.setColor(Color.RED);
                if (isWon()) {
                    gfx.drawString("You won", getWidth() / 2, getHeight() / 2);
                } else {
                    gfx.drawString("Game Over", getWidth() / 2, getHeight() / 2);
                }
            }
            g.drawImage(buffer, 0, 0, null);
            try {
                Thread.sleep(20);
            } catch (InterruptedException ignored) {
            }
            repaint();
        }
    }
}
