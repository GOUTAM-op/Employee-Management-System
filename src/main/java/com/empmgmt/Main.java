package com.empmgmt;

import com.empmgmt.dao.DatabaseConnection;
import com.empmgmt.exception.DatabaseException;
import com.empmgmt.ui.MainFrame;
import com.empmgmt.ui.theme.AppTheme;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        AppTheme.applyGlobalDefaults();

        SwingUtilities.invokeLater(() -> {
            try {
                DatabaseConnection.initializeSchema();
            } catch (DatabaseException ex) {
                JOptionPane.showMessageDialog(null,
                        ex.getMessage() + "\n\nThe app will still open, but nothing will load or save until this is fixed.",
                        "Database connection problem", JOptionPane.WARNING_MESSAGE);
            }

            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
