package Puzzles.AdventOfCode.AOC2025.Day12;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Day12Task1 {
    static void main() throws IOException {
        List<String> input = Files.readAllLines(Path.of("src/Puzzles/AdventOfCode/AOC2025/Day12/input.txt"));

        List<Shape> shapes = parseShapes(input);
        List<Task> tasks = parseTasks(input);

        int solvableCount = 0;
        for (Task task : tasks) {
            int area = getMinArea(task.counts, shapes);
            if (area >= task.width * task.height) {
                continue;
            }
            boolean solvable = solve(task, shapes, new long[task.height], 0);
            if (solvable) {
                solvableCount++;
            }
        }

        System.out.println("Solvable tasks: " + solvableCount);
    }

    private static boolean solve(final Task task, final List<Shape> shapes, final long[] state, int shapeIndex) {
        if (shapeIndex >= task.counts.length) {
            return true;
        }
        final Shape shape = shapes.get(shapeIndex);
        for (int rotation = 0; rotation < 4; rotation++) {
            Shape rotatedShape = shape.rotate(rotation);
            final int maxY = task.height - rotatedShape.getHeight();
            final int maxX = task.width - rotatedShape.getWidth();
            for (int y = 0; y <= maxY; y++) {
                for (int x = 0; x <= maxX; x++) {
                    final long[] bitSetShape = rotatedShape.getShape();
                    if (canPlaceShape(state, bitSetShape, x, y)) {
                        placeShape(state, bitSetShape, x, y);
                        if (solve(task, shapes, state, shapeIndex + 1)) {
                            return true;
                        }
                        removeShape(state, bitSetShape, x, y);
                    }
                }
            }
        }
        return false;
    }

    private static void removeShape(final long[] state, final long[] shape, final int x, final int y) {
        for (int i = 0; i < shape.length; i++) {
            long shapeLine = shape[i] << x;
            state[y + i] &= ~shapeLine;
        }
    }

    private static void placeShape(final long[] state, final long[] shape, final int x, final int y) {
        for (int i = 0; i < shape.length; i++) {
            long shapeLine = shape[i] << x;
            state[y + i] |= shapeLine;
        }
    }

    private static boolean canPlaceShape(final long[] state, final long[] shape, final int x, final int y) {
        for (int i = 0; i < shape.length; i++) {
            long shapeLine = shape[i] << x;
            if ((state[y + i] & shapeLine) != 0) {
                return false;
            }
        }
        return true;
    }

    private static int getMinArea(final int[] counts, final List<Shape> shapes) {
        int area = 0;
        for (int i = 0; i < counts.length; i++) {
            final Shape shape = shapes.get(i);
            area += counts[i] * shape.area;
        }
        return area;
    }

    private static List<Task> parseTasks(final List<String> input) {
        List<Task> tasks = new ArrayList<>();
        for (String line : input) {
            if (line.matches("[0-9]+x[0-9]+:.+")) {
                String[] parts = line.split(": *");
                String[] dimensions = parts[0].split("x");
                int width = Integer.parseInt(dimensions[0]);
                int height = Integer.parseInt(dimensions[1]);
                int[] counts = parseCounts(parts[1]);
                tasks.add(new Task(width, height, counts));
            }
        }
        return tasks;
    }

    private static int[] parseCounts(final String part) {
        String[] countStrings = part.split(" +");
        int[] counts = new int[countStrings.length];
        for (int i = 0; i < countStrings.length; i++) {
            counts[i] = Integer.parseInt(countStrings[i]);
        }
        return counts;
    }

    private static List<Shape> parseShapes(final List<String> input) {
        List<Shape> shapes = new ArrayList<>();
        int lastStart = 0;
        for (int i = 0; i < input.size(); i++) {
            if (input.get(i).isBlank()) {
                shapes.add(parseShape(input.subList(lastStart, i)));
                lastStart = i + 1;
            }
            if (input.get(i).matches("[0-9]+x[0-9]+:[.]+")) {
                break;
            }
        }
        return shapes;
    }

    private static Shape parseShape(final List<String> lines) {
        long[] shape = new long[lines.size()-1];
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            for (int j = 0; j < line.length(); j++) {
                shape[i-1] |= (line.charAt(j) == '#' ? 1L : 0L) << (j);
            }
        }
        return new Shape(shape);
    }

    private record Task(int width, int height, int[] counts) {
    }
}
