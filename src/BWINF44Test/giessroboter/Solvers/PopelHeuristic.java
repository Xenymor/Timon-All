package BWINF44Test.giessroboter.Solvers;

import BWINF44Test.giessroboter.Problem;
import BWINF44Test.giessroboter.Solution;

import java.awt.Point;
import java.util.*;

public class PopelHeuristic {
    public static Solution solve(Problem problem) {
        Map<Integer, List<Integer>> possiblePairs = new HashMap<>();
        int[] possibilityCounts = new int[problem.trees.size()];

        List<Point> trees = problem.trees;
        for (int i = 0; i < trees.size(); i++) {
            Point first = trees.get(i);
            for (int j = i + 1; j < trees.size(); j++) {
                Point second = trees.get(j);
                if (first.distance(second) <= 0.5 * problem.maxReach) {
                    possiblePairs.computeIfAbsent(i, _ -> new ArrayList<>()).add(j);
                    possiblePairs.computeIfAbsent(j, _ -> new ArrayList<>()).add(i);
                    possibilityCounts[i]++;
                    possibilityCounts[j]++;
                }
            }
        }

        PriorityQueue<IntInt> pq = new PriorityQueue<>();
        for (int i = 0; i < problem.trees.size(); i++) {
            pq.add(new IntInt(possibilityCounts[i], i));
        }
        BitSet used = new BitSet(problem.trees.size());

        int usedCount = 0;
        List<List<Integer>> solution = new ArrayList<>();

        while (!pq.isEmpty() && usedCount < problem.trees.size()) {
            var entry = pq.poll();
            int treeIndex = entry.index;
            if (used.get(treeIndex)) {
                continue;
            }
            Point tree = problem.trees.get(treeIndex);
            List<Integer> cycle = greedyCycle4(treeIndex, tree, problem, used);
            for (Integer cycleTree : cycle) {
                if (!used.get(cycleTree)) {
                    used.set(cycleTree);
                    usedCount++;
                    if (possiblePairs.containsKey(cycleTree)) {
                        for (Integer possiblePartner : possiblePairs.get(cycleTree)) {
                            possibilityCounts[possiblePartner]--;
                            pq.add(new IntInt(possibilityCounts[possiblePartner], possiblePartner));
                        }
                    }
                } else {
                    System.out.println("Alarm");
                }
            }
            solution.add(cycle);
        }
        return new Solution(convertToPoints(solution, problem), problem);
    }

    public static Solution solve2(Problem problem) {
        List<List<Integer>> fixed = new ArrayList<>();

        List<List<Integer>> bestSolution = null;
        int bestSize = Integer.MAX_VALUE;

        while (true) {
            Map<Integer, List<Integer>> possiblePairs = new HashMap<>();
            int[] possibilityCounts = new int[problem.trees.size()];

            List<Point> trees = problem.trees;
            for (int i = 0; i < trees.size(); i++) {
                Point first = trees.get(i);
                for (int j = i + 1; j < trees.size(); j++) {
                    Point second = trees.get(j);
                    if (first.distance(second) <= 0.5 * problem.maxReach) {
                        possiblePairs.computeIfAbsent(i, _ -> new ArrayList<>()).add(j);
                        possiblePairs.computeIfAbsent(j, _ -> new ArrayList<>()).add(i);
                        possibilityCounts[i]++;
                        possibilityCounts[j]++;
                    }
                }
            }

            PriorityQueue<IntInt> pq = new PriorityQueue<>();
            for (int i = 0; i < problem.trees.size(); i++) {
                pq.add(new IntInt(possibilityCounts[i], i));
            }
            BitSet used = new BitSet(problem.trees.size());

            int usedCount = 0;
            List<List<Integer>> solution = new ArrayList<>();

            for (List<Integer> cycle : fixed) {
                solution.add(new ArrayList<>(cycle));
                for (Integer treeIndex : cycle) {
                    used.set(treeIndex);
                    usedCount++;
                }
            }

            while (!pq.isEmpty() && usedCount < problem.trees.size()) {
                var entry = pq.poll();
                int treeIndex = entry.index;
                if (used.get(treeIndex)) {
                    continue;
                }
                Point tree = problem.trees.get(treeIndex);
                List<Integer> cycle = greedyCycle4(treeIndex, tree, problem, used);
                for (Integer cycleTree : cycle) {
                    if (!used.get(cycleTree)) {
                        used.set(cycleTree);
                        usedCount++;
                        if (possiblePairs.containsKey(cycleTree)) {
                            for (Integer possiblePartner : possiblePairs.get(cycleTree)) {
                                possibilityCounts[possiblePartner]--;
                                pq.add(new IntInt(possibilityCounts[possiblePartner], possiblePartner));
                            }
                        }
                    } else {
                        System.out.println("Alarm");
                    }
                }
                solution.add(cycle);
            }

            if (solution.size() <= bestSize) {
                bestSize = solution.size();
                bestSolution = solution;
            } else {
                break;
            }

            List<Integer> optimizedCycle = enhanceCycle(new ArrayList<>(solution.getLast()), problem, fixed);
            if (optimizedCycle.size() > solution.getLast().size()) {
                fixed.add(optimizedCycle);
            } else {
                break;
            }
        }
        return new Solution(convertToPoints(bestSolution, problem), problem);
    }

