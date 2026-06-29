package com.campuscomplaint.util;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

public class LogoUtils {
    
    public static ImageIcon loadLogo(int size) {
        try {
            File logoFile = new File("resources/images/logo.png");
            if (!logoFile.exists()) {
                return createDefaultLogo(size);
            }
            ImageIcon original = new ImageIcon(logoFile.getAbsolutePath());
            Image scaled = original.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            BufferedImage rounded = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = rounded.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Shape circle = new RoundRectangle2D.Float(0, 0, size, size, size, size);
            g2.setClip(circle);
            g2.drawImage(scaled, 0, 0, null);
            g2.dispose();
            return new ImageIcon(rounded);
        } catch (Exception e) {
            return createDefaultLogo(size);
        }
    }
    
    private static ImageIcon createDefaultLogo(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(21, 101, 192));
        g2.fillOval(0, 0, size, size);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, size / 3));
        String text = "CCTS";
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        g2.drawString(text, (size - textWidth) / 2, (size + textHeight) / 2 - 2);
        g2.dispose();
        return new ImageIcon(img);
    }
}