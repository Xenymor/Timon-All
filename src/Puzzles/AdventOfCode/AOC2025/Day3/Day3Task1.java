package Puzzles.AdventOfCode.AOC2025.Day3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Day3Task1 {
    public static void main(String[] args) throws IOException {
        List<String> input = Files.readAllLines(Path.of("src/Puzzles/AdventOfCode/AOC2025/Day3/input.txt"));

        long sum = 0;
        for (String line : input) {
            int max = 0;

            for (int i = 0; i < line.length(); i++) {
                for (int j = i+1; j < line.length(); j++) {
                    final int currNum = Integer.parseInt(line.charAt(i) + "" + line.charAt(j));
                    max = Math.max(max, currNum);
                }
            }

            sum += max;
        }

        System.out.println(sum);
    }
}
