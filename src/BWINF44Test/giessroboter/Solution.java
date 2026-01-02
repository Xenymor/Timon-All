package BWINF44Test.giessroboter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Solution {
    final List<List<Point>> cycles;
    private final List<Point> trees;
    private MyFrame frame;
    private final int maxReach;
    // Speichert die Grenzen zwischen Teil-Solutions für farbliche Unterscheidung
    private final List<Integer> solutionBoundaries;

    public Solution(List<List<Point>> cycles, Problem problem) {
        this.cycles = cycles;
        this.trees = problem.trees;
        this.maxReach = problem.maxReach;
        this.solutionBoundaries = new ArrayList<>();
    }

    // Privater Konstruktor für merge()
    private Solution(List<List<Point>> cycles, List<Point> trees, int maxReach, List<Integer> solutionBoundaries) {
        this.cycles = cycles;
        this.trees = trees;
        this.maxReach = maxReach;
        this.solutionBoundaries = solutionBoundaries;
    }

    /**
     * Führt mehrere Teil-Solutions zu einer Gesamt-Solution zusammen.
     * Alle Zyklen und Bäume werden in einer gemeinsamen Visualisierung dargestellt.
     * 
     * @param solutions Liste der zusammenzuführenden Solutions
     * @return Eine neue Solution, die alle Teil-Solutions enthält
     */
    public static Solution merge(List<Solution> solutions) {
        if (solutions == null || solutions.isEmpty()) {
            throw new IllegalArgumentException("Solutions list cannot be null or empty");
        }

        if (solutions.size() == 1) {
            return solutions.getFirst();
        }

        // Sammle alle Zyklen und merke Grenzen für Farbcodierung
        List<List<Point>> allCycles = new ArrayList<>();
        List<Integer> boundaries = new ArrayList<>();
        
        for (Solution solution : solutions) {
            allCycles.addAll(solution.cycles);
            boundaries.add(allCycles.size()); // Grenze nach dieser Solution
        }

        // Sammle alle Bäume (ohne Duplikate)
        Set<Point> treeSet = new HashSet<>();
        for (Solution solution : solutions) {
            treeSet.addAll(solution.trees);
        }
        List<Point> allTrees = new ArrayList<>(treeSet);

        // Berechne gemeinsamen maxReach (Minimum, da alle Zyklen diesen einhalten müssen)
        int commonMaxReach = solutions.getFirst().maxReach;
        boolean maxReachWarning = false;
        for (Solution solution : solutions) {
            if (solution.maxReach != commonMaxReach) {
                maxReachWarning = true;
            }
            commonMaxReach = Math.min(commonMaxReach, solution.maxReach);
        }

        if (maxReachWarning) {
            System.out.println("Warning: Solutions have different maxReach values. Using minimum: " + commonMaxReach);
        }

        return new Solution(allCycles, allTrees, commonMaxReach, boundaries);
    }

    public List<List<Point>> getCycles() {
        return cycles;
    }

    public void display(int width, int height) {
        if (frame != null) {
            frame.setVisible(false);
            frame.dispose();
        }
        frame = new MyFrame();
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setUndecorated(true);
        frame.setVisible(true);
    }

    public void closeDisplay() {
        if (frame != null) {
            frame.setVisible(false);
            frame.dispose();
            frame = null;
        }
    }

    private class MyFrame extends JFrame {

        public static final int TREE_SIZE = 8;
        boolean painted = false;

        // Farben für verschiedene Teil-Solutions
        private static final Color[] SOLUTION_COLORS = {
            new Color(0, 180, 0),    // Grün
            new Color(0, 100, 255),  // Blau
            new Color(255, 140, 0),  // Orange
            new Color(180, 0, 180),  // Lila
            new Color(0, 180, 180),  // Cyan
            new Color(180, 180, 0),  // Gelb-Grün
            new Color(255, 80, 80),  // Hellrot
            new Color(100, 60, 180)  // Indigo
        };

        @Override
        public void paint(Graphics g) {
            int maxX = Integer.MIN_VALUE;
            int minX = Integer.MAX_VALUE;
            int maxY = Integer.MIN_VALUE;
            int minY = Integer.MAX_VALUE;

            for (Point tree : trees) {
                maxX = Math.max(maxX, tree.x);
                minX = Math.min(minX, tree.x);
                maxY = Math.max(maxY, tree.y);
                minY = Math.min(minY, tree.y);
            }

            float scaleFactor = Math.min((getWidth() - 10f) / (maxX - minX), (getHeight() - 10f) / (maxY - minY));

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.BLACK);
            for (Point tree : trees) {
                float x = scale(tree.x, minX, scaleFactor);
                float y = scale(tree.y, minY, scaleFactor);
                g.fillOval((int) (x - (float) TREE_SIZE / 2), (int) (y - (float) TREE_SIZE / 2), TREE_SIZE, TREE_SIZE);
            }

            double totalLength = 0;
            int cycleIndex = 0;
            int currentSolutionIndex = 0;
            
            for (List<Point> cycle : cycles) {
                cycleIndex++;
                
                // Bestimme Farbe basierend auf Teil-Solution
                Color cycleColor = getCycleColor(cycleIndex - 1, currentSolutionIndex);
                
                // Aktualisiere Solution-Index wenn Grenze überschritten
                if (!solutionBoundaries.isEmpty() && 
                    currentSolutionIndex < solutionBoundaries.size() && 
                    cycleIndex > solutionBoundaries.get(currentSolutionIndex)) {
                    currentSolutionIndex++;
                }
                
                float xSum = 0;
                float ySum = 0;
                double cycleLength = 0;
                Point prev = cycle.getLast();
                for (Point point : cycle) {
                    float x1 = scale(prev.x, minX, scaleFactor);
                    float y1 = scale(prev.y, minY, scaleFactor);
                    float x2 = scale(point.x, minX, scaleFactor);
                    float y2 = scale(point.y, minY, scaleFactor);
                    g.setColor(cycleColor);
                    g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
                    g.setColor(Color.black);
                    g.drawString((int)(prev.distance(point) * 100 / maxReach) + "%", (int)((x1+x2)/2), (int)((y1+y2)/2));
                    cycleLength += prev.distance(point);
                    prev = point;
                    xSum += x2;
                    ySum += y2;
                }
                g.setColor(Color.red);
                g.drawString((int)(cycleLength * 100 / maxReach) + "% (" + cycleIndex + ")", (int) (xSum / cycle.size()), (int) (ySum / cycle.size()));

                totalLength += cycleLength;
            }


            if (painted) {
                return;
            }
            
            // Statistik-Ausgabe
            int numSubSolutions = solutionBoundaries.isEmpty() ? 1 : solutionBoundaries.size();
            if (numSubSolutions > 1) {
                System.out.println("=== Combined Solution (" + numSubSolutions + " sub-solutions) ===");
            }
            System.out.println("Used " + cycles.size() + " cycles.");
            System.out.println("Total cycle length:      " + totalLength + " (avg: " + (totalLength / (float)cycles.size()) + ")" );
            System.out.println("Average trees per cycle: " + (trees.size() / (float)cycles.size()));
            if (numSubSolutions > 1) {
                System.out.println("Sub-solution boundaries: " + solutionBoundaries);
            }
            System.out.println("-------------------------------");

            painted = true;
        }

        private Color getCycleColor(int cycleIndex, int solutionIndex) {
            if (solutionBoundaries.isEmpty()) {
                // Keine Teil-Solutions, verwende Standard-Grün
                return SOLUTION_COLORS[0];
            }
            // Bestimme welche Teil-Solution dieser Zyklus gehört
            int solutionIdx = 0;
            for (int i = 0; i < solutionBoundaries.size(); i++) {
                if (cycleIndex < solutionBoundaries.get(i)) {
                    solutionIdx = i;
                    break;
                }
            }
            return SOLUTION_COLORS[solutionIdx % SOLUTION_COLORS.length];
        }

        private static float scale(int c, int minC, float scaleFactor) {
            return (float) (c - minC) * scaleFactor + 5;
        }
    }
}
