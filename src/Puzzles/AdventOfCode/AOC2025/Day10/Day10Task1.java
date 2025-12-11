package Puzzles.AdventOfCode.AOC2025.Day10;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class Day10Task1 {
    static void main() throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/Puzzles/AdventOfCode/AOC2025/Day10/input.txt"));

        long result = 0;

        for (final String line : lines) {
            int targetState = parseTargetState(line);

            int[] moves = parseMoves(line);

            PriorityQueue<State> pq = new PriorityQueue<>();
            Set<State> visited = new HashSet<>();
            final State start = new State(0, 0);
            pq.add(start);
            visited.add(start);

            while (!pq.isEmpty()) {
                State current = pq.poll();

                if (current.state == targetState) {
                    result += current.cost;
                    break;
                }

                for (int move : moves) {
                    int newState = current.state ^ move;
                    State newEntry = new State(current.cost + 1, newState);
                    if (visited.contains(newEntry)) {
                        continue;
                    }
                    pq.add(newEntry);
                    visited.add(newEntry);
                }
            }
        }

        System.out.println("Result: " + result);

    }

    private static int[] parseMoves(final String line) {
        String[] parts = line.split(" +");

        int[] moves = new int[parts.length - 2];

        for (int i = 1; i < parts.length - 1; i++) {
            String moveStr = parts[i];
            int move = parseMove(moveStr);
            moves[i - 1] = move;
        }
        return moves;
    }

    private static int parseMove(final String moveStr) {
        String[] moveParts = moveStr.split("[(),]");

        int move = 0;
        for (int i = 1; i < moveParts.length; i++) {
            final String movePart = moveParts[i];
            int pos = Integer.parseInt(movePart);
            move |= (1 << pos);
        }
        return move;
    }

    private static int parseTargetState(final String line) {
        String[] parts = line.split("[\\[\\]]");

        String curr = parts[1];
        int targetState = 0;
        for (int i = 0; i < curr.length(); i++) {
            if (curr.charAt(i) == '#') {
                targetState |= (1 << i);
            } else if (curr.charAt(i) != '.') {
                throw new IllegalArgumentException("Unexpected character: " + curr.charAt(i));
            }
        }
        return targetState;
    }

    private record State(int cost, int state) implements Comparable<State> {
        @Override
        public int compareTo(State o) {
            return Integer.compare(this.cost, o.cost);
        }
    }
}
