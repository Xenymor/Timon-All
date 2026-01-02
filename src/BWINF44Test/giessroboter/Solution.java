package BWINF44Test.giessroboter;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Solution {
    final List<List<Point>> cycles;
    private final List<Point> trees;
    private MyFrame frame;
    private final int maxReach;

    public Solution(List<List<Point>> cycles, Problem problem) {
        this.cycles = cycles;
        this.trees = problem.trees;
        this.maxReach = problem.maxReach;
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
            for (List<Point> cycle : cycles) {
                cycleIndex++;
                float xSum = 0;
                float ySum = 0;
                double cycleLength = 0;
                Point prev = cycle.getLast();
                for (Point point : cycle) {
                    float x1 = scale(prev.x, minX, scaleFactor);
                    float y1 = scale(prev.y, minY, scaleFactor);
                    float x2 = scale(point.x, minX, scaleFactor);
                    float y2 = scale(point.y, minY, scaleFactor);
                    g.setColor(Color.GREEN);
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
            System.out.println("Used " + cycles.size() + " cycles.");
            System.out.println("Total cycle length:      " + totalLength + " (avg: " + (totalLength / (float)cycles.size()) + ")" );
            System.out.println("Average trees per cycle: " + (trees.size() / (float)cycles.size()));
            System.out.println("-------------------------------");

            painted = true;
        }

        private static float scale(int c, int minC, float scaleFactor) {
            return (float) (c - minC) * scaleFactor + 5;
        }
    }
}
