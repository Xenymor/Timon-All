package CodeWars.Physik;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class WaveSimulation {
    public static void main(String[] args) {
        MyFrame frame = new MyFrame();
        frame.setUndecorated(true);
        frame.setSize(600, 600);
        frame.setVisible(true);
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(final KeyEvent e) {
                char typed = e.getKeyChar();
                switch (typed) {
                    case 'k' -> frame.time += 1;
                    case 'j' -> frame.time -= 1;
                    case 'd' -> frame.exciterX += 1;
                    case 'a' -> frame.exciterX -= 1;
                    case 's' -> frame.exciterY += 1;
                    case 'w' -> frame.exciterY -= 1;
                    case 'c' -> {
                        frame.exciterY = frame.getHeight() / 2;
                        frame.exciterX = frame.getWidth() / 2;
                    }
                    case '+' -> frame.exciterVelocityX += 1;
                    case '-' -> frame.exciterVelocityX -= 1;
                    case '#' -> frame.exciterVelocityX = 0;
                }
                frame.repaint();
            }

            @Override
            public void keyPressed(final KeyEvent e) {

            }

            @Override
            public void keyReleased(final KeyEvent e) {

            }
        });
    }

    private static class MyFrame extends JFrame {
        public int time;
        public final double velocity;
        public int exciterX;
        public int exciterY;
        public double exciterVelocityX;

        public MyFrame() throws HeadlessException {
            time = 0;
            velocity = 10;
            exciterX = 0;
            exciterY = 0;
            exciterVelocityX = 0;
        }

        @Override
        public void paint(final Graphics g) {
            g.clearRect(0, 0, getWidth(), getHeight());

            for (int currTime = 0; currTime < time; currTime++) {
                int currX = (int) (exciterX + currTime * exciterVelocityX);
                int currY = exciterY;
                int radius = (int) ((time - currTime) * velocity);
                g.drawOval(currX - radius, currY - radius, radius * 2, radius * 2);
            }
        }
    }
}
