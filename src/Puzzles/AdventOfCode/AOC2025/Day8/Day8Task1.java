package Puzzles.AdventOfCode.AOC2025.Day8;

import StandardClasses.Vectors.Vector3I;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Day8Task1 {
    static void main() throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/Puzzles/AdventOfCode/AOC2025/Day8/input.txt"));

        Vector3I[] points = new Vector3I[lines.size()];
        Map<Vector3I, Integer> pointToIndex = new HashMap<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            final String[] split = line.split(",");
            final Vector3I curr = new Vector3I(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
            points[i] = curr;
            pointToIndex.put(curr, i);
        }

        PriorityQueue<Entry> pq = new PriorityQueue<>();
        for (int i = 0; i < points.length; i++) {
            final Vector3I point = points[i];
            for (int j = i+1; j < points.length; j++) {
                final Vector3I other = points[j];
                if (point == other) continue;
                pq.add(new Entry(point, other, point.getDist(other)));
            }
        }

        UnionFind uf = new UnionFind(points.length);
        for (int i = 0; i < 1000; i++) {
            Entry entry = pq.poll();
            assert entry != null;
            uf.union(pointToIndex.get(entry.a), pointToIndex.get(entry.b));
        }

        Set<Integer> uniqueParents = new HashSet<>();
        for (int i = 0; i < points.length; i++) {
            uniqueParents.add(uf.find(i));
        }
        System.out.println(uniqueParents.size());

        PriorityQueue<Integer> greatest = new PriorityQueue<>(Comparator.reverseOrder());
        long sum = 0;
        for (Integer parent : uniqueParents) {
            final int x = uf.size[parent];
            greatest.add(x);
            sum += x;
        }

        System.out.println("Sum: " + sum);

        long result = 1;
        for (int i = 0; i < 3; i++) {
            Integer size = greatest.poll();
            result *= size;
        }

        System.out.println("Result: " + result);
    }

    private record Entry(Vector3I a, Vector3I b, double dist) implements Comparable<Entry> {
        @Override
        public int compareTo(Entry o) {
            return Double.compare(this.dist, o.dist);
        }
    }
}
