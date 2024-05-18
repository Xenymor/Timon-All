package GameOfLife;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.atomic.AtomicBoolean;

public class Test {

    public static void main(String[] args) throws InterruptedException {
        boolean[][] start = new boolean[1919][1079];
        start[5][4] = true;
        start[5][5] = true;
        start[5][6] = true;
        start[4][5] = true;
        start[6][6] = true;
        final Board board = new Board(start);
        Simulation test = new Simulation(board);
        Graphics gfx = new Graphics(board, 1);
        gfx.setUndecorated(true);
        gfx.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        final AtomicBoolean shouldRun = new AtomicBoolean(false);
        gfx.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(final KeyEvent e) {
                final char keyChar = e.getKeyChar();
                System.out.println("Typed: " + keyChar);
                if (keyChar == 'a') {
                    System.out.println("Detected a");
                    shouldRun.set(true);
                } else if (keyChar == 's') {
                    shouldRun.set(false);
                } else if (keyChar == 'd') {
                    test.nextStep();
                }
            }

            @Override
            public void keyPressed(final KeyEvent e) {

            }

            @Override
            public void keyReleased(final KeyEvent e) {

            }
        });
        gfx.setVisible(true);
        gfx.requestFocusInWindow();

        while (true) {
            //System.out.println(shouldRun[0]);
            if (shouldRun.get()) {
                //System.out.println("Simulating");
                test.nextStep();
                Thread.sleep(1);
            }
        }
    }
}
