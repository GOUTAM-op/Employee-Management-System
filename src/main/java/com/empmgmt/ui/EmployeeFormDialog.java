package com.empmgmt.ui;

import com.empmgmt.dao.EmployeeDAO;
import com.empmgmt.exception.DatabaseException;
import com.empmgmt.exception.ValidationException;
import com.empmgmt.model.Employee;
import com.empmgmt.service.ValidationService;
import com.empmgmt.ui.components.RoundedButton;
import com.empmgmt.ui.components.StyledField;
import com.empmgmt.ui.theme.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class EmployeeFormDialog extends JDialog {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final Employee existing; // null => creating a new employee
    private boolean saved = false;

    private final StyledField nameField = new StyledField("e.g. Priya Sharma");
    private final StyledField emailField = new StyledField("e.g. priya@example.com");
    private final StyledField phoneField = new StyledField("10-digit phone number");
    private final JComboBox<String> departmentBox = new JComboBox<>(
            new String[]{"Engineering", "Sales", "Marketing", "Human Resources", "Finance", "Operations", "Support"});
    private final StyledField designationField = new StyledField("e.g. Software Engineer");
    private final StyledField doJField = new StyledField("YYYY-MM-DD");
    private final StyledField salaryField = new StyledField("Monthly basic salary, e.g. 45000");
    private final JComboBox<Employee.Status> statusBox = new JComboBox<>(Employee.Status.values());

    public EmployeeFormDialog(JFrame owner, Employee existing) {
        super(owner, existing == null ? "Add Employee" : "Edit Employee", true);
        this.existing = existing;
        setSize(480, 620);
        setLocationRelativeTo(owner);
        setResizable(false);
        getContentPane().setBackground(AppTheme.CARD);
        setLayout(new BorderLayout());

        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        if (existing != null) populateFrom(existing);
    }

    public boolean wasSaved() { return saved; }

    private JPanel buildForm() {
        JPanel form = new JPanel();
        form.setBackground(AppTheme.CARD);
        form.setBorder(AppTheme.padding(24));
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        form.add(sectionLabel(existing == null ? "New Employee" : "Editing " + existing.getFullName()));
        form.add(Box.createVerticalStrut(16));

        form.add(fieldBlock("Full Name", nameField));
        form.add(fieldBlock("Email", emailField));
        form.add(fieldBlock("Phone", phoneField));
        form.add(comboBlock("Department", departmentBox));
        form.add(fieldBlock("Designation", designationField));
        form.add(fieldBlock("Date of Joining", doJField));
        form.add(fieldBlock("Basic Salary (₹ / month)", salaryField));
        form.add(comboBlock("Status", statusBox));

        return form;
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(AppTheme.FONT_HEADING);
        l.setForeground(AppTheme.TEXT_PRIMARY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JPanel fieldBlock(String labelText, StyledField field) {
        JPanel block = new JPanel();
        block.setOpaque(false);
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setAlignmentX(Component.LEFT_ALIGNMENT);
        block.setBorder(AppTheme.padding(0, 0, 12, 0));

        JLabel label = new JLabel(labelText);
        label.setFont(AppTheme.FONT_BODY_BOLD);
        label.setForeground(AppTheme.TEXT_SECONDARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.getErrorLabel().setAlignmentX(Component.LEFT_ALIGNMENT);

        block.add(label);
        block.add(Box.createVerticalStrut(4));
        block.add(field);
        block.add(field.getErrorLabel());
        return block;
    }

    private JPanel comboBlock(String labelText, JComboBox<?> combo) {
        JPanel block = new JPanel();
        block.setOpaque(false);
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setAlignmentX(Component.LEFT_ALIGNMENT);
        block.setBorder(AppTheme.padding(0, 0, 12, 0));

        JLabel label = new JLabel(labelText);
        label.setFont(AppTheme.FONT_BODY_BOLD);
        label.setForeground(AppTheme.TEXT_SECONDARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        combo.setAlignmentX(Component.LEFT_ALIGNMENT);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        combo.setFont(AppTheme.FONT_BODY);

        block.add(label);
        block.add(Box.createVerticalStrut(4));
        block.add(combo);
        return block;
    }

    private JPanel buildButtons() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        bar.setBackground(AppTheme.CARD);
        bar.setBorder(AppTheme.padding(0, 24, 12, 24));

        RoundedButton cancel = new RoundedButton("Cancel", RoundedButton.Style.SECONDARY);
        cancel.addActionListener(e -> dispose());

        RoundedButton save = new RoundedButton(existing == null ? "Add Employee" : "Save Changes", RoundedButton.Style.PRIMARY);
        save.addActionListener(e -> onSave());

        bar.add(cancel);
        bar.add(save);
        return bar;
    }

    private void populateFrom(Employee e) {
        nameField.setText(e.getFullName());
        emailField.setText(e.getEmail());
        phoneField.setText(e.getPhone());
        departmentBox.setSelectedItem(e.getDepartment());
        designationField.setText(e.getDesignation());
        doJField.setText(e.getDateOfJoining().toString());
        salaryField.setText(String.valueOf(e.getBasicSalary()));
        statusBox.setSelectedItem(e.getStatus());
    }

    private void onSave() {
        clearErrors();
        boolean valid = true;
        LocalDate dateOfJoining = null;

        try { ValidationService.validateName(nameField.getText()); }
        catch (ValidationException ex) { nameField.setError(ex.getMessage()); valid = false; }

        try { ValidationService.validateEmail(emailField.getText()); }
        catch (ValidationException ex) { emailField.setError(ex.getMessage()); valid = false; }

        try { ValidationService.validatePhone(phoneField.getText()); }
        catch (ValidationException ex) { phoneField.setError(ex.getMessage()); valid = false; }

        try { ValidationService.validateRequired(designationField.getText(), "Designation", "designation"); }
        catch (ValidationException ex) { designationField.setError(ex.getMessage()); valid = false; }

        try {
            dateOfJoining = LocalDate.parse(doJField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            ValidationService.validateDateOfJoining(dateOfJoining);
        } catch (DateTimeParseException parseEx) {
            doJField.setError("Use the format YYYY-MM-DD, e.g. 2024-06-01.");
            valid = false;
        } catch (ValidationException ex) {
            doJField.setError(ex.getMessage());
            valid = false;
        }

        try { ValidationService.validateSalary(salaryField.getText()); }
        catch (ValidationException ex) { salaryField.setError(ex.getMessage()); valid = false; }

        if (!valid) return;

        Employee employee = existing == null ? new Employee() : existing;
        employee.setFullName(nameField.getText().trim());
        employee.setEmail(emailField.getText().trim());
        employee.setPhone(phoneField.getText().trim());
        employee.setDepartment((String) departmentBox.getSelectedItem());
        employee.setDesignation(designationField.getText().trim());
        employee.setDateOfJoining(dateOfJoining);
        employee.setBasicSalary(Double.parseDouble(salaryField.getText().trim()));
        employee.setStatus((Employee.Status) statusBox.getSelectedItem());

        try {
            if (existing == null) employeeDAO.insert(employee);
            else employeeDAO.update(employee);
            saved = true;
            dispose();
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Could not save employee", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearErrors() {
        nameField.clearError();
        emailField.clearError();
        phoneField.clearError();
        designationField.clearError();
        doJField.clearError();
        salaryField.clearError();
    }
}
