package GameOfLife;

import Music.MusicPlayer;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.atomic.AtomicBoolean;

public class Test {

    public static final int FRAME_LENGTH = 400;

    public static void main(String[] args) throws InterruptedException {
        MusicPlayer player = new MusicPlayer(11);
        boolean[][] start = new boolean[11][11];
        start[5][5] = true;
        start[5][6] = true;
        start[5][7] = true;
        start[4][7] = true;
        start[3][6] = true;
        final Board board = new Board(start);
        Simulation test = new Simulation(board);
        Graphics gfx = new Graphics(board, 10);
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
                test.setStates();
                final boolean[][] state = board.getChange();
                for (int x = 0; x < board.width; x++) {
                    for (int y = 0; y < board.height; y++) {
                        if (state[x][y] && !board.getState(x, y)) {
                            player.playNote(10 + x * 3 + (board.height - y) * 5, FRAME_LENGTH);
                        }
                    }
                }
                board.update();
                Thread.sleep(FRAME_LENGTH);
            }
        }
    }
}
