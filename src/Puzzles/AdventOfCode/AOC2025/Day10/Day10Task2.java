package Puzzles.AdventOfCode.AOC2025.Day10;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Day10Task2 {
    static void main() throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/Puzzles/AdventOfCode/AOC2025/Day10/input.txt"));

        long result = 0;

        for (int i = 0; i < lines.size(); i++) {
            System.out.println("Processing line " + (i + 1) + "/" + lines.size());
            final String line = lines.get(i);
            int[] targetState = parseTargetState(line);

            int[][] moves = parseMoves(line);

            result += solve(targetState, moves);

            System.out.println("Current result: " + result);
        }

        System.out.println("Result: " + result);

    }

    private static long solve(int[] targetState, int[][] moves) {
        Loader.loadNativeLibraries();

        MPSolver solver = MPSolver.createSolver("SCIP");

        MPVariable[] numApplies = new MPVariable[moves.length];
        for (int i = 0; i < numApplies.length; i++) {
            numApplies[i] = solver.makeIntVar(0.0, Double.POSITIVE_INFINITY, "nm_" + i);
        }

        for (int i = 0; i < targetState.length; i++) {
            MPConstraint constraint = solver.makeConstraint(targetState[i], targetState[i], "ct_" + i);
            for (int j = 0; j < moves.length; j++) {
                if (containsIndex(moves[j], i)) {
                    constraint.setCoefficient(numApplies[j], 1);
                }
            }
        }

        MPObjective objective = solver.objective();
        for (final MPVariable numApply : numApplies) {
            objective.setCoefficient(numApply, 1);
        }
        objective.setMinimization();

        var status = solver.solve();

        if (status != MPSolver.ResultStatus.OPTIMAL) {
            throw new RuntimeException("No optimal solution found!");
        }

        long total = 0;
        for (MPVariable numApply : numApplies) {
            total += (long) numApply.solutionValue();
        }

        return total;
    }

    private static boolean containsIndex(int[] move, int idx) {
        for (int i : move) if (i == idx) return true;
        return false;
    }

    private static int[] applyMove(final int[] state, final int[] move) {
        int[] newState = state.clone();
        for (int pos : move) {
            newState[pos]++;
        }
        return newState;
    }

    private static int[][] parseMoves(final String line) {
        String[] parts = line.split(" +");

        int[][] moves = new int[parts.length - 2][];

        for (int i = 1; i < parts.length - 1; i++) {
            String moveStr = parts[i];
            int[] move = parseMove(moveStr);
            moves[i - 1] = move;
        }
        return moves;
    }

    private static int[] parseMove(final String moveStr) {
        String[] moveParts = moveStr.split("[(),]");

        int[] move = new int[moveParts.length - 1];
        for (int i = 1; i < moveParts.length; i++) {
            final String movePart = moveParts[i];
            int pos = Integer.parseInt(movePart);
            move[i - 1] = pos;
        }
        return move;
    }

    private static int[] parseTargetState(final String line) {
        String[] parts = line.split("[{}]");

        String[] currParts = parts[1].split(",");
        int[] targetState = new int[currParts.length];
        for (int i = 0; i < currParts.length; i++) {
            targetState[i] = Integer.parseInt(currParts[i]);
        }
        return targetState;
    }

    private record State(int cost, int[] state) implements Comparable<State> {
        @Override
        public int compareTo(State o) {
            return Integer.compare(this.cost, o.cost);
        }
    }
}
