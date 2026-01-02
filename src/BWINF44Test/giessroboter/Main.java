package BWINF44Test.giessroboter;

import BWINF44Test.giessroboter.Solvers.PopelHeuristic;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {

    public static final String EXAMPLES_DIR = "src/BWINF44Test/giessroboter/examples/";
    public static final String EXAMPLE_START = "roboter";
    public static final String FILE_TYPE = ".txt";
    public static final String EXAMPLE_NUM = "01";
    public static final String EXAMPLE = EXAMPLE_START + EXAMPLE_NUM + FILE_TYPE;
    public static final String EXAMPLE_PATH = EXAMPLES_DIR + EXAMPLE;
    public static final int EXAMPLE_COUNT = 11;

    public static void main() throws IOException {
        //runExample();

        //testAllExamples();

        compareSolvers();
    }

    private static void testAllExamples() {
        for (int i = 1; i <= EXAMPLE_COUNT; i++) {
            String exampleNum = (i < 10) ? "0" + i : Integer.toString(i);
            String exampleFile = EXAMPLE_START + exampleNum + FILE_TYPE;
            String examplePath = EXAMPLES_DIR + exampleFile;
            try {
                Problem problem = new Problem(Files.readAllLines(Path.of(examplePath)));

                System.out.println("Example " + exampleNum);
                long startNanos = System.nanoTime();
                Solution estimate = PopelHeuristic.solve2(problem);
                System.out.println("Calculation time: " + (System.nanoTime() - startNanos)/1_000_000F + "ms");
                estimate.display(1000, 1000);
                Thread.sleep(20);
                estimate.closeDisplay();
            } catch (IOException e) {
                System.err.println("Failed to read example file: " + examplePath);
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void runExample() throws IOException {
        Problem problem = new Problem(Files.readAllLines(Path.of(EXAMPLE_PATH)));

        //problem.display(1000, 1000);

        long startNanos = System.nanoTime();
        Solution estimate = PopelHeuristic.solve2(problem);
        System.out.println("Calculation time: " + (System.nanoTime() - startNanos)/1_000_000F + "ms");

        //problem.closeDisplay();
        estimate.display(1000, 1000);
    }

    private static void compareSolvers() {
        int solveBetterCount = 0;
        int solve2BetterCount = 0;
        int equalCount = 0;
        double totalRatioCycles = 0;
        double totalRatioLength = 0;

        System.out.println("=".repeat(80));

        for (int i = 1; i <= EXAMPLE_COUNT; i++) {
            String exampleNum = (i < 10) ? "0" + i : Integer.toString(i);
            String exampleFile = EXAMPLE_START + exampleNum + FILE_TYPE;
            String examplePath = EXAMPLES_DIR + exampleFile;

            try {
                Problem problem = new Problem(Files.readAllLines(Path.of(examplePath)));

                Solution solution1 = PopelHeuristic.solve(problem);
                Solution solution2 = PopelHeuristic.solve2(problem);

                int cycles1 = solution1.getCycles().size();
                int cycles2 = solution2.getCycles().size();
                double length1 = calculateTotalLength(solution1);
                double length2 = calculateTotalLength(solution2);

                double ratioCycles = (double) cycles1 / cycles2;
                double ratioLength = length1 / length2;
                totalRatioCycles += ratioCycles;
                totalRatioLength += ratioLength;

                String winner;
                if (cycles1 < cycles2) {
                    solveBetterCount++;
                    winner = "solve better";
                } else if (cycles2 < cycles1) {
                    solve2BetterCount++;
                    winner = "solve2 better";
                } else {
                    equalCount++;
                    winner = "tied";
                }

                System.out.printf("Example %s: solve=%d Zyklen (len=%.1f), solve2=%d Zyklen (len=%.1f) -> Ratio: %.2f (%s)\n",
                        exampleNum, cycles1, length1, cycles2, length2, ratioCycles, winner);

            } catch (IOException e) {
                System.err.println("Fehler beim Lesen: " + examplePath);
                e.printStackTrace();
            }
        }

        System.out.println("=".repeat(80));
        System.out.println("Overview:");
        System.out.printf("solve won:  %d times\n", solveBetterCount);
        System.out.printf("solve2 won: %d times\n", solve2BetterCount);
        System.out.printf("tied:        %d times\n", equalCount);
        System.out.printf("Average cyle ratio (solve/solve2): %.3f\n", totalRatioCycles / EXAMPLE_COUNT);
        System.out.printf("Average length ratio (solve/solve2):  %.3f\n", totalRatioLength / EXAMPLE_COUNT);
        System.out.println("=".repeat(80));
    }

    private static double calculateTotalLength(Solution solution) {
        double totalLength = 0;
        for (List<Point> cycle : solution.getCycles()) {
            Point prev = cycle.getLast();
            for (Point point : cycle) {
                totalLength += prev.distance(point);
                prev = point;
            }
        }
        return totalLength;
    }
}
