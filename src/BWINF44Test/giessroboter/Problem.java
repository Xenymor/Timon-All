package BWINF44Test.giessroboter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Problem {
    public final int maxReach;
    public final List<Point> trees;
    private MyFrame frame;

    public Problem(List<String> input) {
        maxReach = Integer.parseInt(input.getFirst());
        List<Point> temp = new ArrayList<>(Integer.parseInt(input.get(1)));
        for (int i = 2; i < input.size(); i++) {
            String[] parts = input.get(i).split(" ");
            temp.add(new Point(Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
        }
        trees = Collections.unmodifiableList(temp);
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

            g.setColor(Color.RED);
            for (Point first : trees) {
                for (Point second : trees) {
                    if (first == second) {
                        continue;
                    }
                    if (first.distance(second) <= 0.5 * maxReach) {
                        g.drawLine((int) scale(first.x, minX, scaleFactor), (int) scale(first.y, minY, scaleFactor),
                                (int) scale(second.x, minX, scaleFactor), (int) scale(second.y, minY, scaleFactor));
                    }
                }
            }
        }

        private static float scale(int c, int minC, float scaleFactor) {
            return (float) (c - minC) * scaleFactor + 5;
        }
    }
}
