package com.campuscomplaint.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ColorScheme {
    public static final Color PRIMARY_DARK = new Color(13, 71, 161);
    public static final Color PRIMARY_BLUE = new Color(21, 101, 192);
    public static final Color DANGER_RED = new Color(198, 40, 40);
    public static final Color BACKGROUND = new Color(245, 245, 245);
    public static final Color SUCCESS_GREEN = new Color(46, 125, 50);
    public static final Color STATUS_LIGHT = new Color(227, 242, 253);
    public static final Color ALTERNATE_ROW = new Color(240, 248, 255);

    public static void styleButton(JButton button) {
        button.setBackground(PRIMARY_BLUE);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void styleDangerButton(JButton button) {
        button.setBackground(DANGER_RED);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void styleTableHeader(JTable table) {
        table.getTableHeader().setBackground(PRIMARY_BLUE);
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setRowHeight(25);
    }

  public static void styleTable(JTable table) {
    table.setBackground(Color.WHITE);
    table.setForeground(Color.BLACK);
    table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    table.setSelectionBackground(STATUS_LIGHT);
    table.setSelectionForeground(Color.BLACK);
    table.setGridColor(new Color(200, 200, 200));
    table.setRowHeight(25);

    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            // Ensure value is displayed as String
            if (value != null && !(value instanceof String)) {
                value = String.valueOf(value);
            }
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? Color.WHITE : ALTERNATE_ROW);
            }
            return c;
        }
    });
}
}