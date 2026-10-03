package com.empmgmt.ui.components;

import com.empmgmt.ui.theme.AppTheme;

import javax.swing.*;
import java.awt.*;

/** Small metric card used across the dashboard: an icon chip, a big number, and a caption. */
public class StatCard extends RoundedPanel {

    private final JLabel valueLabel;

    public StatCard(String icon, String caption, String initialValue, Color accent) {
        super(16, true);
        setLayout(new BorderLayout(0, 6));

        JLabel iconChip = new JLabel(icon, SwingConstants.CENTER);
        iconChip.setOpaque(true);
        iconChip.setBackground(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 30));
        iconChip.setForeground(accent);
        iconChip.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        iconChip.setPreferredSize(new Dimension(36, 36));
        iconChip.setMaximumSize(new Dimension(36, 36));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        top.setOpaque(false);
        top.add(iconChip);

        valueLabel = new JLabel(initialValue);
        valueLabel.setFont(AppTheme.FONT_STAT);
        valueLabel.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel captionLabel = new JLabel(caption);
        captionLabel.setFont(AppTheme.FONT_SMALL);
        captionLabel.setForeground(AppTheme.TEXT_SECONDARY);

        JPanel textBlock = new JPanel();
        textBlock.setOpaque(false);
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));
        textBlock.add(valueLabel);
        textBlock.add(captionLabel);

        add(top, BorderLayout.NORTH);
        add(textBlock, BorderLayout.SOUTH);
        setPreferredSize(new Dimension(210, 110));
    }

    public void setValue(String value) { valueLabel.setText(value); }
}
