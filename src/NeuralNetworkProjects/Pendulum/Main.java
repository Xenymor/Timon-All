package NeuralNetworkProjects.Pendulum;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Main {
    public static void main(String[] args) {
        Pendulum pendulum = new Pendulum(new Vector2(600, 300), 100, 0.5 * Math.PI);
        MyFrame frame = new MyFrame(pendulum);
        frame.setSize(1200, 600);
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    pendulum.moveOriginLeft();
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    pendulum.moveOriginRight();
                }
            }
        });
        while (true) {
            pendulum.update();
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
        }

        @Override
        public void paint(final Graphics g) {
            g.clearRect(0, 0, getWidth(), getHeight());
            pendulum.paint(g);
            repaint();
        }
    }
}
