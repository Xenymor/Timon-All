package Puzzles.AdventOfCode.AOC2024.Day1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Day1 {
    public static void main(String[] args) throws IOException {
        task1();
        task2();
    }

    private static void task2() throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/Puzzles/AdventOfCode/AOC2024/Day1/input.txt"));

        int[] first = new int[100_000];
        int[] second = new int[100_000];

        for (final String line : lines) {
            String[] parts = line.split(" {3}");
            int a = Integer.parseInt(parts[0]);
            int b = Integer.parseInt(parts[1]);
            first[a]++;
            second[b]++;
        }

        int indexA = 0;
        int count = 0;
        int similarity = 0;
        while (count < lines.size()) {
            if (first[indexA] > 0) {
                count++;
                similarity += indexA * second[indexA];
            }
            indexA++;
        }

        System.out.println(similarity);
    }

    private static void task1() throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/Puzzles/AdventOfCode/AOC2024/Day1/input.txt"));

        int[] first = new int[100_000];
        int[] second = new int[100_000];

        for (final String line : lines) {
            String[] parts = line.split(" {3}");
            int a = Integer.parseInt(parts[0]);
            int b = Integer.parseInt(parts[1]);
            first[a]++;
            second[b]++;
        }

        int indexA = 0;
        int indexB = 0;
        int count = 0;
        int dist = 0;
        while (count < lines.size()) {
            if (first[indexA] == 0) {
                indexA++;
                continue;
            }
            if (second[indexB] == 0) {
                indexB++;
                continue;
            }
            dist += Math.abs(indexA - indexB);
            first[indexA]--;
            second[indexB]--;
            count++;
        }

        System.out.println(dist);
    }
}
