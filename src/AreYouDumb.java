import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class AreYouDumb {
    public static void main(String[] args) {
        new AreYouDumb().ask();
    }

    private void ask() {
        JFrame jFrame = new JFrame();
        JPanel jPanel = new JPanel();
        jPanel.setPreferredSize(new Dimension(600, 600));
        jPanel.setOpaque(false);
        jFrame.add(jPanel);
        jFrame.setSize(600, 600);
        JLabel jLabel = new JLabel("Are you dumb?");
        //jLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel.setPreferredSize(new Dimension(100, 50));
        jFrame.add(jLabel);
        JButton yesButton = new JButton("Yes");
        yesButton.addActionListener(e -> {
            jFrame.removeAll();
            jFrame.add(new JLabel(("I knew it.")));
            jFrame.paint(jFrame.getGraphics());
        });
        //yesButton.setHorizontalAlignment(SwingConstants.LEFT);
        yesButton.setPreferredSize(new Dimension(20,  20));
        jFrame.add(yesButton);
        jFrame.pack();
        jFrame.setVisible(true);
    }
}