    public static Solution solve3(Problem problem) {
        List<List<Integer>> fixed = new ArrayList<>();

        List<List<Integer>> bestSolution = null;
        int bestSize = Integer.MAX_VALUE;

        while (true) {
            Map<Integer, List<Integer>> possiblePairs = new HashMap<>();
            int[] possibilityCounts = new int[problem.trees.size()];

            List<Point> trees = problem.trees;
            for (int i = 0; i < trees.size(); i++) {
                Point first = trees.get(i);
                for (int j = i + 1; j < trees.size(); j++) {
                    Point second = trees.get(j);
                    if (first.distance(second) <= 0.5 * problem.maxReach) {
                        possiblePairs.computeIfAbsent(i, _ -> new ArrayList<>()).add(j);
                        possiblePairs.computeIfAbsent(j, _ -> new ArrayList<>()).add(i);
                        possibilityCounts[i]++;
                        possibilityCounts[j]++;
                    }
                }
            }

            PriorityQueue<IntInt> pq = new PriorityQueue<>();
            for (int i = 0; i < problem.trees.size(); i++) {
                pq.add(new IntInt(possibilityCounts[i], i));
            }
            BitSet used = new BitSet(problem.trees.size());

            int usedCount = 0;
            List<List<Integer>> solution = new ArrayList<>();

            for (List<Integer> cycle : fixed) {
                solution.add(new ArrayList<>(cycle));
                for (Integer treeIndex : cycle) {
                    used.set(treeIndex);
                    usedCount++;
                }
            }

            while (!pq.isEmpty() && usedCount < problem.trees.size()) {
                var entry = pq.poll();
                int treeIndex = entry.index;
                if (used.get(treeIndex)) {
                    continue;
                }
                Point tree = problem.trees.get(treeIndex);
                List<Integer> cycle = greedyCycle4(treeIndex, tree, problem, used);
                for (Integer cycleTree : cycle) {
                    if (!used.get(cycleTree)) {
                        used.set(cycleTree);
                        usedCount++;
                        if (possiblePairs.containsKey(cycleTree)) {
                            for (Integer possiblePartner : possiblePairs.get(cycleTree)) {
                                possibilityCounts[possiblePartner]--;
                                pq.add(new IntInt(possibilityCounts[possiblePartner], possiblePartner));
                            }
                        }
                    } else {
                        System.out.println("Alarm");
                    }
                }
                solution.add(cycle);
            }

            if (solution.size() <= bestSize) {
                bestSize = solution.size();
                bestSolution = solution;
            } else {
                break;
            }

            boolean improved = false;
            for (int i = solution.size() - 1; i >= 0; i--) {
                final List<Integer> originalCycle = solution.get(i);
                List<Integer> optimizedCycle = enhanceCycle(new ArrayList<>(originalCycle), problem, fixed);
                if (optimizedCycle.size() > originalCycle.size()) {
                    fixed.add(optimizedCycle);
                    improved = true;
                    break;
                }
            }

            if (!improved) {
                break;
            }

        }
        return new Solution(convertToPoints(bestSolution, problem), problem);
    }

    private static List<Integer> enhanceCycle(final List<Integer> cycle, final Problem problem, final List<List<Integer>> fixed) {
        Set<Integer> used = new HashSet<>();
        for (List<Integer> fixedCycle : fixed) {
            used.addAll(fixedCycle);
        }
        used.addAll(cycle);

        double cycleLength = 0;

        for (int i = 0; i < cycle.size(); i++) {
            Point p1 = problem.trees.get(cycle.get(i));
            Point p2 = problem.trees.get(cycle.get((i + 1) % cycle.size()));
            cycleLength += p1.distance(p2);
        }

        boolean isOptimized = false;
        while (cycleLength <= problem.maxReach) {
            Entry best = null;

            for (int i = 0; i < problem.trees.size(); i++) {
                if (used.contains(i)) {
                    continue;
                }
                Entry curr = getBestLengthChange(cycle, problem, problem.trees.get(i), i);
                if (best == null || curr.distance < best.distance) {
                    best = curr;
                }
            }

            if (best == null || cycleLength + best.distance > problem.maxReach) {
                if (isOptimized) {
                    return cycle;
                } else {
                    isOptimized = true;
                    cycleLength = optimizeCycle(problem, cycle, cycleLength);
                    if (cycleLength < 0) {
                        return cycle;
                    }
                    continue;
                }
            }

            isOptimized = false;
            if (best.toIndex == cycle.size()-1) {
                cycle.add(best.treeIndex);
            } else {
                cycle.add( best.toIndex + 1, best.treeIndex);
            }
            used.add(best.treeIndex);
            cycleLength += best.distance;
        }

        return cycle;
    }

