package TicTacToe;

import StandardClasses.Vector2L;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class TicTacToe {
    public static final int FIELD_SIZE = 3;
    final FieldPosition field;
    private boolean computerOnTurn;
    final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public TicTacToe() {
        this.field = new FieldPosition();
        field.setField(new int[FIELD_SIZE][FIELD_SIZE]);
    }

    @SuppressWarnings("CommentedOutCode")
    public static void main(String[] args) throws CloneNotSupportedException {
        TicTacToe game = new TicTacToe();
        /*MinMaxResult bestMoveWithScore = game.findBestMoveWithScore();
        System.out.println(bestMoveWithScore.getMove() + "; " + bestMoveWithScore.getScore());
        game.start();*/
        game.playSelfAndPrintMoves();
    }

    private void playSelfAndPrintMoves() throws CloneNotSupportedException {
        while (!this.isWon() && !this.isDrawn()) {
            MinMaxResult move = findBestMoveWithScore();
            makeMove(move.getX(), move.getY(), field, getComputerOnTurn() ? 1 : -1);
            setComputerOnTurn(!getComputerOnTurn());
            printField();
            System.out.println(move.getScore());
        }
    }

    private void start() throws IOException, CloneNotSupportedException {
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
        for (int[] ints : field.getField()) {
            for (int anInt : ints) {
                if (anInt == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private void gameLoop() throws IOException, CloneNotSupportedException {
        boolean moveWorked = getAndMakeMove();
        if (getComputerOnTurn()) {
            printField();
        }
        if (moveWorked) {
            setComputerOnTurn(!getComputerOnTurn());
        }
    }

    private boolean getAndMakeMove() throws IOException, CloneNotSupportedException {
        Vector2L move = getMove();
        if (isValidMove(move)) {
            makeMove((int) move.getX(), (int) move.getY(), field, getComputerOnTurn() ? 1 : -1);
            return true;
        } else if (getComputerOnTurn()) {
            System.out.println("Engine misfunctioned");
            return false;
        } else {
            System.out.println("Invalid move");
            return false;
        }
    }

    private Vector2L getMove() throws IOException, CloneNotSupportedException {
        return getComputerOnTurn() ? findBestMove() : askMove();
    }

    private boolean isValidMove(Vector2L move) {
        if (move.getX() < 0 || move.getY() < 0 || move.getX() > field.getField().length || move.getY() > field.getField().length) {
            return false;
        }
        return field.getField()[(int) move.getX()][(int) move.getY()] == 0;
    }

    private void printDrawMessage() {
        System.out.println("You drew the game");
    }

    private Vector2L findBestMove() throws CloneNotSupportedException {
        FieldPosition fieldAfterMove = new FieldPosition();
        fieldAfterMove.setField(field.getField().clone());
        for (int i = 0; i < fieldAfterMove.getField().length; i++) {
            fieldAfterMove.getField()[i] = fieldAfterMove.getField()[i].clone();
        }
        MinMaxResult result = minMax(fieldAfterMove, 1);
        return result.getMove();
    }

    private MinMaxResult findBestMoveWithScore() throws CloneNotSupportedException {
        FieldPosition fieldAfterMove = new FieldPosition();
        fieldAfterMove.setField(field.getField().clone());
        for (int i = 0; i < fieldAfterMove.getField().length; i++) {
            fieldAfterMove.getField()[i] = fieldAfterMove.getField()[i].clone();
        }
        return minMax(fieldAfterMove, 1);
    }

    final Map<FieldPosition, MinMaxResult> checkedPositions = new HashMap<>();

    private MinMaxResult minMax(FieldPosition fieldAfterMove, int player) throws CloneNotSupportedException {
        if (checkedPositions.containsKey(fieldAfterMove)) {
            return checkedPositions.get(fieldAfterMove);
        }
        if (isWon(fieldAfterMove.getField())) {
            checkedPositions.put(fieldAfterMove, new MinMaxResult(player, -1, -1));
            return checkedPositions.get(fieldAfterMove);
        }
        int bestScore = player == 1 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int bestX = -1;
        int bestY = -1;
        for (int x = 0; x < fieldAfterMove.getField().length; x++) {
            for (int y = 0; y < fieldAfterMove.getField()[x].length; y++) {
                if (fieldAfterMove.getField()[x][y] == 0) {
                    makeMove(x, y, fieldAfterMove, player);
                    if (isWon(fieldAfterMove.getField())) {
                        checkedPositions.put(fieldAfterMove.clone(), new MinMaxResult(player, x, y));
                        reverseMove(x, y, fieldAfterMove.getField());
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
                    reverseMove(x, y, fieldAfterMove.getField());
                }
            }
        }
        if (bestX == -1) {
            checkedPositions.put(fieldAfterMove.clone(), new MinMaxResult(0, -1, -1));
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

    private void makeMove(int x, int y, FieldPosition board, int player) {
        board.getField()[x][y] = player;
        board.player = player == 1 ? -1 : 1;
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
        boolean diagonalsWon = checkDiagonals(field.getField());
        boolean horizontalWon = checkHorizontal(field.getField());
        boolean verticalWon = checkVertical(field.getField());
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
        //System.out.println(Arrays.deepToString(field.getField()));
        final int[][] field = this.field.getField();
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
