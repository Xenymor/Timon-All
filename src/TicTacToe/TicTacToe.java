package TicTacToe;

import StandardClasses.Vector2L;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TicTacToe {
    int[][] field;
    private boolean computerOnTurn;
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public TicTacToe() {
        this.field = new int[3][3];
    }

    public static void main(String[] args) throws IOException {
        TicTacToe game = new TicTacToe();
        //System.out.println(game.findBestMoveWithScore().getMove());
        game.start();
    }

    private void start() throws IOException {
        printField();
        boolean computerStarting = askComputerStarting();
        setComputerOnTurn(computerStarting);
        while (true) {
            if (isWon()) {
                printEndMessage();
                break;
            } else if (isDrawn()) {
                printDrawMessage();
                break;
            }
            gameLoop();
        }
    }

    private boolean isDrawn() {
        int freeFields = 0;
        for (int[] ints : field) {
            for (int anInt : ints) {
                if (anInt == 0) {
                    freeFields++;
                }
            }
        }
        return freeFields == 0;
    }

    private void gameLoop() throws IOException {
        getAndMakeMove();
        if (getComputerOnTurn()) {
            printField();
        }
        setComputerOnTurn(!getComputerOnTurn());
    }

    private void getAndMakeMove() throws IOException {
        while (true) {
            Vector2L move = getMove();
            if (isValidMove(move)) {
                makeMove((int) move.getX(), (int) move.getY(), field, getComputerOnTurn() ? 1 : -1);
                break;
            } else if (getComputerOnTurn()) {
                System.out.println("Engine misfunctioned");
            } else {
                System.out.println("Invalid move");
            }
        }
    }

    private Vector2L getMove() throws IOException {
        return getComputerOnTurn() ? findBestMove() : askMove();
    }

    private boolean isValidMove(Vector2L move) {
        return field[(int) move.getX()][(int) move.getY()] == 0;
    }

    private void printDrawMessage() {
        System.out.println("You drew the game");
    }

    private Vector2L findBestMove() {
        int[][] fieldAfterMove = field.clone();
        for (int i = 0; i < fieldAfterMove.length; i++) {
            fieldAfterMove[i] = fieldAfterMove[i].clone();
        }
        MinMaxResult result = minMax(fieldAfterMove, 1);
        return result.getMove();
    }

    private MinMaxResult findBestMoveWithScore() {
        int[][] fieldAfterMove = field.clone();
        for (int i = 0; i < fieldAfterMove.length; i++) {
            fieldAfterMove[i] = fieldAfterMove[i].clone();
        }
        return minMax(fieldAfterMove, 1);
    }

    private MinMaxResult minMax(int[][] fieldAfterMove, int player) {
        int bestScore = player == 1 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int bestX = -1;
        int bestY = -1;
        for (int x = 0; x < fieldAfterMove.length; x++) {
            for (int y = 0; y < fieldAfterMove[x].length; y++) {
                if (fieldAfterMove[x][y] == 0) {
                    makeMove(x, y, fieldAfterMove, player);
                    if (isWon(fieldAfterMove)) {
                        reverseMove(x, y, fieldAfterMove);
                        return new MinMaxResult(player, x, y);
                    }
                    MinMaxResult currentResult = minMax(fieldAfterMove, -player);
                    if (player == -1) {
                        if (currentResult.getScore() <= bestScore) {
                            bestScore = currentResult.getScore();
                            bestX = x;
                            bestY = y;
                        }
                    } else {
                        if (currentResult.getScore() >= bestScore) {
                            bestScore = currentResult.getScore();
                            bestX = x;
                            bestY = y;
                        }
                    }
                    reverseMove(x, y, fieldAfterMove);
                }
            }
        }
        if (bestX == -1) {
            return new MinMaxResult(0, bestX, bestY);
        }
        return new MinMaxResult(bestScore, bestX, bestY);
    }

    private boolean isWon(int[][] fieldAfterMove) {
        boolean diagonalsWon = checkDiagonals(fieldAfterMove);
        boolean horizontalWon = checkHorizontal(fieldAfterMove);
        boolean verticalWon = checkVertical(fieldAfterMove);
        return diagonalsWon || horizontalWon || verticalWon;
    }

    private boolean checkVertical(int[][] fieldAfterMove) {
        int firstValue = 0;
        outer:
        for (int y = 0; y < fieldAfterMove[0].length; y++) {
            for (int x = 0; x < fieldAfterMove.length; x++) {
                if (x == 0) {
                    firstValue = fieldAfterMove[x][y];
                    if (firstValue == 0) {
                        continue outer;
                    }
                } else {
                    if (firstValue != fieldAfterMove[x][y]) {
                        continue outer;
                    }
                }
            }
            return true;
        }
        return false;
    }

    private boolean checkHorizontal(int[][] fieldAfterMove) {
        for (int[] ints : fieldAfterMove) {
            if (allEquals(ints)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkDiagonals(int[][] fieldAfterMove) {
        int firstField = -10;
        boolean broken = false;
        for (int x = 0; x < fieldAfterMove.length; x++) {
            if (x == 0) {
                firstField = fieldAfterMove[x][x];
                if (firstField == 0) {
                    broken = true;
                    break;
                }
            } else {
                if (fieldAfterMove[x][x] != firstField) {
                    broken = true;
                    break;
                }
            }
        }
        if (!broken) {
            return true;
        }
        broken = false;
        for (int x = 0; x < fieldAfterMove.length; x++) {
            if (x == 0) {
                firstField = fieldAfterMove[x][fieldAfterMove.length - x - 1];
                if (firstField == 0) {
                    broken = true;
                    break;
                }
            } else {
                if (fieldAfterMove[x][fieldAfterMove.length - x - 1] != firstField) {
                    broken = true;
                    break;
                }
            }
        }
        return !broken;
    }

    private void printEndMessage() {
        if (getComputerOnTurn()) {
            System.out.println("You won");
        } else {
            System.out.println("You lost");
        }
    }

    private void makeMove(int x, int y, int[][] field, int player) {
        field[x][y] = player;
    }

    private void reverseMove(int x, int y, int[][] field) {
        field[x][y] = 0;
    }

    private Vector2L askMove() throws IOException {
        String answer;
        while (true) {
            System.out.println("Where do you want to place? (x(1-3), y(1-3))");
            answer = reader.readLine();
            try {
                String[] coordinateStrings = answer.split(", ");
                return new Vector2L(Integer.parseInt(coordinateStrings[0]) - 1, Integer.parseInt(coordinateStrings[1]) - 1);
            } catch (Exception ignored) {

            }
        }
    }

    private void setComputerOnTurn(boolean newPlayer) {
        this.computerOnTurn = newPlayer;
    }

    private boolean getComputerOnTurn() {
        return computerOnTurn;
    }

    private boolean isWon() {
        boolean diagonalsWon = checkDiagonals(field);
        boolean horizontalWon = checkHorizontal(field);
        boolean verticalWon = checkVertical(field);
        return diagonalsWon || horizontalWon || verticalWon;
    }

    private boolean allEquals(int... ints) {
        if (ints.length == 0) {
            return true;
        }
        if (ints[0] == 0) {
            return false;
        }
        int startValue = ints[0];
        for (int i = 1; i < ints.length; i++) {
            if (ints[i] != startValue) {
                return false;
            }
        }
        return true;
    }

    private boolean askComputerStarting() throws IOException {
        System.out.print("Do you want to start? (Y/N)");
        String line = reader.readLine();
        while (true) {
            switch (line) {
                case "Y":
                    return false;
                case "N":
                    return true;
                default:
                    System.out.println("No possible answer. Try again.");
                    line = reader.readLine();
            }
        }
    }

    private void printField() {
        for (int y = field.length - 1; y >= 0; y--) {
            for (int x = 0; x < field[0].length; x++) {
                switch (field[x][y]) {
                    case -1 -> System.out.print("X");
                    case 0 -> System.out.print("-");
                    case 1 -> System.out.print("0");
                }
            }
            System.out.println();
        }
    }

    private static class MinMaxResult {
        int score;
        int x;
        int y;

        public MinMaxResult(int score, int x, int y) {
            this.score = score;
            this.x = x;
            this.y = y;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public Vector2L getMove() {
            return new Vector2L(x, y);
        }

        public void setMove(Vector2L move) {
            this.x = (int) move.getX();
            this.y = (int) move.getY();
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }
}
