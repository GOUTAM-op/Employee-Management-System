package com.empmgmt.ui;

import com.empmgmt.dao.EmployeeDAO;
import com.empmgmt.dao.PayrollDAO;
import com.empmgmt.exception.DatabaseException;
import com.empmgmt.model.Employee;
import com.empmgmt.model.PayrollRecord;
import com.empmgmt.service.PayrollService;
import com.empmgmt.ui.components.RoundedButton;
import com.empmgmt.ui.components.RoundedPanel;
import com.empmgmt.ui.theme.AppTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Locale;

public class PayrollPanel extends JPanel {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final PayrollDAO payrollDAO = new PayrollDAO();
    private final PayrollService payrollService = new PayrollService();
    private final NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"));

    private final JComboBox<Employee> employeeBox = new JComboBox<>();
    private final JComboBox<Month> monthBox = new JComboBox<>(Month.values());
    private final JComboBox<Integer> yearBox = new JComboBox<>();

    private final JLabel grossValue = new JLabel("₹0.00");
    private final JLabel deductionsValue = new JLabel("₹0.00");
    private final JLabel netValue = new JLabel("₹0.00");

    private final DefaultTableModel historyModel = new DefaultTableModel(
            new Object[]{"Month", "Year", "Gross", "Deductions", "Net Pay", "Generated On"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    private PayrollRecord lastPreview;

    public PayrollPanel(MainFrame frame) {
        setOpaque(false);
        setLayout(new BorderLayout(0, 16));
        setBorder(AppTheme.padding(28));

        JLabel title = new JLabel("Payroll");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        add(title, BorderLayout.NORTH);

        JPanel body = new JPanel(new GridLayout(1, 2, 20, 0));
        body.setOpaque(false);
        body.add(buildGeneratorCard());
        body.add(buildHistoryCard());
        add(body, BorderLayout.CENTER);

        int currentYear = Year.now().getValue();
        for (int y = currentYear - 3; y <= currentYear + 1; y++) yearBox.addItem(y);
        yearBox.setSelectedItem(currentYear);
        monthBox.setSelectedItem(java.time.LocalDate.now().getMonth());
    }

    private RoundedPanel buildGeneratorCard() {
        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        card.add(heading("Generate Payslip"));
        card.add(Box.createVerticalStrut(14));

        card.add(row("Employee", employeeBox));
        card.add(Box.createVerticalStrut(10));

        JPanel monthYear = new JPanel(new GridLayout(1, 2, 10, 0));
        monthYear.setOpaque(false);
        monthYear.setAlignmentX(Component.LEFT_ALIGNMENT);
        monthYear.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        monthYear.add(monthBox);
        monthYear.add(yearBox);
        card.add(monthYear);
        card.add(Box.createVerticalStrut(16));

        RoundedButton previewBtn = new RoundedButton("Calculate Preview", RoundedButton.Style.SECONDARY);
        previewBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        previewBtn.addActionListener(e -> preview());
        card.add(previewBtn);
        card.add(Box.createVerticalStrut(18));

        card.add(breakdownRow("Gross Earnings", grossValue, AppTheme.TEXT_PRIMARY));
        card.add(breakdownRow("Total Deductions", deductionsValue, AppTheme.DANGER));
        card.add(Box.createVerticalStrut(6));
        card.add(new JSeparator());
        card.add(Box.createVerticalStrut(6));
        card.add(breakdownRow("Net Pay", netValue, AppTheme.SUCCESS));
        card.add(Box.createVerticalStrut(18));

        RoundedButton saveBtn = new RoundedButton("💾  Save Payslip", RoundedButton.Style.PRIMARY);
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> save());
        card.add(saveBtn);

        return card;
    }

    private RoundedPanel buildHistoryCard() {
        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BorderLayout(0, 10));
        card.add(heading("Payroll History"), BorderLayout.NORTH);

        JTable table = new JTable(historyModel);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.getTableHeader().setBackground(AppTheme.BACKGROUND);
        table.getTableHeader().setForeground(AppTheme.TEXT_SECONDARY);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JLabel heading(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppTheme.FONT_HEADING);
        l.setForeground(AppTheme.TEXT_PRIMARY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JPanel row(String labelText, JComponent field) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel label = new JLabel(labelText);
        label.setFont(AppTheme.FONT_BODY_BOLD);
        label.setForeground(AppTheme.TEXT_SECONDARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        p.add(label);
        p.add(Box.createVerticalStrut(4));
        p.add(field);
        return p;
    }

    private JPanel breakdownRow(String label, JLabel valueLabel, Color valueColor) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        JLabel l = new JLabel(label);
        l.setFont(AppTheme.FONT_BODY);
        l.setForeground(AppTheme.TEXT_SECONDARY);
        valueLabel.setFont(AppTheme.FONT_BODY_BOLD);
        valueLabel.setForeground(valueColor);
        p.add(l, BorderLayout.WEST);
        p.add(valueLabel, BorderLayout.EAST);
        return p;
    }

    public void refresh() {
        try {
            Employee selected = (Employee) employeeBox.getSelectedItem();
            employeeBox.removeAllItems();
            List<Employee> employees = employeeDAO.findAll();
            for (Employee e : employees) employeeBox.addItem(e);
            if (selected != null) {
                for (Employee e : employees) if (e.getId() == selected.getId()) employeeBox.setSelectedItem(e);
            }
            loadHistory();
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Could not load payroll screen", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadHistory() {
        historyModel.setRowCount(0);
        Employee selected = (Employee) employeeBox.getSelectedItem();
        if (selected == null) return;
        try {
            List<PayrollRecord> records = payrollDAO.findByEmployee(selected.getId());
            for (PayrollRecord r : records) {
                historyModel.addRow(new Object[]{
                        r.getMonth(), r.getYear(), currency.format(r.getGrossEarnings()),
                        currency.format(r.getTotalDeductions()), currency.format(r.getNetPay()), r.getGeneratedOn()
                });
            }
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Could not load payroll history", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preview() {
        Employee selected = (Employee) employeeBox.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Add an employee first, then generate their payslip here.",
                    "No employees yet", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Month month = (Month) monthBox.getSelectedItem();
        int year = (Integer) yearBox.getSelectedItem();
        lastPreview = payrollService.calculate(selected, month, year);

        grossValue.setText(currency.format(lastPreview.getGrossEarnings()));
        deductionsValue.setText(currency.format(lastPreview.getTotalDeductions()));
        netValue.setText(currency.format(lastPreview.getNetPay()));
    }

    private void save() {
        if (lastPreview == null) {
            JOptionPane.showMessageDialog(this, "Click \"Calculate Preview\" first.", "Nothing to save", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            payrollDAO.insert(lastPreview);
            JOptionPane.showMessageDialog(this,
                    "Payslip saved for " + lastPreview.getEmployeeName() + " — " + lastPreview.getMonth() + " " + lastPreview.getYear() + ".",
                    "Saved", JOptionPane.INFORMATION_MESSAGE);
            lastPreview = null;
            loadHistory();
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Could not save payslip", JOptionPane.ERROR_MESSAGE);
        }
    }
}
