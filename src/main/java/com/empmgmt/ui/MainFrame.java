package com.empmgmt.ui;

import com.empmgmt.ui.components.SidebarButton;
import com.empmgmt.ui.theme.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/** The application shell: dark sidebar on the left, swappable content area on the right. */
public class MainFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel content = new JPanel(cardLayout);
    private final Map<String, SidebarButton> navButtons = new LinkedHashMap<>();

    private DashboardPanel dashboardPanel;
    private EmployeeListPanel employeeListPanel;
    private PayrollPanel payrollPanel;

    public MainFrame() {
        super("Employee Management & Payroll System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1080, 680));
        setSize(1200, 740);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);

        showScreen("dashboard");
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(AppTheme.SIDEBAR);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(AppTheme.padding(20, 0, 20, 0));

        JLabel brand = new JLabel("  💼  PayrollPro");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        brand.setBorder(AppTheme.padding(0, 12, 24, 0));
        sidebar.add(brand);

        sidebar.add(navItem(sidebar, "dashboard", "📊", "Dashboard"));
        sidebar.add(navItem(sidebar, "employees", "👥", "Employees"));
        sidebar.add(navItem(sidebar, "payroll", "💰", "Payroll"));

        sidebar.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("<html>Java · JDBC · MySQL<br>v1.0</html>");
        footer.setFont(AppTheme.FONT_SMALL);
        footer.setForeground(new Color(0x94, 0xA3, 0xB8));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        footer.setBorder(AppTheme.padding(12, 22, 0, 0));
        sidebar.add(footer);

        return sidebar;
    }

    private SidebarButton navItem(JPanel sidebar, String key, String icon, String text) {
        SidebarButton btn = new SidebarButton(icon, text);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { showScreen(key); }
        });
        navButtons.put(key, btn);
        return btn;
    }

    private JPanel buildContent() {
        content.setBackground(AppTheme.BACKGROUND);

        dashboardPanel = new DashboardPanel(this);
        employeeListPanel = new EmployeeListPanel(this);
        payrollPanel = new PayrollPanel(this);

        content.add(dashboardPanel, "dashboard");
        content.add(employeeListPanel, "employees");
        content.add(payrollPanel, "payroll");
        return content;
    }

    public void showScreen(String key) {
        navButtons.forEach((k, b) -> b.setActive(k.equals(key)));
        cardLayout.show(content, key);
        if (key.equals("dashboard")) dashboardPanel.refresh();
        if (key.equals("employees")) employeeListPanel.refresh();
        if (key.equals("payroll")) payrollPanel.refresh();
    }

    /** Lets sub-panels notify the shell that underlying data changed (e.g. after adding an employee). */
    public void onDataChanged() {
        dashboardPanel.refresh();
        employeeListPanel.refresh();
        payrollPanel.refresh();
    }
}
