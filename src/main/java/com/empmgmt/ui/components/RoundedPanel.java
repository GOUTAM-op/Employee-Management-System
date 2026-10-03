package com.empmgmt.ui.components;

import com.empmgmt.ui.theme.AppTheme;

import javax.swing.*;
import java.awt.*;

/** A JPanel painted as a white rounded-corner "card" with a faint drop shadow. */
public class RoundedPanel extends JPanel {

    private final int radius;
    private final boolean shadow;

    public RoundedPanel(int radius, boolean shadow) {
        this.radius = radius;
        this.shadow = shadow;
        setOpaque(false);
        setBorder(AppTheme.padding(16));
    }

    public RoundedPanel() { this(16, true); }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        int pad = shadow ? 4 : 0;

        if (shadow) {
            g2.setColor(new Color(0, 0, 0, 18));
            g2.fillRoundRect(2, 4, w - 4, h - 4, radius, radius);
        }
        g2.setColor(AppTheme.CARD);
        g2.fillRoundRect(0, 0, w - pad, h - pad, radius, radius);
        g2.setColor(AppTheme.BORDER);
        g2.drawRoundRect(0, 0, w - pad - 1, h - pad - 1, radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
}
