package BattleshipAi;

import BattleshipAi.Bots.BattleshipBot;
import BattleshipAi.Bots.HeatMapBot3;
import StandardClasses.Vector2I;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class VSHuman {
    private static final int WIDTH = 10;
    private static final int HEIGHT = 10;
    private static final int[] SHIP_LENGTHS = new int[]{2, 3, 3, 4, 5};
    private static final int ZOOM = 100;
    public static final int WAIT_BETWEEN_MOVES = 500;

    public static void main(String[] args) throws InterruptedException {
        new VSHuman().startMatch();
    }

    private final BattleshipBoard botBoard = new BattleshipBoard(WIDTH, HEIGHT, SHIP_LENGTHS);
    private final BattleshipBoard humanBoard = new BattleshipBoard(WIDTH, HEIGHT, SHIP_LENGTHS);
    private final BattleshipBot bot = new HeatMapBot3(WIDTH, HEIGHT, SHIP_LENGTHS);
    private boolean botToMove = false;
    private boolean editMode = true;
    private final BoardUI boardUI = new BoardUI(botBoard, ZOOM);
    private Vector2I clicked = null;
    final Lock clickedLock = new ReentrantLock();
    private boolean confirmed = false;

    private void startMatch() throws InterruptedException {
        remainingShipLengths.clear();
        for (int SHIP_LENGTH : SHIP_LENGTHS) {
            remainingShipLengths.add(SHIP_LENGTH);
        }
        boardUI.setSize(WIDTH * ZOOM, HEIGHT * ZOOM);
        boardUI.setUndecorated(true);
        boardUI.setVisible(true);
        boardUI.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                final int button = e.getButton();
                if (button == 1) {
                    clickedLock.lock();
                    clicked = new Vector2I((e.getX() - boardUI.getX()) / ZOOM, (e.getY() - boardUI.getY()) / ZOOM);
                    System.out.println("Clicked " + clicked);
                    clickedLock.unlock();
                }
                if (button == 3 && editMode) {
                    confirmed = true;
                }
            }

            @Override
            public void mousePressed(final MouseEvent e) {
            }

            @Override
            public void mouseReleased(final MouseEvent e) {

            }

            @Override
            public void mouseEntered(final MouseEvent e) {

            }

            @Override
            public void mouseExited(final MouseEvent e) {

            }
        });
        humanBoard.initializeRandomBoats();
        while (true) {
            if (editMode) {
                clickedLock.lock();
                if (clicked != null) {
                    final int x = clicked.getX();
                    final int y = clicked.getY();
                    System.out.println("ReceivedClick X:" + x + "; Y:" + y);
                    final Field field = botBoard.board[x][y];
                    final boolean isShip = field.isShip();
                    if (isShip) {
                        field.setShip(false);
                    } else {
                        if (isAllowedPlacement(x, y)) {
                            field.setShip(true);
                        }
                    }
                    clicked = null;
                }
                clickedLock.unlock();
                if (confirmed) {
                    confirmed = false;
                    if (checkShipLengths()) {
                        editMode = false;
                        System.out.println("Valid Placement");
                    } else {
                        System.out.println("Placement not allowed");
                    }
                }
            } else {
                if (botToMove) {
                    boardUI.board = botBoard;
                    boardUI.drawUnattackedShips = true;
                    Thread.sleep(WAIT_BETWEEN_MOVES);
                    Vector2I move = bot.getMove();
                    bot.moveResult(move, botBoard.attack(move));
                    Thread.sleep(WAIT_BETWEEN_MOVES);
                    botToMove = !botToMove;
                    if (botBoard.isWon()) {
                        break;
                    }
                } else {
                    boardUI.board = humanBoard;
                    boardUI.drawUnattackedShips = false;
                    if (clicked != null) {
                        humanBoard.attack(clicked);
                        botToMove = !botToMove;
                        clicked = null;
                        Thread.sleep(WAIT_BETWEEN_MOVES);
                        if (humanBoard.isWon()) {
                            break;
                        }
                    }
                }
            }
        }
        if (humanBoard.isWon()) {
            System.out.println("Humanity may survive");
        } else {
            System.out.println("We're all going to die");
        }
    }

    final List<Integer> remainingShipLengths = new ArrayList<>();
    final HashSet<Field> checkedPositions = new HashSet<>();

    private boolean checkShipLengths() {
        remainingShipLengths.clear();
        Arrays.stream(SHIP_LENGTHS).forEach(remainingShipLengths::add);

        final Field[][] board = botBoard.board;
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                final Field field = board[x][y];
                if (field.isShip() && !checkedPositions.contains(field)) {
                    int shipLength = getShipLength(x, y);
                    if (remainingShipLengths.contains(shipLength)) {
                        remainingShipLengths.remove((Integer) shipLength);
                    } else {
                        return false;
                    }
                }
            }
        }

        return remainingShipLengths.size() <= 0;
    }

    private int getShipLength(final int x, final int y) {
        Vector2I[] adjacentPositions = getAdjacentPositions(x, y);
        final Field[][] board = botBoard.board;
        boolean isHorizontal = false;
        boolean isKnown = false;
        for (final Vector2I pos : adjacentPositions) {
            if (board[pos.getX()][pos.getY()].isShip()) {
                isHorizontal = pos.getX() != x;
                isKnown = true;
                break;
            }
        }
        if (isKnown) {
            return getShipLength(x, y, isHorizontal);
        } else {
            checkedPositions.add(board[x][y]);
            return 1;
        }
    }

    private int getShipLength(final int x, final int y, final boolean isHorizontal) {
        int multiplier = 1;
        int length = 1;
        final Field[][] board = botBoard.board;
        do {
            int delta = multiplier;
            while (true) {
                int currX = x + (isHorizontal ? delta : 0);
                int currY = y + (isHorizontal ? 0 : delta);
                if (currX < 0 || currX >= WIDTH || currY < 0 || currY >= HEIGHT) {
                    break;
                }
                final Field field = board[currX][currY];
                if (field.isShip()) {
                    checkedPositions.add(field);
                    length++;
                    delta++;
                } else {
                    break;
                }
            }
            multiplier = -multiplier;
        } while (multiplier < 0);
        return length;
    }


    private boolean isAllowedPlacement(final int x, final int y) {
        final Field[][] board = botBoard.board;
        final Field field = board[x][y];
        field.setShip(true);
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j].isShip() && !checkField(i, j)) {
                    field.setShip(false);
                    return false;
                }
            }
        }
        field.setShip(false);
        return true;
    }

    private boolean checkField(final int i, final int j) {
        Vector2I[] adjacentLR = getAdjacentPositions(i, j, true);
        Vector2I[] adjacentTD = getAdjacentPositions(i, j, false);
        int sumLR = 0;
        int sumTD = 0;
        final Field[][] board = botBoard.board;
        for (final Vector2I neighbour : adjacentLR) {
            if (board[neighbour.getX()][neighbour.getY()].isShip()) {
                sumLR++;
            }
        }
        for (final Vector2I neighbour : adjacentTD) {
            if (board[neighbour.getX()][neighbour.getY()].isShip()) {
                sumTD++;
            }
        }
        return !(sumLR > 0 && sumTD > 0);
    }

    private Vector2I[] getAdjacentPositions(final int x, final int y, final boolean isHorizontal) {
        List<Vector2I> result = new ArrayList<>();
        for (int delta = -1; delta < 2; delta++) {
            final int currX = x + (isHorizontal ? delta : 0);
            final int currY = y + (isHorizontal ? 0 : delta);
            if (currX >= 0 && currX < WIDTH && currY >= 0 && currY < WIDTH && !(currX == x && currY == y)) {
                result.add(new Vector2I(currX, currY));
            }
        }
        return result.toArray(new Vector2I[0]);
    }

    private Vector2I[] getAdjacentPositions(final int x, final int y) {
        List<Vector2I> result = new ArrayList<>();
        for (int dX = -1; dX < 2; dX++) {
            for (int dY = -1; dY < 2; dY++) {
                if (Math.abs(dX) + Math.abs(dY) == 2 || dX == dY)
                    continue;
                final int currX = x + dX;
                final int currY = y + dY;
                if (currX >= 0 && currX < WIDTH && currY >= 0 && currY < WIDTH && !(currX == x && currY == y)) {
                    result.add(new Vector2I(currX, currY));
                }
            }
        }
        return result.toArray(new Vector2I[0]);
    }
}