package Puzzles.AdventOfCode.AOC2025.Day11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Day11Task1 {
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
        int[] pathCount = new int[nodeCount];
        pathCount[nameToIndex.get("you")] = 1;
        for (int node : order) {
            if (node >= adjacencyMatrix.size()) {
                continue;
            }
            for (int neighbor : adjacencyMatrix.get(node)) {
                pathCount[neighbor] += pathCount[node];
            }
        }

        System.out.println("Result: " + pathCount[nameToIndex.get("out")]);
    }

    private static List<Integer> topologicalSort(List<List<Integer>> adjacencyList) {
        int n = adjacencyList.size()+1;
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

}
