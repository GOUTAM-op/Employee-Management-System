package com.empmgmt.ui.components;

import com.empmgmt.ui.theme.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Flat, rounded button with a hover/press color shift instead of the default gray Swing look. */
public class RoundedButton extends JButton {

    public enum Style { PRIMARY, SECONDARY, DANGER, GHOST }

    private final Style style;
    private Color base;
    private Color hover;
    private boolean isHovering = false;
    private boolean isPressed = false;

    public RoundedButton(String text, Style style) {
        super(text);
        this.style = style;
        setFont(AppTheme.FONT_BODY_BOLD);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(9, 20, 9, 20));

        switch (style) {
            case PRIMARY:
                base = AppTheme.ACCENT; hover = AppTheme.ACCENT_DARK; setForeground(Color.WHITE);
                break;
            case DANGER:
                base = AppTheme.DANGER; hover = new Color(0xB9, 0x1C, 0x1C); setForeground(Color.WHITE);
                break;
            case SECONDARY:
                base = AppTheme.BORDER; hover = new Color(0xCB, 0xD5, 0xE1); setForeground(AppTheme.TEXT_PRIMARY);
                break;
            case GHOST:
            default:
                base = new Color(0, 0, 0, 0); hover = new Color(0, 0, 0, 15); setForeground(AppTheme.ACCENT);
        }

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { isHovering = true; repaint(); }
            @Override public void mouseExited(MouseEvent e)  { isHovering = false; repaint(); }
            @Override public void mousePressed(MouseEvent e) { isPressed = true; repaint(); }
            @Override public void mouseReleased(MouseEvent e){ isPressed = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color fill = isPressed ? hover.darker() : (isHovering ? hover : base);
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public boolean isOpaque() { return false; }
}
