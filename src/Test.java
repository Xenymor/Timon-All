import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        MyFrame frame = new MyFrame();
        frame.std = 1;
        frame.setSize(1000, 1000);
        frame.setUndecorated(true);
        frame.setVisible(true);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            frame.std = scanner.nextInt();
            frame.repaint();
            Thread.sleep(1);
        }
    }

    private static class MyFrame extends JFrame {
        public int std;

        @Override
        public void paint(final Graphics g) {
            final int width = getWidth();
            final int height = getHeight();
            double[][] points = fillArray(width, height);
            double min = Double.POSITIVE_INFINITY;
            double max = Double.NEGATIVE_INFINITY;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    min = Math.min(Math.abs(points[x][y]), min);
                    max = Math.max(Math.abs(points[x][y]), max);
                }
            }
            System.out.println("min: " + min);
            System.out.println("max: " + max);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    final double a = points[height - y - 1][x];
                    final int c = (int) Math.round(map(Math.abs(a), min, max, 0, 255));
                    final int abs = Math.abs(c);
                    g.setColor(new Color(a < 0 ? 255 : 0, abs, 0));
                    g.fillRect(x, y, 1, 1);
                }
            }
        }

        private double[][] fillArray(int width, int height) {
            double[][] result = new double[width][height];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    result[x][y] = method(x / (double) width, y / (double) height);
                }
            }
            return result;
        }

        private double method(double x, double y) {
            if (x == 0 || y == 0) {
                return 0;
            }
            double y1 = Math.sqrt(-2.0 * Math.log(x)) * Math.cos(2.0 * Math.PI * y);
            double result = y1 * std + 2d;
            if (Double.isNaN(result)) {
                System.out.println(x + "; " + y);
            }
            return result;
        }

        private double map(double n, double start1, double stop1, double start2, double stop2) {
            return ((n - start1) / (stop1 - start1)) * (stop2 - start2) + start2;
        }
    }
}
