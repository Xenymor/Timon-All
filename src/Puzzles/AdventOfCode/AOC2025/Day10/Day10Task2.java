package Puzzles.AdventOfCode.AOC2025.Day10;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Day10Task2 {
    static void main() throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/Puzzles/AdventOfCode/AOC2025/Day10/input.txt"));

        long result = 0;

        for (int i = 0; i < lines.size(); i++) {
            System.out.println("Processing line " + (i + 1) + "/" + lines.size());
            final String line = lines.get(i);
            int[] targetState = parseTargetState(line);

            int[][] moves = parseMoves(line);

            PriorityQueue<State> pq = new PriorityQueue<>();
            Set<String> visited = new HashSet<>();
            final State start = new State(0, new int[targetState.length]);
            pq.add(start);
            visited.add(Arrays.toString(start.state));

            outer:
            while (!pq.isEmpty()) {
                State current = pq.poll();

                if (Arrays.equals(current.state, targetState)) {
                    result += current.cost;
                    break;
                }

                for (int[] move : moves) {
                    int[] newState = applyMove(current.state, move);
                    final String key = Arrays.toString(newState);
                    if (visited.contains(key)) {
                        continue;
                    }
                    boolean isValid = true;
                    for (int j = 0; j < newState.length; j++) {
                        if (newState[j] > targetState[j]) {
                            isValid = false;
                            break;
                        }
                    }
                    if (!isValid) {
                        continue;
                    }

                    if (Arrays.equals(newState, targetState)) {
                        result += current.cost + 1;
                        break outer;
                    }

                    pq.add(new State(current.cost + 1, newState));
                    visited.add(key);
                }
            }
            System.out.println("Current result: " + result);
        }

        System.out.println("Result: " + result);

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
