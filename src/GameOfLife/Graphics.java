package GameOfLife;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Graphics extends JFrame {
    private static final Color ALIVE_COL = new Color(199, 192, 17);
    private static final Color DEAD_COL = new Color(255, 255, 255);
    Board board;
    final int BLOCK_SIZE;
    BufferedImage img;

    public Graphics(Board board, int blockSize) throws HeadlessException {
        this.board = board;
        setMinimumSize(new Dimension(board.width*blockSize, board.height*blockSize));
        BLOCK_SIZE = blockSize;
        img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
    }

    @Override
    public void paint(final java.awt.Graphics og) {
        java.awt.Graphics g = img.getGraphics();
        board.executeWithLock(() -> {
            g.setColor(DEAD_COL);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(ALIVE_COL);
            for (int x = 0; x < board.width; x+=1) {
                for (int y = 0; y < board.height; y+=1) {
                    if (board.getState(x, y)) {
                        g.fillRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                    }
                }
            }
        });
        og.drawImage(img, 0, 0, null);
        repaint();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
