package Puzzles.AdventOfCode.AOC2025.Day4;

import StandardClasses.Vector2I;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Day4Task2 {
    public static void main(String[] args) throws IOException {
        List<String> input = Files.readAllLines(Path.of("src/Puzzles/AdventOfCode/AOC2025/Day4/input.txt"));

        int width = input.getFirst().length();
        int height = input.size();

        boolean[][] field = new boolean[height][width];

        Queue<Vector2I> toCheck = new ArrayDeque<>();
        Set<Vector2I> inQueue = new HashSet<>();

        for (int y = 0; y < height; y++) {
            final String curr = input.get(y);
            for (int x = 0; x < width; x++) {
                final boolean b = curr.charAt(x) == '@';
                field[x][y] = b;
                if (b) {
                    final Vector2I v = new Vector2I(x, y);
                    if (inQueue.contains(v)) continue;
                    toCheck.add(v);
                    inQueue.add(v);
                }
            }
        }

        System.out.println("Finished setup; Queue size: " + toCheck.size());

        int removableCount = 0;
        while (!toCheck.isEmpty()) {
            Vector2I curr = toCheck.poll();
            inQueue.remove(curr);

            int neighbourCount = getNeighbourCount(field, curr);

            if (neighbourCount <= 3) {
                removableCount++;
                field[curr.getX()][curr.getY()] = false;
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        if (dx == 0 && dy == 0) continue;
                        final int newX = dx + curr.getX();
                        final int newY = dy + curr.getY();
                        if (newX >= 0 && newX < field.length && newY >= 0 && newY < field[newX].length && field[newX][newY]) {
                            Vector2I neighbour = new Vector2I(newX, newY);
                            if (!inQueue.contains(neighbour)) {
                                toCheck.add(neighbour);
                                inQueue.add(neighbour);
                            }
                        }
                    }
                }
            }
        }
        System.out.println(removableCount);
    }

    private static int getNeighbourCount(final boolean[][] field, final Vector2I curr) {
        int count = 0;
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                final int newX = curr.getX() + dx;
                final int newY = curr.getY() + dy;
                if (newX > 0 && newX < field.length && newY > 0 && newY < field[0].length && field[newX][newY]) {
                    count++;
                }
            }
        }
        return count;
    }
}
