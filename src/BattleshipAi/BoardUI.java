package BattleshipAi;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class BoardUI extends JFrame {
    private static final Color SHIP_COLOR = new Color(0, 35, 5);
    private static final Color HIT_COLOR = new Color(222, 46, 46, 127);
    BattleshipBoard board;
    final int ZOOM;

    public BoardUI(final BattleshipBoard battleshipBoard, final int zoom) {
        this.board = battleshipBoard;
        this.ZOOM = zoom;
    }

    BufferedImage buffer;

    @Override
    public void paint(final Graphics g) {
        buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = buffer.getGraphics();
        Field[][] boardArr = board.board;
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, getWidth(), getHeight());
        for (int x = 0; x < boardArr.length; x++) {
            for (int y = 0; y < boardArr[x].length; y++) {
                if (boardArr[x][y].isShip()) {
                    graphics.setColor(SHIP_COLOR);
                    graphics.fillRect(x*ZOOM, y*ZOOM, ZOOM, ZOOM);
                }
                if (boardArr[x][y].isHit()) {
                    graphics.setColor(HIT_COLOR);
                    graphics.fillRect(x*ZOOM, y*ZOOM, ZOOM, ZOOM);
                }
            }
        }
        g.drawImage(buffer, 0, 0, null);
        repaint(1_000);
    }
}
