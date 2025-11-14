package com.csc207.arcade.multiplechoice.app;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Utility to generate placeholder question images for testing.
 */
public class ImageGenerator {
    public static void main(String[] args) {
        String imagesDir = "src/main/resources/data/images/";
        new File(imagesDir).mkdirs();
        
        for (int i = 1; i <= 15; i++) {
            String id = String.format("id%03d", i);
            int level = (i % 3) + 1;
            String answer = String.valueOf((char)('A' + (i % 4)));
            String filename = id + "_level" + level + "_answer" + answer + ".png";
            
            generatePlaceholderImage(imagesDir + filename, id, level, answer);
        }
        
        System.out.println("Successfully generated 15 placeholder images.");
    }
    
    private static void generatePlaceholderImage(String filepath, String id, int level, String answer) {
        int width = 600;
        int height = 400;
        
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        
        // Set background color based on level
        Color bgColor;
        switch (level) {
            case 1: bgColor = new Color(220, 240, 255); break;
            case 2: bgColor = new Color(255, 240, 220); break;
            case 3: bgColor = new Color(240, 255, 220); break;
            default: bgColor = Color.WHITE;
        }
        g.setColor(bgColor);
        g.fillRect(0, 0, width, height);
        
        // Draw border
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(3));
        g.drawRect(5, 5, width - 10, height - 10);
        
        // Draw question ID
        g.setFont(new Font("Arial", Font.BOLD, 32));
        g.setColor(Color.DARK_GRAY);
        String questionText = "Question " + id.substring(2);
        FontMetrics fm = g.getFontMetrics();
        int x = (width - fm.stringWidth(questionText)) / 2;
        g.drawString(questionText, x, 80);
        
        // Draw level
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        String levelText = "Level " + level;
        x = (width - g.getFontMetrics().stringWidth(levelText)) / 2;
        g.drawString(levelText, x, 140);
        
        // Draw sample math problem based on id number
        g.setFont(new Font("Arial", Font.BOLD, 36));
        int num = Integer.parseInt(id.substring(2));
        String problem = String.format("What is %d + %d?", num, num);
        x = (width - g.getFontMetrics().stringWidth(problem)) / 2;
        g.drawString(problem, x, 220);
        
        // Draw answer options
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        int correctValue = num + num;
        String[] options = new String[4];
        for (int i = 0; i < 4; i++) {
            char option = (char)('A' + i);
            int value = correctValue + (answer.equals(String.valueOf(option)) ? 0 : (i - 2));
            options[i] = option + ") " + value;
        }
        
        int startY = 280;
        for (int i = 0; i < 4; i++) {
            x = (width - g.getFontMetrics().stringWidth(options[i])) / 2;
            g.drawString(options[i], x, startY + i * 30);
        }
        
        // Draw correct answer hint (small text at bottom)
        g.setFont(new Font("Arial", Font.ITALIC, 14));
        g.setColor(Color.GRAY);
        String hint = "Correct Answer: " + answer;
        x = (width - g.getFontMetrics().stringWidth(hint)) / 2;
        g.drawString(hint, x, height - 20);
        
        g.dispose();
        
        try {
            ImageIO.write(image, "png", new File(filepath));
        } catch (IOException e) {
            System.err.println("Error writing image: " + filepath);
            e.printStackTrace();
        }
    }
}
