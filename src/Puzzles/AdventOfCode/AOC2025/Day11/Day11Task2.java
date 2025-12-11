package Puzzles.AdventOfCode.AOC2025.Day11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Day11Task2 {
    static void main() throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/Puzzles/AdventOfCode/AOC2025/Day11/input.txt"));

        Map<String, Integer> nameToIndex = new HashMap<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String name = line.split(": ")[0];
            nameToIndex.put(name, i);
        }
        nameToIndex.put("out", lines.size());

        int nodeCount = lines.size() + 1;

        List<List<Integer>> adjacencyMatrix = new ArrayList<>();
        for (String line : lines) {
            List<Integer> edgeList = new ArrayList<>();
            String[] parts = line.split("[: ]+");
            for (int j = 1; j < parts.length; j++) {
                final String part = parts[j];
                int destIndex = nameToIndex.get(part);
                edgeList.add(destIndex);
            }
            adjacencyMatrix.add(edgeList);
        }

        List<Integer> order = topologicalSort(adjacencyMatrix);

        int start = nameToIndex.get("svr"), end = nameToIndex.get("out");

        int first = nameToIndex.get("dac"), second = nameToIndex.get("fft");

        State[] states = new State[nodeCount];

        for (int i = 0; i < states.length; i++) {
            if (i == start) {
                states[i] = new State(1, 0, 0, 0);
            } else {
                states[i] = new State(0, 0, 0, 0);
            }
        }

        int startIndex = order.indexOf(start);
        int endIndex = order.indexOf(end);
        for (int i = startIndex; i < endIndex; i++) {
            int node = order.get(i);

            for (int neighbor : adjacencyMatrix.get(node)) {
                State currentState = states[node];
                State neighborState = states[neighbor];

                if (neighbor == first) {
                    neighborState.firstUsedCount += currentState.noneUsedCount;
                    neighborState.bothUsedCount += currentState.secondUsedCount;
                } else if (neighbor == second) {
                    neighborState.secondUsedCount += currentState.noneUsedCount;
                    neighborState.bothUsedCount += currentState.firstUsedCount;
                } else {
                    neighborState.noneUsedCount += currentState.noneUsedCount;
                    neighborState.firstUsedCount += currentState.firstUsedCount;
                    neighborState.secondUsedCount += currentState.secondUsedCount;
                    neighborState.bothUsedCount += currentState.bothUsedCount;
                }
            }
        }

        System.out.println("Result: " + states[end].bothUsedCount);
    }

    private static List<Integer> topologicalSort(List<List<Integer>> adjacencyList) {
        int n = adjacencyList.size() + 1;
        int[] inDegree = new int[n];

        for (List<Integer> neighbors : adjacencyList) {
            for (int neighbor : neighbors) {
                inDegree[neighbor]++;
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
            }
        }

        List<Integer> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            int node = queue.poll();
            result.add(node);

            if (node >= adjacencyList.size()) {
                continue;
            }

            List<Integer> edges = adjacencyList.get(node);
            for (final int neighbor : edges) {
                inDegree[neighbor]--;
                if (inDegree[neighbor] == 0) {
                    queue.add(neighbor);
                }
            }
        }

        if (result.size() != n) {
            throw new IllegalStateException("Graph contains cycles!");
        }

        return result;
    }

    private static final class State {
        long noneUsedCount;
        long firstUsedCount;
        long secondUsedCount;
        long bothUsedCount;

        private State(int noneUsedCount, int firstUsedCount, int secondUsedCount, int bothUsedCount) {
            this.noneUsedCount = noneUsedCount;
            this.firstUsedCount = firstUsedCount;
            this.secondUsedCount = secondUsedCount;
            this.bothUsedCount = bothUsedCount;
        }

    }
}
