package Puzzles.AdventOfCode.AOC2025.Day4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Day4Task1 {
    public static void main(String[] args) throws IOException {
        List<String> input = Files.readAllLines(Path.of("src/Puzzles/AdventOfCode/AOC2025/Day4/input.txt"));

        int width = input.getFirst().length();
        int height = input.size();

        int moveableCount = 0;

        for (int y = 0; y < height; y++) {
            final String curr = input.get(y);
            for (int x = 0; x < width; x++) {
                if (curr.charAt(x) != '@') {
                    continue;
                }
                int count = 0;
                for (int dy = -1; dy <= 1; dy++) {
                    int newY = y + dy;
                    if (newY < 0 || newY >= height) continue;
                    String line = input.get(newY);
                    for (int dx = -1; dx <= 1; dx++) {
                        int newX = x + dx;
                        if (newX < 0 || newX >= width) continue;
                        if (dx == 0 && dy == 0) continue;
                        if (line.charAt(newX) == '@') {
                            count++;
                        }
                    }
                    if (count > 3) {
                        break;
                    }
                }
                if (count <= 3) {
                    moveableCount++;
                }
            }
        }

        System.out.println(moveableCount);
    }
}
