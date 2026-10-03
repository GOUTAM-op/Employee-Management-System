package com.empmgmt.ui.theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Single source of truth for colors, fonts and spacing so every screen
 * looks like it belongs to the same product instead of five different
 * default-Swing prototypes glued together.
 */
public final class AppTheme {

    private AppTheme() { }

    // --- palette ---
    public static final Color SIDEBAR         = new Color(0x1E, 0x29, 0x3B);
    public static final Color SIDEBAR_ACTIVE  = new Color(0x33, 0x41, 0x5C);
    public static final Color ACCENT          = new Color(0x63, 0x66, 0xF1);
    public static final Color ACCENT_DARK     = new Color(0x4F, 0x46, 0xE5);
    public static final Color BACKGROUND      = new Color(0xF1, 0xF5, 0xF9);
    public static final Color CARD            = Color.WHITE;
    public static final Color BORDER          = new Color(0xE2, 0xE8, 0xF0);
    public static final Color TEXT_PRIMARY    = new Color(0x0F, 0x17, 0x2A);
    public static final Color TEXT_SECONDARY  = new Color(0x64, 0x74, 0x8B);
    public static final Color SUCCESS         = new Color(0x16, 0xA3, 0x4A);
    public static final Color DANGER          = new Color(0xDC, 0x26, 0x26);
    public static final Color WARNING         = new Color(0xD9, 0x77, 0x06);

    // --- typography ---
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_STAT    = new Font("Segoe UI", Font.BOLD, 26);

    public static void applyGlobalDefaults() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { /* fall back to default L&F */ }

        UIManager.put("control", BACKGROUND);
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("OptionPane.background", CARD);
        UIManager.put("OptionPane.messageFont", FONT_BODY);
        UIManager.put("Button.font", FONT_BODY_BOLD);
        UIManager.put("Label.font", FONT_BODY);
        UIManager.put("TextField.font", FONT_BODY);
        UIManager.put("Table.font", FONT_BODY);
        UIManager.put("Table.rowHeight", 34);
        UIManager.put("TableHeader.font", FONT_BODY_BOLD);
    }

    public static EmptyBorder padding(int all) { return new EmptyBorder(all, all, all, all); }
    public static EmptyBorder padding(int top, int left, int bottom, int right) {
        return new EmptyBorder(top, left, bottom, right);
    }
}
