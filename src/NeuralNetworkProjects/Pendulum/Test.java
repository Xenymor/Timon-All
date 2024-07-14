package NeuralNetworkProjects.Pendulum;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class Test {
    private static final double SECONDS_TO_NANOS = 1_000_000_000;
    public static final int ORIGIN_SPEED = 10;

    public static void main(String[] args) {
        Pendulum pendulum = new Pendulum(new Vector2(600, 300), 200, 0.25 * Math.PI, 10000, 0.1);
        MyFrame frame = new MyFrame(pendulum);
        frame.setSize(1200, 600);
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        long lastFrameFinish = System.nanoTime();
        long lastFrameTime = 1;
        final int[] key = {0};
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    key[0] = -1;
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    key[0] = 1;
                }
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    key[0] = 0;
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    key[0] = 0;
                }
            }
        });
        while (true) {
            pendulum.update(lastFrameTime / SECONDS_TO_NANOS);
            final long curr = System.nanoTime();
            lastFrameTime = (curr - lastFrameFinish);
            lastFrameFinish = curr;
            if (key[0] == 1) {
                pendulum.moveOrigin(ORIGIN_SPEED, lastFrameTime);
            } else if (key[0] == -1) {
                pendulum.moveOrigin(-ORIGIN_SPEED, lastFrameTime);
            }
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
