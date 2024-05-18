package GameOfLife;

import javax.swing.*;
import java.awt.*;

public class Graphics extends JFrame {
    private static final Color ALIVE_COL = new Color(199, 192, 17);
    private static final Color DEAD_COL = new Color(255, 255, 255);
    Board board;
    final int BLOCK_SIZE;

    public Graphics(Board board, int blockSize) throws HeadlessException {
        this.board = board;
        setMinimumSize(new Dimension(board.width*blockSize, board.height*blockSize));
        BLOCK_SIZE = blockSize;
    }

    @Override
    public void paint(final java.awt.Graphics g) {
        final int width = getWidth();
        final int height = getHeight();
        board.executeWithLock(() -> {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    g.setColor(board.getState(x/BLOCK_SIZE, y/BLOCK_SIZE) ? ALIVE_COL : DEAD_COL);
                    g.fillRect(x, y, 1, 1);
                }
            }
        });
        repaint();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
