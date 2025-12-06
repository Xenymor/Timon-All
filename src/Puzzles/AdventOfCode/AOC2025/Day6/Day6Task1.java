package Puzzles.AdventOfCode.AOC2025.Day6;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Day6Task1 {
    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/Puzzles/AdventOfCode/AOC2025/Day6/input.txt"));
        int[][] grid = new int[lines.getFirst().split(" +").length][lines.size() - 1];

        for (int y = 0; y < lines.size() - 1; y++) {
            String[] numbers = lines.get(y).split(" +");
            for (int x = 0; x < numbers.length; x++) {
                grid[x][y] = Integer.parseInt(numbers[x]);
            }
        }

        String[] instructions = lines.getLast().split(" +");

        long sum = 0;
        for (int x = 0; x < grid.length; x++) {
            boolean shouldAdd = instructions[x].equals("+");
            long curr = shouldAdd ? 0 : 1;
            for (int y = 0; y < grid[x].length; y++) {
                if (shouldAdd) {
                    curr += grid[x][y];
                } else {
                    curr *= grid[x][y];
                }
            }
            sum += curr;
        }

        System.out.println(sum);
    }
}
