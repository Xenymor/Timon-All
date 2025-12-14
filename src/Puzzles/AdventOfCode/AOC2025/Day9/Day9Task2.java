package Puzzles.AdventOfCode.AOC2025.Day9;

import StandardClasses.Vectors.Vector2I;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

public class Day9Task2 {
    static void main() throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/Puzzles/AdventOfCode/AOC2025/Day9/input.txt"));

        GeneralPath polygon = new GeneralPath();
        Set<Vector2I> redSquares = new HashSet<>();

        for (int i = 0; i < lines.size(); i++) {
            final String line = lines.get(i);
            final String[] split = line.split(",");
            Vector2I point = new Vector2I(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
            redSquares.add(point);

            if (i == 0) {
                polygon.moveTo(point.getX(), point.getY());
            } else {
                polygon.lineTo(point.getX(), point.getY());
            }
        }

        polygon.closePath();

        long largestRect = 0;

        for (Vector2I a : redSquares) {
            for (Vector2I b : redSquares) {
                if (a.equals(b)) {
                    continue;
                }
                Rectangle2D rect = new Rectangle(
                        Math.min(a.getX(), b.getX()),
                        Math.min(a.getY(), b.getY()),
                        Math.abs(a.getX() - b.getX()) + 1,
                        Math.abs(a.getY() - b.getY()) + 1
                );
                if (polygon.contains(rect)) {
                    largestRect = Math.max(largestRect, (long) rect.getWidth() * (long) rect.getHeight());
                }
            }
        }

        System.out.println("Largest Rectangle Area: " + largestRect);
    }

}

