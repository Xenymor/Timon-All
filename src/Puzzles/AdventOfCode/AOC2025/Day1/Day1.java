package Puzzles.AdventOfCode.AOC2025.Day1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Day1 {
    public static void main(String[] args) throws IOException {
        List<String> input = Files.readAllLines(Path.of("src/Puzzles/AdventOfCode/AOC2025/Day1/input.txt"));

        int position = 50;
        int password = 0;

        for (String line : input) {
            int dir = line.charAt(0) == 'R' ? 1 : -1;
            int value = Integer.parseInt(line.substring(1));

            for (int i = 0; i < value; i++) {
                position += dir;
                if (position % 100 == 0) {
                    password++;
                }
            }
        }

        System.out.println("Password is: " + password);
    }
}
