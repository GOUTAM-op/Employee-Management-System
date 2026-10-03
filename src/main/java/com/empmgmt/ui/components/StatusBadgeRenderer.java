package com.empmgmt.ui.components;

import com.empmgmt.model.Employee;
import com.empmgmt.ui.theme.AppTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/** Renders the "status" column as a small colored pill instead of plain text. */
public class StatusBadgeRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                     boolean hasFocus, int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setOpaque(true);
        label.setFont(AppTheme.FONT_SMALL);

        String status = String.valueOf(value);
        Color color;
        switch (Employee.Status.valueOf(status)) {
            case ACTIVE:     color = AppTheme.SUCCESS; break;
            case ON_LEAVE:   color = AppTheme.WARNING; break;
            case TERMINATED: default: color = AppTheme.DANGER;
        }
        label.setBackground(isSelected ? color : new Color(color.getRed(), color.getGreen(), color.getBlue(), 28));
        label.setForeground(isSelected ? Color.WHITE : color);
        label.setText(status.replace("_", " "));
        label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        return label;
    }
}
