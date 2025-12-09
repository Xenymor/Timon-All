package Puzzles.AdventOfCode.AOC2025.Day9;

import StandardClasses.Vectors.Vector2I;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Day9Task2 {
    static void main() throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/Puzzles/AdventOfCode/AOC2025/Day9/input.txt"));

        Set<Vector2I> redSquares = new HashSet<>();
        Set<Vector2I> greenSquares = new HashSet<>();
        Set<Vector2I> outSideSquares = new HashSet<>();

        Vector2I prevPoint = null;

        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        Vector2I minXPoint = null;

        for (String line : lines) {
            final String[] split = line.split(",");
            Vector2I point = new Vector2I(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
            redSquares.add(point);

            if (point.getX() < minX) {
                minX = point.getX();
                minXPoint = point;
            }

            maxX = Math.max(maxX, point.getX());
            minY = Math.min(minY, point.getY());
            maxY = Math.max(maxY, point.getY());

            if (prevPoint != null) {
                // Fill in green squares between prevPoint and point
                int xStart = Math.min(prevPoint.getX(), point.getX());
                int xEnd = Math.max(prevPoint.getX(), point.getX());
                int yStart = Math.min(prevPoint.getY(), point.getY());
                int yEnd = Math.max(prevPoint.getY(), point.getY());

                for (int x = xStart; x <= xEnd; x++) {
                    for (int y = yStart; y <= yEnd; y++) {
                        greenSquares.add(new Vector2I(x, y));
                    }
                }
            }

            prevPoint = point;
        }

        if (prevPoint != null) {
            final String[] split = lines.getFirst().split(",");
            Vector2I point = new Vector2I(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
            // Fill in green squares between prevPoint and point
            int xStart = Math.min(prevPoint.getX(), point.getX());
            int xEnd = Math.max(prevPoint.getX(), point.getX());
            int yStart = Math.min(prevPoint.getY(), point.getY());
            int yEnd = Math.max(prevPoint.getY(), point.getY());

            for (int x = xStart; x <= xEnd; x++) {
                for (int y = yStart; y <= yEnd; y++) {
                    final Vector2I p = new Vector2I(x, y);
                    greenSquares.add(p);
                }
            }
        }

        System.out.println("Starting area fill...");

        Queue<Vector2I> toProcess = new ArrayDeque<>();
        toProcess.add(new Vector2I(minXPoint.getX()-1, minXPoint.getY()));
        outSideSquares.add(new Vector2I(minX - 1, minY - 1));

        boolean reachedEdge = false;

        while (!toProcess.isEmpty()) {
            Vector2I curr = toProcess.poll();

            List<Vector2I> neighbors = List.of(
                    new Vector2I(curr.getX() + 1, curr.getY()),
                    new Vector2I(curr.getX() - 1, curr.getY()),
                    new Vector2I(curr.getX(), curr.getY() + 1),
                    new Vector2I(curr.getX(), curr.getY() - 1)
            );

            boolean oneEdge = false;
            for (Vector2I neighbor : neighbors) {
                if (redSquares.contains(neighbor) || greenSquares.contains(neighbor)) {
                    oneEdge = true;
                    break;
                }
            }

            if (oneEdge && !reachedEdge) {
                System.out.println("Reached edge of shape...");
                reachedEdge = true;
            }

            if (reachedEdge && !oneEdge) {
                continue;
            }

            for (Vector2I neighbor : neighbors) {
                final int x = neighbor.getX();
                final int y = neighbor.getY();
                if (x < minX - 1 || x > maxX + 1 ||
                        y < minY - 1 || y > maxY + 1) {
                    continue;
                }
                if (redSquares.contains(neighbor) || greenSquares.contains(neighbor)) {
                    continue;
                }
                if (outSideSquares.contains(neighbor)) {

                    continue;
                }
                outSideSquares.add(neighbor);
                toProcess.add(neighbor);
            }

        }

        System.out.println("Area fill complete. Calculating largest rectangle...");

        final long largestRect = getLargestRect(redSquares, outSideSquares);

        System.out.println("Result: " + largestRect);
    }

    private static long getLargestRect(final Set<Vector2I> redSquares, final Set<Vector2I> outsideSquares) {
        final PriorityQueue<Rect> pq = getSortedRects(redSquares);

        System.out.println("Total rectangles to check: " + pq.size());

        long largestRect = 0;

        while (!pq.isEmpty()) {
            Rect rect = pq.poll();

            long area = rect.area;

            Vector2I red1 = rect.a;
            Vector2I red2 = rect.b;

            int xStart = Math.min(red1.getX(), red2.getX());
            int xEnd = Math.max(red1.getX(), red2.getX());
            int yStart = Math.min(red1.getY(), red2.getY());
            int yEnd = Math.max(red1.getY(), red2.getY());

            if (area > largestRect) {
                boolean allValid = true;
                for (int x = xStart; x <= xEnd; x++) {
                    Vector2I a = new Vector2I(x, yStart);
                    Vector2I b = new Vector2I(x, yEnd);
                    if (outsideSquares.contains(a) || outsideSquares.contains(b)) {
                        allValid = false;
                        break;
                    }

                }
                if (!allValid) continue;
                for (int y = yStart; y <= yEnd; y++) {
                    Vector2I a = new Vector2I(xStart, y);
                    Vector2I b = new Vector2I(xEnd, y);
                    if (outsideSquares.contains(a) || outsideSquares.contains(b)) {
                        allValid = false;
                        break;
                    }
                }

                if (allValid) {
                    largestRect = area;
                }

            }

        }
        return largestRect;
    }

    private static PriorityQueue<Rect> getSortedRects(final Set<Vector2I> redSquares) {
        List<Vector2I> redList = new ArrayList<>(redSquares);

        PriorityQueue<Rect> pq = new PriorityQueue<>();

        for (int i = 0; i < redList.size(); i++) {
            final Vector2I point = redList.get(i);
            for (int j = i + 1; j < redList.size(); j++) {
                final Vector2I other = redList.get(j);
                long area = (long) (Math.abs(point.getX() - other.getX()) + 1) * (Math.abs(point.getY() - other.getY()) + 1);
                pq.add(new Rect(point, other, area));
            }
        }
        return pq;
    }

    private record Rect(Vector2I a, Vector2I b, long area) implements Comparable<Rect> {
        @Override
        public int compareTo(Rect o) {
            return Long.compare(o.area, this.area); // Absteigend sortieren
        }
    }
}

