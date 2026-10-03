package com.empmgmt.ui;

import com.empmgmt.dao.EmployeeDAO;
import com.empmgmt.exception.DatabaseException;
import com.empmgmt.model.Employee;
import com.empmgmt.ui.components.RoundedButton;
import com.empmgmt.ui.components.RoundedPanel;
import com.empmgmt.ui.components.StatusBadgeRenderer;
import com.empmgmt.ui.components.StyledField;
import com.empmgmt.ui.theme.AppTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class EmployeeListPanel extends JPanel {

    private final MainFrame frame;
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"));

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Name", "Email", "Department", "Designation", "Basic Salary", "Status"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private final StyledField searchField = new StyledField("Search by name, department, designation, email…");

    public EmployeeListPanel(MainFrame frame) {
        this.frame = frame;
        setOpaque(false);
        setLayout(new BorderLayout(0, 16));
        setBorder(AppTheme.padding(28));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTableCard(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Employees");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        RoundedButton addBtn = new RoundedButton("＋ Add Employee", RoundedButton.Style.PRIMARY);
        addBtn.addActionListener(e -> openForm(null));
        actions.add(addBtn);
        header.add(actions, BorderLayout.EAST);

        JPanel searchRow = new JPanel(new BorderLayout());
        searchRow.setOpaque(false);
        searchRow.setBorder(AppTheme.padding(14, 0, 0, 0));
        searchField.setPreferredSize(new Dimension(0, 38));
        searchField.getDocument().addDocumentListener(new SimpleDocListener(this::runSearch));
        searchRow.add(searchField, BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(searchRow, BorderLayout.SOUTH);
        return wrapper;
    }

    private RoundedPanel buildTableCard() {
        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BorderLayout(0, 10));

        table.setRowHeight(38);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(AppTheme.ACCENT.getRed(), AppTheme.ACCENT.getGreen(), AppTheme.ACCENT.getBlue(), 30));
        table.setSelectionForeground(AppTheme.TEXT_PRIMARY);
        table.getTableHeader().setBackground(AppTheme.BACKGROUND);
        table.getTableHeader().setForeground(AppTheme.TEXT_SECONDARY);
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        table.getColumnModel().getColumn(6).setCellRenderer(new StatusBadgeRenderer());
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        card.add(scroll, BorderLayout.CENTER);

        JPanel rowActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rowActions.setOpaque(false);
        RoundedButton editBtn = new RoundedButton("Edit Selected", RoundedButton.Style.SECONDARY);
        RoundedButton deleteBtn = new RoundedButton("Delete Selected", RoundedButton.Style.DANGER);
        editBtn.addActionListener(e -> editSelected());
        deleteBtn.addActionListener(e -> deleteSelected());
        rowActions.add(editBtn);
        rowActions.add(deleteBtn);
        card.add(rowActions, BorderLayout.SOUTH);

        return card;
    }

    public void refresh() { runSearch(); }

    private void runSearch() {
        String keyword = searchField.getText().trim();
        try {
            List<Employee> list = keyword.isEmpty() ? employeeDAO.findAll() : employeeDAO.search(keyword);
            model.setRowCount(0);
            for (Employee e : list) {
                model.addRow(new Object[]{
                        e.getId(), e.getFullName(), e.getEmail(), e.getDepartment(),
                        e.getDesignation(), currency.format(e.getBasicSalary()), e.getStatus().name()
                });
            }
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Could not load employees", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an employee first.", "Nothing selected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        try {
            Employee employee = employeeDAO.findById(id);
            if (employee == null) {
                JOptionPane.showMessageDialog(this, "That employee no longer exists.", "Not found", JOptionPane.WARNING_MESSAGE);
                refresh();
                return;
            }
            openForm(employee);
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Could not open employee", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an employee first.", "Nothing selected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        String name = (String) model.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete " + name + "? This also removes their payroll history. This cannot be undone.",
                "Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            employeeDAO.delete(id);
            frame.onDataChanged();
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Could not delete", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openForm(Employee existing) {
        EmployeeFormDialog dialog = new EmployeeFormDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), existing);
        dialog.setVisible(true);
        if (dialog.wasSaved()) {
            frame.onDataChanged();
        }
    }

    /** Tiny helper so a search box can react to typing without three anonymous classes. */
    private static class SimpleDocListener implements javax.swing.event.DocumentListener {
        private final Runnable action;
        SimpleDocListener(Runnable action) { this.action = action; }
        @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { action.run(); }
        @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { action.run(); }
        @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { action.run(); }
    }
}
