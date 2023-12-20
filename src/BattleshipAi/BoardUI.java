package BattleshipAi;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class BoardUI extends JFrame {
    private static final Color SHIP_COLOR = new Color(0, 35, 5);
    private static final Color HIT_COLOR = new Color(222, 46, 46, 127);
    private static final Color GRID_COLOR = new Color(98, 98, 98);
    private static final int GRID_WIDTH = 2;
    BattleshipBoard board;
    final int ZOOM;
    public boolean drawUnattackedShips = true;

    public BoardUI(final BattleshipBoard battleshipBoard, final int zoom) {
        this.board = battleshipBoard;
        this.ZOOM = zoom;
    }

    BufferedImage buffer;

    @Override
    public void paint(final Graphics g) {
        final int width = getWidth();
        final int height = getHeight();
        buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = buffer.getGraphics();
        Field[][] boardArr = board.board;
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        for (int x = 0; x < boardArr.length; x++) {
            for (int y = 0; y < boardArr[x].length; y++) {
                final Field field = boardArr[x][y];
                if (field.isShip()) {
                    if (drawUnattackedShips || field.isHit()) {
                        graphics.setColor(SHIP_COLOR);
                        graphics.fillRect(x * ZOOM, y * ZOOM, ZOOM, ZOOM);
                    }
                }
                if (field.isHit()) {
                    graphics.setColor(HIT_COLOR);
                    graphics.fillRect(x * ZOOM, y * ZOOM, ZOOM, ZOOM);
                }
            }
        }
        graphics.setColor(GRID_COLOR);
        for (int x = 0; x < width; x += ZOOM) {
            graphics.fillRect(x-(GRID_WIDTH/2), 0, GRID_WIDTH, height);
        }
        for (int y = 0; y < height; y += ZOOM) {
            graphics.fillRect(0, y-(GRID_WIDTH/2), width, GRID_WIDTH);
        }
        g.drawImage(buffer, 0, 0, null);
        repaint(20);
    }
}
