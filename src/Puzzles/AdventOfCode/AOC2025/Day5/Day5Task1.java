package Puzzles.AdventOfCode.AOC2025.Day5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Day5Task1 {
    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/Puzzles/AdventOfCode/AOC2025/Day5/input.txt"));

        List<Range> ranges = new ArrayList<>();

        int idStart = -1;
        for (int i = 0; i < lines.size(); i++) {
            final String line = lines.get(i);
            if (line.isBlank()) {
                idStart = i + 1;
                break;
            }
            String[] parts = line.split("-");
            long start = Long.parseLong(parts[0]);
            long end = Long.parseLong(parts[1]);
            ranges.add(new Range(start, end));
        }

        ranges.sort(Comparator.naturalOrder());

        TreeMap<Long, Long> mergedRanges = mergeRanges(ranges);
        int count = 0;

        for (int i = idStart; i < lines.size(); i++) {
            final long id = Long.parseLong(lines.get(i));

            var floorEntry = mergedRanges.floorEntry(id);
            if (floorEntry != null && floorEntry.getKey() <= id && id <= floorEntry.getValue()) {
                count++;
            }
        }

        System.out.println("Count: " + count);
    }

    private static TreeMap<Long, Long> mergeRanges(final List<Range> ranges) {
        TreeMap<Long, Long> result = new TreeMap<>();

        for (Range range : ranges) {
            if (result.isEmpty()) {
                result.put(range.start, range.end);
                continue;
            }
            final Map.Entry<Long, Long> lastEntry = result.lastEntry();
            if (range.start <= lastEntry.getValue()) {
                if (range.end > lastEntry.getValue()) {
                    result.remove(lastEntry.getKey());
                    result.put(lastEntry.getKey(), range.end);
                }
            } else {
                result.put(range.start, range.end);
            }
        }

        return result;
    }

    record Range(long start, long end) implements Comparable<Range> {
        @Override
        public int compareTo(final Range o) {
            return Long.compare(this.start, o.start);
        }
    }

}
