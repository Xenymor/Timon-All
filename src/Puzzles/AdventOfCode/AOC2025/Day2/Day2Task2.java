package Puzzles.AdventOfCode.AOC2025.Day2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Day2Task2 {
    static long max = 0;

    public static void main(String[] args) throws IOException {
        List<String> line = Files.readAllLines(Path.of("src/Puzzles/AdventOfCode/AOC2025/Day2/input.txt"));
        String[] ranges = line.getFirst().split(",");

        TreeMap<Long, Long> merged = buildMergedRanges(ranges);

        long sum = 0;

        Set<Long> seen = new HashSet<>();

        long id = 1;
        final int length = String.valueOf(max).length();
        int idLength = String.valueOf(id).length();
        while (idLength*2 <= length) {
            final long pow = pow(10, idLength);
            for (int i = 2; i <= length / idLength; i++) {
                long num = id;
                for (int j = 1; j < i; j++) {
                    num *= pow;
                    num += id;
                }
                if (seen.contains(num)) {
                    continue;
                }
                seen.add(num);
                if (contains(merged, num)) {
                    sum += num;
                }
            }

            //Next id
            id++;
            idLength = String.valueOf(id).length();
        }

        System.out.println("Sum: " + sum);
    }

    private static long pow(final long b, final long e) {
        long r = 1;
        for (long i = 0; i < e; i++) {
            r *= b;
        }
        return r;
    }

    public static TreeMap<Long, Long> buildMergedRanges(String[] ranges) {

        final List<long[]> list = parseStrings(ranges);

        return fillMap(list);
    }

    private static List<long[]> parseStrings(final String[] ranges) {
        List<long[]> list = new ArrayList<>();
        for (String r : ranges) {
            String[] b = r.split("-");
            long l = Long.parseLong(b[0]);
            long u = Long.parseLong(b[1]);
            max = Math.max(max, u);
            list.add(new long[]{l, u});
        }
        list.sort(Comparator.comparingLong(a -> a[0]));
        return list;
    }

    private static TreeMap<Long, Long> fillMap(final List<long[]> list) {
        TreeMap<Long, Long> map = new TreeMap<>();
        for (long[] iv : list) {
            if (map.isEmpty()) {
                map.put(iv[0], iv[1]);
                continue;
            }
            Map.Entry<Long, Long> last = map.lastEntry();
            if (iv[0] <= last.getValue() + 1) { // überlappend oder direkt anliegend
                long newEnd = Math.max(last.getValue(), iv[1]);
                map.put(last.getKey(), newEnd);
            } else {
                map.put(iv[0], iv[1]);
            }
        }
        return map;
    }

    public static boolean contains(TreeMap<Long, Long> map, long x) {
        Map.Entry<Long, Long> e = map.floorEntry(x);
        return e != null && e.getValue() >= x;
    }
}