    private static List<Integer> greedyCycle(int treeIndex, Point first, Problem problem, BitSet used) {
        BitSet localUsed = (BitSet) used.clone();
        localUsed.set(treeIndex);
        List<Integer> cycle = new ArrayList<>();
        cycle.add(treeIndex);
        PriorityQueue<Entry> nearestPoints = new PriorityQueue<>();
        double cycleLength = 0;

        for (int i = 0; i < problem.trees.size(); i++) {
            Point curr = problem.trees.get(i);
            if (curr == first) {
                continue;
            }
            nearestPoints.add(new Entry(curr.distance(first), i, treeIndex));
        }

        while (!nearestPoints.isEmpty() && cycleLength <= problem.maxReach) {
            var entry = nearestPoints.poll();
            int index = entry.treeIndex;
            if (!localUsed.get(index)) {
                Point newTree = problem.trees.get(index);
                double lengthChange;
                if (cycleLength == 0) {
                    lengthChange = entry.distance * 2;
                } else {
                    lengthChange = getLengthChange(first, problem.trees.get(cycle.getLast()), newTree);
                }
                if (cycleLength + lengthChange <= problem.maxReach) {
                    cycleLength += lengthChange;
                    cycle.add(index);
                    localUsed.set(index);

                    nearestPoints.clear(); //TODO maybe leave out
                    List<Point> trees = problem.trees;
                    for (int i = 0; i < trees.size(); i++) {
                        Point tree = trees.get(i);
                        if (tree == newTree) {
                            continue;
                        }
                        nearestPoints.add(new Entry(newTree.distance(tree), i, index));
                    }
                } else {
                    return cycle;
                }
            }
        }
        return cycle;
    }

    private static List<Integer> greedyCycle2(int pointIndex, Point first, Problem problem, BitSet used) {
        BitSet localUsed = (BitSet) used.clone();
        localUsed.set(pointIndex);
        List<Integer> cycle = new ArrayList<>();
        cycle.add(pointIndex);
        double cycleLength = 0;

        while (true) {
            Entry entry = null;
            double bestLenChange = -1;
            for (int i = 0; i < problem.trees.size(); i++) {
                Point curr = problem.trees.get(i);
                if (localUsed.get(i)) {
                    continue;
                }
                double lenChange = getLengthChange(first, problem.trees.get(cycle.getLast()), curr);
                if (entry == null || lenChange < bestLenChange) {
                    entry = new Entry(lenChange, i, cycle.size());
                    bestLenChange = lenChange;
                }
            }

            if (entry == null || cycleLength + bestLenChange > problem.maxReach) {
                return cycle;
            }

            cycle.add(entry.treeIndex);
            localUsed.set(entry.treeIndex);
            cycleLength += bestLenChange;
        }
    }

    private static List<Integer> greedyCycle3(int pointIndex, Point first, Problem problem, BitSet used) {
        BitSet localUsed = (BitSet) used.clone();
        localUsed.set(pointIndex);
        List<Integer> cycle = new ArrayList<>();
        cycle.add(pointIndex);
        double cycleLength = 0;

        while (true) {
            Entry entry = null;
            double bestLenChange = -1;
            for (int i = 0; i < problem.trees.size(); i++) {
                Point curr = problem.trees.get(i);
                if (localUsed.get(i)) {
                    continue;
                }
                Entry lenChange = getBestLengthChange(cycle, problem, curr, i);
                if (entry == null || lenChange.distance < bestLenChange) {
                    entry = lenChange;
                    bestLenChange = lenChange.distance;
                }
            }

            if (entry == null || cycleLength + bestLenChange > problem.maxReach) {
                return cycle;
            }

            if (entry.toIndex == cycle.size()-1) {
                cycle.add(entry.treeIndex);
            } else {
                cycle.add( entry.toIndex + 1, entry.treeIndex);
            }
            localUsed.set(entry.treeIndex);
            cycleLength += bestLenChange;
        }
    }

