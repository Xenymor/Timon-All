package Sudoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class Sudoku {

    public static final int SIZE = 900;
    public static final Color RED = new Color(255, 100, 100);
    public static final Color CHOSEN_COLOR = new Color(100, 255, 100);
    private final double diff;
    AtomicIntegerArray[] newSudoku;
    boolean[][] changeable;
    private boolean checked = false;
    private boolean won = false;
    private int chosenX;
    private int chosenY;

    public int[][] getSudoku() {
        int[][] res = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                res[i][j] = newSudoku[i].get(j);
            }
        }
        return res;
    }

    public void setSudoku(int[][] sudoku) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                this.newSudoku[i].set(j, sudoku[i][j]);
            }
        }
    }

    Sudoku(double diff) {
        this.diff = diff;
        newSudoku = new AtomicIntegerArray[9];
        for (int i = 0; i < newSudoku.length; i++) {
            newSudoku[i] = new AtomicIntegerArray(9);
        }
        createNewSudoku();
    }

    int[][] createSudoku() {
        int[][] numbers = {
                {1, 2, 3, 4, 5, 6, 7, 8, 9},
                {4, 5, 6, 7, 8, 9, 1, 2, 3},
                {7, 8, 9, 1, 2, 3, 4, 5, 6},

                {2, 3, 1, 5, 6, 4, 8, 9, 7},
                {5, 6, 4, 8, 9, 7, 2, 3, 1},
                {8, 9, 7, 2, 3, 1, 5, 6, 4},

                {3, 1, 2, 6, 4, 5, 9, 7, 8},
                {6, 4, 5, 9, 7, 8, 3, 1, 2},
                {9, 7, 8, 3, 1, 2, 6, 4, 5}
        };
        List<int[]> lines = new ArrayList<>();
        for (int i = 0; i < 9; i += 3) {
            lines.add(numbers[i]);
            lines.add(numbers[i + 1]);
            lines.add(numbers[i + 2]);
            for (int j = 0; j < 3; j++) {
                numbers[i + j] = lines.get(randomIntInRange(0, lines.size() - 1));
                lines.remove(numbers[i + j]);
            }
        }
        List<int[]> columns = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            int[] column = new int[9];
            for (int j = 0; j < 9; j++) {
                column[j] = numbers[j][i];
            }
            columns.add(column);
        }
        List<int[]> newColumns = new ArrayList<>();
        List<int[]> actualCol = new ArrayList<>();
        for (int i = 0; i < 9; i += 3) {
            actualCol.add(columns.get(i));
            actualCol.add(columns.get(i + 1));
            actualCol.add(columns.get(i + 2));
            for (int j = 0; j < 3; j++) {
                newColumns.add(actualCol.get(randomIntInRange(0, actualCol.size() - 1)));
                actualCol.remove(newColumns.get(newColumns.size() - 1));
            }
        }
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                numbers[y][x] = newColumns.get(x)[y];
            }
        }
        List<Integer> availableNumbers = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            availableNumbers.add(i);
        }
        int[] newNumbers = new int[9];
        for (int i = 0; i < newNumbers.length; i++) {
            newNumbers[i] = availableNumbers.get(randomIntInRange(0, availableNumbers.size() - 1));
            availableNumbers.remove((Integer) newNumbers[i]);
        }
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                numbers[x][y] = newNumbers[numbers[x][y] - 1] + 1;
            }
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (Math.random() <= diff) {
                    numbers[i][j] = 0;
                }
            }
        }
        return numbers;
    }

    public void createNewSudoku() {
        setSudoku(createSudoku());
        int[][] sudoku = getSudoku();
        changeable = new boolean[9][9];
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                changeable[x][y] = sudoku[x][y] == 0;
            }
        }
    }

    /*public static void main(String[] args) throws InterruptedException {
        System.out.println("Enter your difficulty from 0.25 (easy) to 0.7 (hard)");
        double diff = Double.parseDouble(new Scanner(System.in).nextLine());
        new Sudoku(diff).run();
    }*/

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Do you want to play yourself(p) or do you want to let the computer solve something?(c)");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("p")) {
            System.out.println("Enter your difficulty from 0.25 (easy) to 0.7 (hard)");
            double diff = Double.parseDouble(new Scanner(System.in).nextLine());
            new Sudoku(diff).run();
        } else if (input.equalsIgnoreCase("c")) {
            Sudoku sudoku = new Sudoku(0.66);
            System.out.println("Random(r) or user defined(ud)?");
            input = scanner.nextLine();
            if (input.equalsIgnoreCase("r")) {
                long start = System.nanoTime();
                int[][] solution = Sudoku.solve(sudoku);
                long end = System.nanoTime();
                if (solution == null) {
                    System.out.println("No solution");
                    System.exit(0);
                }
                for (int i = 0; i < 9; i++) {
                    System.out.println(Arrays.toString(solution[i]));
                }
                System.out.println("Took " + (TimeUnit.NANOSECONDS.toMicros(end - start)) + "µs to calculate");
            } else if (input.equalsIgnoreCase("ud")) {
                System.out.println("Enter the Sudoku line for line eg.: 100289070");
                int[][] newSudoku = new int[9][9];
                for (int i = 0; i < 9; i++) {
                    input = scanner.nextLine();
                    String[] split = input.split("");
                    for (int j = 0; j < 9; j++) {
                        newSudoku[i][j] = Integer.parseInt(split[j]);
                    }
                }
                sudoku.setSudoku(newSudoku);
                long start = System.nanoTime();
                int[][] solution = Sudoku.solve(sudoku);
                long end = System.nanoTime();
                if (solution == null) {
                    System.out.println("No solution");
                    System.exit(0);
                }
                for (int i = 0; i < 9; i++) {
                    System.out.println(Arrays.toString(solution[i]));
                }
                System.out.println("Took " + (TimeUnit.NANOSECONDS.toMicros(end - start)) + "µs to calculate");
            }
        }
    }

    private class IntegerList {
        List <Integer> list;

        public IntegerList() {
            this.list = new ArrayList<>();
        }

        public IntegerList(List<Integer> list) {
            this.list = list;
        }

        public List getList() {
            return list;
        }

        public void add(int toAdd) {
            list.add(toAdd);
        }

        public int get(int index) {
            return list.get(index);
        }

        public void remove(int index) {
            list.remove(index);
        }
    }

    private static int[][] solve(Sudoku sudoku) {
        int[][] newSudoku = sudoku.getSudoku().clone();
        IntegerList[][] possibilitiesArr = new IntegerList[9][9];
        int x = 0;
        int y = 0;
        if (isPossible(newSudoku)) {
            if (checkBoard(newSudoku)) {
                return newSudoku;
            }
        } else {
            return null;
        }
        boolean shouldRun = true;
        while (shouldRun) {

            boolean shouldContinue = false;
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (newSudoku[i][j] != 0) {
                        continue;
                    }
                    int[] possibilities = findPossibilities(newSudoku, i, j);
                    if (possibilities.length == 0) {
                        return null;
                    } else if (possibilities.length == 1) {
                        newSudoku[i][j] = possibilities[0];
                        shouldContinue = true;
                    }
                }
            }
            shouldRun = shouldContinue;
        }
        if (checkBoard(newSudoku)) {
            return newSudoku;
        }
        return solveField(newSudoku, x, y);
    }

    private static int[][] oldSolve(Sudoku sudoku) {
        int[][] newSudoku = sudoku.getSudoku().clone();
        int x = 0;
        int y = 0;
        if (isPossible(newSudoku)) {
            if (checkBoard(newSudoku)) {
                return newSudoku;
            }
        } else {
            return null;
        }
        boolean shouldRun = true;
        while (shouldRun) {
            boolean shouldContinue = false;
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (newSudoku[i][j] != 0) {
                        continue;
                    }
                    int[] possibilities = findPossibilities(newSudoku, i, j);
                    if (possibilities.length == 0) {
                        return null;
                    } else if (possibilities.length == 1) {
                        newSudoku[i][j] = possibilities[0];
                        shouldContinue = true;
                    }
                }
            }
            shouldRun = shouldContinue;
        }
        if (checkBoard(newSudoku)) {
            return newSudoku;
        }
        System.out.println("Start Backtracking");
        return solveField(newSudoku, x, y);
    }

    private static int[] findPossibilities(int[][] sudoku, int i, int j) {
        List<Integer> possibilities = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        for (int k = 0; k < 9; k++) {
            possibilities.remove((Integer) sudoku[i][k]);
        }
        for (int k = 0; k < 9; k++) {
            possibilities.remove((Integer) sudoku[k][j]);
        }
        int x = i / 3 * 3;
        int y = j / 3 * 3;
        for (int k = x; k < x + 3; k++) {
            for (int l = y; l < y + 3; l++) {
                possibilities.remove((Integer) sudoku[k][l]);
            }
        }
        return possibilities.stream().mapToInt(Integer::intValue).toArray();
    }

    static long startTime;

    private static int[][] solveField(int[][] newSudoku, int x, int y) {
        if (System.nanoTime() - startTime >= TimeUnit.SECONDS.toNanos(10)) { //every 10 seconds
            startTime = System.nanoTime();
            for (int i = 0; i < 9; i++) {
                System.out.println(Arrays.toString(newSudoku[i]));
            }
            System.out.println("\n");
        }
        if (newSudoku[y][x] != 0) {
            if (x == 8) {
                if (y == 8) {
                    return null;
                }
                return solveField(newSudoku, 0, y + 1);
            } else {
                return solveField(newSudoku, x + 1, y);
            }
        }
        for (int i = 0; i < 9; i++) {
            newSudoku[y][x] = i + 1;
            if (isPossible(newSudoku, x, y)) {
                if (checkBoard(newSudoku)) {
                    return newSudoku;
                }
            } else {
                continue;
            }
            if (x == 8) {
                if (y == 8) {
                    continue;
                }
                int[][] result = solveField(newSudoku, 0, y + 1);
                if (result != null) {
                    return result;
                }
            } else {
                int[][] result = solveField(newSudoku, x + 1, y);
                if (result != null) {
                    return result;
                }
            }
        }
        newSudoku[y][x] = 0;
        return null;
    }

    private static boolean isPossible(int[][] sudoku, int x, int y) {
        if (!checkLinePossible(sudoku, y)) {
            return false;
        }
        if (!checkRowsPossible(sudoku, x)) {
            return false;
        }
        return checkSurroundingPossible(sudoku, x / 3 * 3, y / 3 * 3);
    }

    private static boolean checkSurroundingPossible(int[][] sudoku, int x, int y) {
        for (int k = 0; k < 9; k++) {
            if (indexOfSquare(k + 1, x, y, 3, 3, sudoku) != lastIndexOfSquare(k + 1, x, y, 3, 3, sudoku)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkRowsPossible(int[][] sudoku, int x) {
        for (int j = 0; j < 9; j++) {
            if (indexOfRow(j + 1, x, sudoku) != lastIndexOfRow(j + 1, x, sudoku)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkLinePossible(int[][] sudoku, int y) {
        for (int j = 0; j < 9; j++) {
            if (indexOf(j + 1, y, sudoku) != lastIndexOf(j + 1, y, sudoku)) {
                return false;
            }
        }
        return true;
    }

    public static int[][] deepClone(int[][] newSudoku) {
        int[][] clone = new int[newSudoku.length][newSudoku[0].length];
        for (int i = 0; i < clone.length; i++) {
            clone[i] = newSudoku[i].clone();
        }
        return clone;
    }

    private void run() throws InterruptedException {
        final Dimension[] clickPos = {new Dimension(-1, -1)};
        chosenX = -1;
        chosenY = -1;
        final int[] typedInt = {-1};
        final boolean[] checkBoard = {false};
        JFrame jFrame = new JFrame();
        jFrame.add(new MyPanel(SIZE, SIZE));
        jFrame.setUndecorated(true);
        jFrame.setSize(SIZE, SIZE);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == 1) {
                    clickPos[0] = new Dimension(e.getLocationOnScreen().x, e.getLocationOnScreen().y);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        jFrame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    checkBoard[0] = true;
                } else {
                    try {
                        typedInt[0] = Integer.parseInt(String.valueOf(e.getKeyChar()));
                    } catch (NumberFormatException a) {
                        a.printStackTrace();
                    }
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        jFrame.setVisible(true);
        while (true) {
            if (clickPos[0].width != -1) {
                Dimension pos = clickPos[0];
                int x = (int) pos.getWidth();
                int y = (int) pos.getHeight();
                Point clickPos1 = new Point(x, y);
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (new Rectangle(i * SIZE / 9, j * SIZE / 9, SIZE / 9, SIZE / 9).contains(clickPos1)) {
                            chosenX = i;
                            chosenY = j;
                        }
                    }
                }
                clickPos[0] = new Dimension(-1, -1);
            }
            if (typedInt[0] != -1 & chosenX != -1) {
                if (changeable[chosenX][chosenY]) {
                    newSudoku[chosenX].set(chosenY, typedInt[0]);
                }
                chosenX = -1;
                chosenY = -1;
                typedInt[0] = -1;
            }
            if (checkBoard[0]) {
                boolean result = checkBoard();
                checkBoard[0] = false;
                checked = true;
                won = result;
            }
            Thread.sleep(20);
        }
    }

    private boolean checkBoard() {
        List<List<Integer>> sudokuList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            sudokuList.add(new ArrayList<>());
            for (int j = 0; j < 9; j++) {
                sudokuList.get(i).add(newSudoku[i].get(j));
            }
        }
        if (!checkLine(sudokuList)) {
            return false;
        }
        if (!checkRows(sudokuList)) {
            return false;
        }
        return checkSurrounding(sudokuList);
    }

    private static boolean isPossible(int[][] sudoku) {
        if (!checkLinePossible(sudoku)) {
            return false;
        }
        if (!checkRowsPossible(sudoku)) {
            return false;
        }
        return checkSurroundingPossible(sudoku);
    }

    private static int indexOf(int o, int line, int[][] toSearch) {
        for (int i = 0; i < toSearch[line].length; i++) {
            if (toSearch[line][i] == o) {
                return i;
            }
        }
        return -1;
    }

    private static int lastIndexOf(int o, int line, int[][] toSearch) {
        int lastIndex = -1;
        for (int i = 0; i < toSearch[line].length; i++) {
            if (toSearch[line][i] == o) {
                lastIndex = i;
            }
        }
        return lastIndex;
    }

    private static int indexOfRow(int o, int row, int[][] toSearch) {
        for (int i = 0; i < toSearch.length; i++) {
            if (toSearch[i][row] == o) {
                return i;
            }
        }
        return -1;
    }

    private static int lastIndexOfRow(int o, int row, int[][] toSearch) {
        int lastIndex = -1;
        for (int i = 0; i < toSearch.length; i++) {
            if (toSearch[i][row] == o) {
                lastIndex = i;
            }
        }
        return lastIndex;
    }

    private static int indexOfSquare(int o, int squareX, int squareY, int width, int height, int[][] toSearch) {
        for (int x = squareX; x < squareX + width; x++) {
            for (int y = squareY; y < squareY + height; y++) {
                if (o == toSearch[x][y]) {
                    return width * squareY + y - 1;
                }
            }
        }
        return -1;
    }

    private static int lastIndexOfSquare(int o, int squareX, int squareY, int width, int height, int[][] toSearch) {
        int last = -1;
        for (int x = squareX; x < squareX + width; x++) {
            for (int y = squareY; y < squareY + height; y++) {
                if (o == toSearch[x][y]) {
                    last = width * squareY + y - 1;
                }
            }
        }
        return last;
    }

    private static boolean checkSurroundingPossible(int[][] sudoku) {
        for (int i = 0; i < sudoku.length - 2; i += 3) {
            for (int j = 0; j < sudoku[i].length - 2; j += 3) {
                for (int k = 0; k < 9; k++) {
                    if (indexOfSquare(k + 1, i, j, 3, 3, sudoku) != lastIndexOfSquare(k + 1, i, j, 3, 3, sudoku)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static boolean checkRowsPossible(int[][] sudoku) {
        for (int i = 0; i < sudoku.length; i++) {
            for (int j = 0; j < 9; j++) {
                if (indexOfRow(j + 1, i, sudoku) != lastIndexOfRow(j + 1, i, sudoku)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean checkLinePossible(int[][] sudoku) {
        for (int i = 0, sudokuListSize = sudoku.length; i < sudokuListSize; i++) {
            for (int j = 0; j < 9; j++) {
                if (indexOf(j + 1, i, sudoku) != lastIndexOf(j + 1, i, sudoku)) {
                    return false;
                }
            }
        }
        return true;
    }

    static private boolean checkBoard(int[][] sudoku) {
        List<List<Integer>> sudokuList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            sudokuList.add(new ArrayList<>());
            for (int j = 0; j < 9; j++) {
                sudokuList.get(i).add(sudoku[i][j]);
            }
        }
        if (!checkLine(sudokuList)) {
            return false;
        }
        if (!checkRows(sudokuList)) {
            return false;
        }
        return checkSurrounding(sudokuList);
    }

    private static boolean checkRows(List<List<Integer>> sudokuList) {
        for (int i = 0; i < sudokuList.size(); i++) {
            List<Integer> row = new ArrayList<>();
            for (int j = 0; j < sudokuList.get(i).size(); j++) {
                row.add(sudokuList.get(j).get(i));
            }
            for (int j = 0; j < 9; j++) {
                if (!row.contains(j + 1)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean checkSurrounding(List<List<Integer>> sudokuList) {
        for (int i = 0; i < sudokuList.size() - 2; i += 3) {
            for (int j = 0; j < sudokuList.get(i).size() - 2; j += 3) {
                List<Integer> square = new ArrayList<>();
                for (int k = 0; k < 3; k++) {
                    for (int l = 0; l < 3; l++) {
                        square.add(sudokuList.get(i + k).get(j + l));
                    }
                }
                for (int k = 0; k < 9; k++) {
                    if (!square.contains(k + 1)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static boolean checkLine(List<List<Integer>> sudokuList) {
        for (List<Integer> integers : sudokuList) {
            for (int j = 0; j < integers.size(); j++) {
                if (!integers.contains(j + 1)) {
                    return false;
                }
            }
        }
        return true;
    }

    public double map(double n, double start1, double stop1, double start2, double stop2) {
        return ((n - start1) / (stop1 - start1)) * (stop2 - start2) + start2;
    }

    public int randomIntInRange(int min, int max) {
        return (int) Math.round(min + Math.random() * (max - min));
    }

    private class MyPanel extends JPanel {
        final int LINE_WIDTH = 5;
        final Color BACKGROUND = new Color(255, 255, 255);
        final Color LINE_COLOR = new Color(0, 0, 0);
        final int WIDTH;
        final int HEIGHT;
        int counter = 0;

        public MyPanel(int WIDTH, int HEIGHT) {
            this.WIDTH = WIDTH;
            this.HEIGHT = HEIGHT;
        }

        @Override
        public void paint(Graphics g) {
            Font currentFont = g.getFont();
            Font newFont = currentFont.deriveFont(currentFont.getSize() * 3F);
            g.setFont(newFont);
            g.setColor(BACKGROUND);
            g.fillRect(0, 0, WIDTH, HEIGHT);
            g.setColor(CHOSEN_COLOR);
            g.fillRect(chosenX * WIDTH/9, chosenY* HEIGHT/9, (WIDTH/9), (HEIGHT/9));
            g.setColor(LINE_COLOR);
            for (int i = 0; i < WIDTH; i += (WIDTH / 9)) {
                if (i % 3 == 0) {
                    g.fillRect(i - (LINE_WIDTH / 2), 0, LINE_WIDTH * 2, HEIGHT);
                } else {
                    g.fillRect(i - (LINE_WIDTH / 2), 0, LINE_WIDTH, HEIGHT);
                }
            }
            for (int i = 0; i < HEIGHT; i += (HEIGHT / 9)) {
                if (i % 3 == 0) {
                    g.fillRect(0, i - (LINE_WIDTH / 2), WIDTH, LINE_WIDTH * 2);
                } else {
                    g.fillRect(0, i - (LINE_WIDTH / 2), WIDTH, LINE_WIDTH);
                }
            }
            if (checked) {
                g.setColor(RED);
                if (won) {
                    g.drawString("You won", WIDTH / 2 - 40, HEIGHT / 2 - 5);
                    counter++;
                    if (counter >= 60) {
                        checked = false;
                        counter = 0;
                        createNewSudoku();
                    }
                } else {
                    g.fillRect(0, 0, WIDTH, HEIGHT);
                    counter++;
                    if (counter >= 60) {
                        checked = false;
                        counter = 0;
                    }
                }
            } else {
                if (newSudoku != null) {
                    int[][] sudoku = getSudoku();
                    if (chosenX != -1) {
                        for (int x = 0; x < 9; x++) {
                            for (int y = 0; y < 9; y++) {
//                                if (chosenX == x && chosenY == y) {
//                                    g.setColor(CHOSEN_COLOR);
//                                    g.fillRect(x*SIZE/9+3, y*SIZE/9+8, SIZE/9-7, SIZE/9-7);
//                                }
                                if (sudoku[x][y] != 0) {
                                    g.setColor(LINE_COLOR);
                                    g.drawString(Integer.toString(sudoku[x][y]), x * WIDTH / 9 + WIDTH / 18 - 4, y * HEIGHT / 9 + HEIGHT / 18 + 4);
                                }
                            }
                        }
                    } else {
                        for (int x = 0; x < 9; x++) {
                            for (int y = 0; y < 9; y++) {
                                if (sudoku[x][y] != 0) {
                                    g.drawString(Integer.toString(sudoku[x][y]), x * WIDTH / 9 + WIDTH / 18 - 4, y * HEIGHT / 9 + HEIGHT / 18 + 4);
                                }
                            }
                        }
                    }
                }
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            repaint();
        }
    }
}