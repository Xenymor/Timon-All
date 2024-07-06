package NeuralNetworkProjects.Pendulum;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class Main {
    private static final double SECONDS_TO_NANOS = 1_000_000_000;
    public static final int ORIGIN_SPEED = 5;

    public static void main(String[] args) {
        Pendulum pendulum = new Pendulum(new Vector2(600, 300), 200, 0.25 * Math.PI, 100000, 10);
        MyFrame frame = new MyFrame(pendulum);
        frame.setSize(1200, 600);
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        long lastFrameFinish = System.nanoTime();
        final long[] lastFrameTime = {1};
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    pendulum.moveOrigin(-ORIGIN_SPEED, lastFrameTime[0] / SECONDS_TO_NANOS);
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    pendulum.moveOrigin(ORIGIN_SPEED, lastFrameTime[0] / SECONDS_TO_NANOS);
                }
            }
        });
        while (true) {
            pendulum.update(lastFrameTime[0] / SECONDS_TO_NANOS);
            final long curr = System.nanoTime();
            lastFrameTime[0] = (curr - lastFrameFinish);
            lastFrameFinish = curr;
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class MyFrame extends JFrame {
        final Pendulum pendulum;

        public MyFrame(final Pendulum pendulum) {
            this.pendulum = pendulum;
            buffer = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            bufferGraphics = buffer.getGraphics();
        }

        BufferedImage buffer;
        Graphics bufferGraphics;

        @Override
        public void paint(final Graphics g) {
            if (buffer.getWidth() != getWidth() || buffer.getHeight() != getHeight()) {
                buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                bufferGraphics = buffer.getGraphics();
            }
            bufferGraphics.setColor(Color.WHITE);
            bufferGraphics.fillRect(0, 0, getWidth(), getHeight());
            pendulum.paint(bufferGraphics);
            g.drawImage(buffer, 0, 0, null);
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            repaint();
        }
    }
}
