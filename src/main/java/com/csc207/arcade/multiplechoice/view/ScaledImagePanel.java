package com.csc207.arcade.multiplechoice.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

/**
 * Custom panel that displays an image scaled to fit the panel size.
 */
public class ScaledImagePanel extends JPanel {
    private BufferedImage image;

    public ScaledImagePanel() {
        setPreferredSize(new Dimension(600, 400));
        setBackground(Color.WHITE);
    }

    /**
     * Sets the image to display from a resource path.
     *
     * @param imagePath Path to the image resource
     */
    public void setImage(String imagePath) {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(imagePath);
            if (is != null) {
                this.image = ImageIO.read(is);
                is.close();
                repaint();
            } else {
                System.err.println("Image not found: " + imagePath);
                this.image = null;
                repaint();
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            this.image = null;
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            // Draw image scaled to panel size
            g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        } else {
            // Draw placeholder text if no image
            g.setColor(Color.GRAY);
            String text = "No Image Available";
            FontMetrics fm = g.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = getHeight() / 2;
            g.drawString(text, x, y);
        }
    }
}