    private static List<Integer> greedyCycle4(int pointIndex, Point first, Problem problem, BitSet used) {
        BitSet localUsed = (BitSet) used.clone();
        localUsed.set(pointIndex);
        List<Integer> cycle = new ArrayList<>();
        cycle.add(pointIndex);
        double cycleLength = 0;

        boolean alreadyOptimizedCycle = false;
        while (true) {
            Entry entry = null;
            double bestLenChange = -1;
            for (int i = 0; i < problem.trees.size(); i++) {
                Point curr = problem.trees.get(i);
                if (localUsed.get(i)) {
                    continue;
                }
                Entry lenChange = getBestLengthChange(cycle, problem, curr, i);
                if (entry == null || lenChange.distance < bestLenChange) {
                    entry = lenChange;
                    bestLenChange = lenChange.distance;
                }
            }

            if (entry == null || cycleLength + bestLenChange > problem.maxReach) {
                if (alreadyOptimizedCycle) {
                    return cycle;
                } else {
                    alreadyOptimizedCycle = true;
                    cycleLength = optimizeCycle(problem, cycle, cycleLength);
                    if (cycleLength < 0) {
                        return cycle;
                    }
                    continue;
                }
            }

            alreadyOptimizedCycle = false;
            if (entry.toIndex == cycle.size()-1) {
                cycle.add(entry.treeIndex);
            } else {
                cycle.add( entry.toIndex + 1, entry.treeIndex);
            }
            localUsed.set(entry.treeIndex);
            cycleLength += bestLenChange;
        }
    }

    private static double optimizeCycle(Problem problem, List<Integer> cycle, double cycleLength) {
        int pointCount = cycle.size();
        if (pointCount < 4) {
            return -1;
        }

        int changeCount = 0;
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = 0; i < pointCount; i++) {
                int aIndex = (i + 1) % pointCount;
                int bIndex = (i + 2) % pointCount;
                Integer a = cycle.get(aIndex);
                Integer b = cycle.get(bIndex);
                Point p1 = problem.trees.get(cycle.get(i));
                Point p2 = problem.trees.get(a);
                Point p3 = problem.trees.get(b);
                Point p4 = problem.trees.get(cycle.get((i+3)%pointCount));
                double lenChange = calcLenChangeForSwapMiddle(p1, p2, p3, p4);
                if (lenChange < 0) {
                    cycle.set(aIndex, b);
                    cycle.set(bIndex, a);
                    cycleLength += lenChange;
                    changed = true;
                    changeCount++;
                }
            }
        }
        return changeCount == 0 ? -1 : cycleLength;
    }

    private static double calcLenChangeForSwapMiddle(Point p1, Point p2, Point p3, Point p4) {
        return -p1.distance(p2) + p1.distance(p3) - p4.distance(p3) + p4.distance(p2);
    }

    private static Entry getBestLengthChange(final List<Integer> cycle, final Problem problem, final Point curr, final int currIndex) {
        Entry best = null;
        for (int i = 0; i < cycle.size(); i++) {
            Point first = problem.trees.get(cycle.get(i));
            Point lastTree = problem.trees.get(cycle.get((i + 1) % cycle.size()));
            double lengthChange = getLengthChange(first, lastTree, curr);
            if (best == null || lengthChange < best.distance) {
                best = new Entry(lengthChange, currIndex, i);
            }
        }
        return best;
    }

    private static double getLengthChange(Point first, Point lastTree, Point newTree) {
        return -first.distance(lastTree)
                + lastTree.distance(newTree) + first.distance(newTree);
    }

    private static List<List<Point>> convertToPoints(List<List<Integer>> solution, Problem problem) {
        List<List<Point>> pointSolution = new ArrayList<>(solution.size());
        for (List<Integer> currList : solution) {
            List<Point> newList = new ArrayList<>(currList.size());
            for (Integer currIndex : currList) {
                newList.add(problem.trees.get(currIndex));
            }
            pointSolution.add(newList);
        }
        return pointSolution;
    }

    private record Entry(double distance, int treeIndex, int toIndex) implements Comparable<Entry> {
        @Override
        public int compareTo(PopelHeuristic.Entry o) {
            int compare = Double.compare(distance, o.distance);
            return compare == 0 ? Integer.compare(this.treeIndex, o.treeIndex) : compare;
        }
    }

    private record IntInt(int possibilityCount, int index) implements Comparable<IntInt> {
        @Override
        public int compareTo(PopelHeuristic.IntInt o) {
            int res = Integer.compare(possibilityCount, o.possibilityCount);
            return res == 0 ? Integer.compare(index, o.index) : res;
        }
    }
}
