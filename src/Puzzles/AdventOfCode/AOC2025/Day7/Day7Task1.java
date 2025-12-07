package Puzzles.AdventOfCode.AOC2025.Day7;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day7Task1 {
    static void main() throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/Puzzles/AdventOfCode/AOC2025/Day7/input.txt"));

        Set<Integer> currStage = new HashSet<>();
        Set<Integer> nextStage = new HashSet<>();

        currStage.add(lines.getFirst().indexOf('S'));

        int splits = 0;

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            for (int pos : currStage) {
                if (line.charAt(pos) == '^') {
                    splits++;
                    nextStage.add(pos - 1);
                    nextStage.add(pos + 1);
                } else {
                    nextStage.add(pos);
                }
            }
            Set<Integer> buff = currStage;
            currStage = nextStage;
            nextStage = buff;
            nextStage.clear();
        }

        System.out.println("Number of splits: " + splits);
    }
}
