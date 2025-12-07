package Puzzles.AdventOfCode.AOC2025.Day7;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day7Task2 {
    static void main() throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/Puzzles/AdventOfCode/AOC2025/Day7/input.txt"));

        Map<Integer, Long> currStage = new HashMap<>();
        Map<Integer, Long> nextStage = new HashMap<>();

        currStage.put(lines.getFirst().indexOf('S'), 1L);

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            for (Map.Entry<Integer, Long> e : currStage.entrySet()) {
                final Integer index = e.getKey();
                final Long count = e.getValue();
                if (line.charAt(index) == '^') {
                    nextStage.compute(index - 1, (_, v) -> (v == null) ? count : v + count);
                    nextStage.compute(index + 1, (_, v) -> (v == null) ? count : v + count);
                } else {
                    nextStage.compute(index, (_, v) -> (v == null) ? count : v + count);
                }
            }
            Map<Integer, Long> buff = currStage;
            currStage = nextStage;
            nextStage = buff;
            nextStage.clear();
        }

        long paths = 0;
        for (Map.Entry<Integer, Long> e : currStage.entrySet()) {
            paths += e.getValue();
        }

        System.out.println("Number of paths: " + paths);
    }
}
