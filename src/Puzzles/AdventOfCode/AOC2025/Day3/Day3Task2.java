package Puzzles.AdventOfCode.AOC2025.Day3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static Puzzles.AdventOfCode.AOC2025.Day2.Day2Task1.pow;

public class Day3Task2 {
    public static void main(String[] args) throws IOException {
        List<String> input = Files.readAllLines(Path.of("src/Puzzles/AdventOfCode/AOC2025/Day3/input.txt"));

        long sum = 0;
        for (final String line : input) {
            final long max = getMax(line, 0, 12);

            sum += max;
        }

        System.out.println(sum);
    }

    private static long getMax(final String line, final int startIndex, final int numLeft) {
        if (numLeft == 0) {
            return 0;
        }

        long max = 0;
        int highest = 0;
        List<Integer> indices = new ArrayList<>();

        final long pow = pow(10, numLeft - 1);

        for (int i = startIndex; i <= line.length() - numLeft; i++) {
            final int currDigit = line.charAt(i) - '0';
            if (currDigit == highest) {
                indices.add(i);
            } else if (currDigit > highest) {
                highest = currDigit;
                indices.clear();
                indices.add(i);
            }
        }

        for (Integer highestIndex : indices) {
            max = Math.max(max, highest * pow + getMax(line, highestIndex + 1, numLeft - 1));
        }

        return max;
    }
}
