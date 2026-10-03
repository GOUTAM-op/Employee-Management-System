package com.empmgmt.ui.components;

import com.empmgmt.ui.theme.AppTheme;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;

/**
 * A JTextField with rounded corners, a placeholder that fades away once the
 * user types, and a red-border + inline error label when validation fails.
 */
public class StyledField extends JTextField {

    private final String placeholder;
    private final JLabel errorLabel;
    private boolean hasError = false;

    public StyledField(String placeholder) {
        super();
        this.placeholder = placeholder;
        this.errorLabel = new JLabel();
        setFont(AppTheme.FONT_BODY);
        setForeground(AppTheme.TEXT_PRIMARY);
        setOpaque(false);
        setBorder(new RoundedBorder());
        setMargin(new Insets(8, 12, 8, 12));

        errorLabel.setFont(AppTheme.FONT_SMALL);
        errorLabel.setForeground(AppTheme.DANGER);
        errorLabel.setVisible(false);
    }

    public JLabel getErrorLabel() { return errorLabel; }

    public void setError(String message) {
        hasError = message != null;
        errorLabel.setText(message == null ? "" : "⚠ " + message);
        errorLabel.setVisible(message != null);
        repaint();
    }

    public void clearError() { setError(null); }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
        g2.dispose();
        super.paintComponent(g);

        if (getText().isEmpty() && !isFocusOwner()) {
            Graphics2D pg = (Graphics2D) g.create();
            pg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            pg.setColor(AppTheme.TEXT_SECONDARY);
            pg.setFont(getFont());
            FontMetrics fm = pg.getFontMetrics();
            pg.drawString(placeholder, getInsets().left, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            pg.dispose();
        }
    }

    private class RoundedBorder extends AbstractBorder {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(hasError ? AppTheme.DANGER : (isFocusOwner() ? AppTheme.ACCENT : AppTheme.BORDER));
            g2.setStroke(new BasicStroke(isFocusOwner() || hasError ? 1.6f : 1f));
            g2.drawRoundRect(x, y, w - 1, h - 1, 8, 8);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(8, 12, 8, 12); }
    }
}
