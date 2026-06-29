package com.campuscomplaint.ui;

import com.campuscomplaint.util.LogoUtils;
import javax.swing.*;
import java.awt.*;

public class TopStripPanel extends JPanel {
    private JLabel titleLabel;
    
    public TopStripPanel(String title) {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.PRIMARY_DARK);
        setPreferredSize(new Dimension(800, 65));
        
        ImageIcon logo = LogoUtils.loadLogo(40);
        JLabel logoLabel = new JLabel(logo != null ? logo : new ImageIcon());
        logoLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
        
        titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        leftPanel.setBackground(ColorScheme.PRIMARY_DARK);
        leftPanel.add(logoLabel);
        
        add(leftPanel, BorderLayout.WEST);
        add(titleLabel, BorderLayout.CENTER);
    }
    
    public void setTitle(String title) {
        titleLabel.setText(title);
    }
}