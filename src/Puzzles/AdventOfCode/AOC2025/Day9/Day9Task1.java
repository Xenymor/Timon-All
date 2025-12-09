package Puzzles.AdventOfCode.AOC2025.Day9;

import StandardClasses.Vectors.Vector2I;
import StandardClasses.Vectors.Vector2L;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Day9Task1 {
    static void main() throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/Puzzles/AdventOfCode/AOC2025/Day9/input.txt"));

        Vector2L[] points = new Vector2L[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            final String[] split = line.split(",");
            points[i] = new Vector2L(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        }

        long largestRect = 0;
        for (var point : points) {
            for (var other : points) {
                if (point == other) continue;
                long area = (Math.abs(point.getX() - other.getX())+1) * (Math.abs(point.getY() - other.getY())+1);
                largestRect = Math.max(largestRect, area);
            }
        }

        System.out.println("Result: " + largestRect);
    }
}
