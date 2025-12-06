package Puzzles.AdventOfCode.AOC2025.Day6;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Day6Task2 {
    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/Puzzles/AdventOfCode/AOC2025/Day6/input.txt"));
        int taskCount = lines.getFirst().split(" +").length;

        List<List<Integer>> grid = parseGrid(lines.subList(0, lines.size() - 1), taskCount);

        String[] instructions = lines.getLast().split(" +");

        long sum = 0;
        for (int x = 0; x < grid.size(); x++) {
            final List<Integer> task = grid.get(x);
            boolean shouldAdd = instructions[x].equals("+");
            long curr = shouldAdd ? 0 : 1;

            for (final Integer num : task) {
                if (shouldAdd) {
                    curr += num;
                } else {
                    curr *= num;
                }
            }
            sum += curr;
        }

        System.out.println(sum);
    }

    private static List<List<Integer>> parseGrid(final List<String> input, final int taskCount) {
        char[][] chars = input.stream()
                .map(String::toCharArray)
                .toArray(char[][]::new);

        List<List<Integer>> result = new ArrayList<>();

        int index = 0;

        for (int i = 0; i < taskCount; i++) {
            List<Integer> nums = new ArrayList<>();
            //Task i
            for (; ; index++) {
                if (index >= chars[0].length) {
                    break;
                }
                StringBuilder currNum = new StringBuilder();
                boolean allEmpty = true;
                for (final char[] aChar : chars) {
                    final char c = aChar[index];
                    if (c != ' ') {
                        currNum.append(c);
                        allEmpty = false;
                    }
                }
                if (allEmpty) {
                    index++;
                    break;
                } else {
                    nums.add(Integer.parseInt(currNum.toString()));
                }
            }
            result.add(nums);
        }

        return result;
    }
}
