package com.empmgmt.ui.components;

import com.empmgmt.ui.theme.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** A full-width nav item for the dark sidebar: icon + label, highlighted when active. */
public class SidebarButton extends JPanel {

    private boolean active = false;
    private boolean hovering = false;
    private final JLabel label;

    public SidebarButton(String icon, String text) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 14, 12));
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        setPreferredSize(new Dimension(220, 46));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
        iconLabel.setForeground(Color.WHITE);

        label = new JLabel(text);
        label.setFont(AppTheme.FONT_BODY_BOLD);
        label.setForeground(new Color(0xCB, 0xD5, 0xE1));

        add(iconLabel);
        add(label);

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovering = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { hovering = false; repaint(); }
        });
    }

    public void setActive(boolean active) {
        this.active = active;
        label.setForeground(active ? Color.WHITE : new Color(0xCB, 0xD5, 0xE1));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (active) {
            g2.setColor(AppTheme.SIDEBAR_ACTIVE);
            g2.fillRoundRect(8, 2, getWidth() - 16, getHeight() - 4, 10, 10);
            g2.setColor(AppTheme.ACCENT);
            g2.fillRoundRect(8, 2, 4, getHeight() - 4, 4, 4);
        } else if (hovering) {
            g2.setColor(new Color(255, 255, 255, 12));
            g2.fillRoundRect(8, 2, getWidth() - 16, getHeight() - 4, 10, 10);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
