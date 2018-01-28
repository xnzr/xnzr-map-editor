package io.xnzr.maped;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * View for radio source
 */
public class JRadioSourceView extends JComponent {
    private BufferedImage bufferedImage;
    JRadioSourceView(BufferedImage image) {
        bufferedImage = image;
    }
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        g2d.scale((double)getWidth()/bufferedImage.getWidth(), (double)getHeight()/bufferedImage.getHeight());
        g2d.drawImage(bufferedImage, 0, 0, null);
    }
}
