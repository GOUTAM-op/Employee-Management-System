package com.empmgmt.ui;

import com.empmgmt.dao.EmployeeDAO;
import com.empmgmt.exception.DatabaseException;
import com.empmgmt.model.Employee;
import com.empmgmt.ui.components.RoundedPanel;
import com.empmgmt.ui.components.StatCard;
import com.empmgmt.ui.theme.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class DashboardPanel extends JPanel {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final NumberFormat currency;

    private final StatCard totalEmployeesCard = new StatCard("👥", "Total Employees", "0", AppTheme.ACCENT);
    private final StatCard activeCard = new StatCard("✅", "Active Employees", "0", AppTheme.SUCCESS);
    private final StatCard departmentsCard = new StatCard("🏢", "Departments", "0", AppTheme.WARNING);
    private final StatCard payrollCard = new StatCard("💰", "Monthly Payroll (Basic)", "₹0", AppTheme.ACCENT_DARK);

    private final DepartmentChartPanel chartPanel = new DepartmentChartPanel();

    public DashboardPanel(MainFrame frame) {
        Locale in = Locale.forLanguageTag("en-IN");
        currency = NumberFormat.getCurrencyInstance(in);

        setOpaque(false);
        setLayout(new BorderLayout(0, 20));
        setBorder(AppTheme.padding(28));

        JLabel title = new JLabel("Dashboard");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        add(title, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 4, 18, 0));
        cards.setOpaque(false);
        cards.add(totalEmployeesCard);
        cards.add(activeCard);
        cards.add(departmentsCard);
        cards.add(payrollCard);

        JPanel center = new JPanel(new BorderLayout(0, 18));
        center.setOpaque(false);
        center.add(cards, BorderLayout.NORTH);

        RoundedPanel chartCard = new RoundedPanel();
        chartCard.setLayout(new BorderLayout());
        JLabel chartTitle = new JLabel("Employees by Department");
        chartTitle.setFont(AppTheme.FONT_HEADING);
        chartTitle.setForeground(AppTheme.TEXT_PRIMARY);
        chartTitle.setBorder(AppTheme.padding(0, 0, 12, 0));
        chartCard.add(chartTitle, BorderLayout.NORTH);
        chartCard.add(chartPanel, BorderLayout.CENTER);
        center.add(chartCard, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    public void refresh() {
        try {
            List<Employee> all = employeeDAO.findAll();
            long active = all.stream().filter(e -> e.getStatus() == Employee.Status.ACTIVE).count();
            Map<String, Integer> byDept = new TreeMap<>();
            double totalBasic = 0;
            for (Employee e : all) {
                byDept.merge(e.getDepartment(), 1, Integer::sum);
                totalBasic += e.getBasicSalary();
            }

            totalEmployeesCard.setValue(String.valueOf(all.size()));
            activeCard.setValue(String.valueOf(active));
            departmentsCard.setValue(String.valueOf(byDept.size()));
            payrollCard.setValue(currency.format(totalBasic));
            chartPanel.setData(byDept);
        } catch (DatabaseException ex) {
            chartPanel.setData(new TreeMap<>());
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(), "Could not load dashboard data", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Minimal hand-drawn horizontal bar chart — no charting library required. */
    private static class DepartmentChartPanel extends JPanel {
        private Map<String, Integer> data = new TreeMap<>();

        void setData(Map<String, Integer> data) { this.data = data; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (data.isEmpty()) {
                g2.setColor(AppTheme.TEXT_SECONDARY);
                g2.setFont(AppTheme.FONT_BODY);
                g2.drawString("No employee data yet — add employees to see this chart.", 8, 24);
                g2.dispose();
                return;
            }

            int max = data.values().stream().max(Integer::compareTo).orElse(1);
            int rowHeight = 34;
            int y = 10;
            int labelWidth = 140;
            int chartRight = getWidth() - 60;

            g2.setFont(AppTheme.FONT_BODY);
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                g2.setColor(AppTheme.TEXT_PRIMARY);
                g2.drawString(trim(entry.getKey(), 18), 0, y + 20);

                int barMaxWidth = chartRight - labelWidth;
                int barWidth = (int) (barMaxWidth * (entry.getValue() / (double) max));
                barWidth = Math.max(barWidth, 6);

                g2.setColor(AppTheme.ACCENT);
                g2.fillRoundRect(labelWidth, y + 6, barWidth, 16, 8, 8);

                g2.setColor(AppTheme.TEXT_SECONDARY);
                g2.drawString(String.valueOf(entry.getValue()), labelWidth + barWidth + 8, y + 20);

                y += rowHeight;
            }
            g2.dispose();
        }

        private String trim(String s, int max) {
            return s.length() <= max ? s : s.substring(0, max - 1) + "…";
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(400, Math.max(200, data.size() * 34 + 20));
        }
    }
}
