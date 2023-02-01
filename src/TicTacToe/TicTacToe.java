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
        //System.out.println(game.findBestMoveWithScore().getScore());
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
        Vector2L move = getComputerOnTurn() ? findBestMove() : askMove();
        makeMove(move, field, getComputerOnTurn() ? 1 : -1);
        if (getComputerOnTurn()) {
            printField();
        }
        setComputerOnTurn(!getComputerOnTurn());
    }

    private void printDrawMessage() {
        System.out.println("You drawed the game");
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
        Vector2L bestMove = new Vector2L(-1, -1);
        for (int x = 0; x < fieldAfterMove.length; x++) {
            for (int y = 0; y < fieldAfterMove[x].length; y++) {
                if (fieldAfterMove[x][y] == 0) {
                    Vector2L move = new Vector2L(x, y);
                    makeMove(move, fieldAfterMove, player);
                    if (isWon(fieldAfterMove)) {
                        reverseMove(move, fieldAfterMove);
                        return new MinMaxResult(player, move);
                    }
                    MinMaxResult currentResult = minMax(fieldAfterMove, -player);
                    if (player == -1) {
                        if (currentResult.getScore() <= bestScore) {
                            bestScore = currentResult.getScore();
                            bestMove = move;
                        }
                    } else {
                        if (currentResult.getScore() >= bestScore) {
                            bestScore = currentResult.getScore();
                            bestMove = move;
                        }
                    }
                    reverseMove(move, fieldAfterMove);
                }
            }
        }
        if (bestMove.getX() == -1) {
            return new MinMaxResult(0, new Vector2L(-1, -1));
        }
        return new MinMaxResult(bestScore, bestMove);
    }

    private boolean isWon(int[][] fieldAfterMove) {
        boolean diagonalsWon = checkDiagonals(fieldAfterMove);
        boolean horizontalWon = checkHorizontal(fieldAfterMove);
        boolean verticalWon = checkVertical(fieldAfterMove);
        return diagonalsWon || horizontalWon || verticalWon;
    }

    private boolean checkVertical(int[][] fieldAfterMove) {
        for (int i = 0; i < fieldAfterMove[0].length; i++) {
            if (threeEquals(fieldAfterMove[0][i], fieldAfterMove[1][i], fieldAfterMove[2][i])) {
                return true;
            }
        }
        return false;
    }

    private boolean checkHorizontal(int[][] fieldAfterMove) {
        for (int[] ints : fieldAfterMove) {
            if (threeEquals(ints[0], ints[1], ints[2])) {
                return true;
            }
        }
        return false;
    }

    private boolean checkDiagonals(int[][] fieldAfterMove) {
        if (threeEquals(fieldAfterMove[0][0], fieldAfterMove[1][1], fieldAfterMove[2][2])) {
            return true;
        }
        return threeEquals(fieldAfterMove[2][0], fieldAfterMove[1][1], fieldAfterMove[0][2]);
    }

    private void printEndMessage() {
        if (getComputerOnTurn()) {
            System.out.println("You won");
        } else {
            System.out.println("You lost");
        }
    }

    private void makeMove(Vector2L move, int[][] field, int player) {
        field[(int) move.getX()][(int) move.getY()] = player;
    }

    private void reverseMove(Vector2L move, int[][] field) {
        field[(int) move.getX()][(int) move.getY()] = 0;
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
        boolean diagonalsWon = checkDiagonals();
        boolean horizontalWon = checkHorizontal();
        boolean verticalWon = checkVertical();
        return diagonalsWon || horizontalWon || verticalWon;
    }

    private boolean checkVertical() {
        for (int i = 0; i < field[0].length; i++) {
            if (threeEquals(field[0][i], field[1][i], field[2][i])) {
                return true;
            }
        }
        return false;
    }

    private boolean checkHorizontal() {
        for (int[] ints : field) {
            if (threeEquals(ints[0], ints[1], ints[2])) {
                return true;
            }
        }
        return false;
    }

    private boolean checkDiagonals() {
        if (threeEquals(field[0][0], field[1][1], field[2][2])) {
            return true;
        }
        return threeEquals(field[2][0], field[1][1], field[0][2]);
    }

    private boolean threeEquals(int i, int i1, int i2) {
        return (i == i1) && (i == i2) && (i != 0);
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

    private class MinMaxResult {
        int score;
        Vector2L move;

        public MinMaxResult(int score, Vector2L move) {
            this.score = score;
            this.move = move;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public Vector2L getMove() {
            return move;
        }

        public void setMove(Vector2L move) {
            this.move = move;
        }
    }
}
