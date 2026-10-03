package com.empmgmt.dao;

import com.empmgmt.exception.DatabaseException;
import com.empmgmt.model.Employee;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * All CRUD access to the `employees` table lives here, and only here —
 * no other class in the app is allowed to write raw SQL against it.
 */
public class EmployeeDAO {

    public Employee insert(Employee e) {
        String sql = "INSERT INTO employees " +
                "(full_name, email, phone, department, designation, date_of_joining, basic_salary, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, e);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) e.setId(keys.getInt(1));
            }
            return e;
        } catch (SQLIntegrityConstraintViolationException dup) {
            throw new DatabaseException("An employee with that email already exists.", dup);
        } catch (SQLException ex) {
            throw new DatabaseException("Could not save the new employee.", ex);
        }
    }

    public void update(Employee e) {
        String sql = "UPDATE employees SET full_name=?, email=?, phone=?, department=?, " +
                "designation=?, date_of_joining=?, basic_salary=?, status=? WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, e);
            ps.setInt(9, e.getId());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new DatabaseException("Employee #" + e.getId() + " no longer exists.", null);
            }
        } catch (SQLIntegrityConstraintViolationException dup) {
            throw new DatabaseException("Another employee already uses that email.", dup);
        } catch (SQLException ex) {
            throw new DatabaseException("Could not update employee #" + e.getId() + ".", ex);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM employees WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DatabaseException("Could not delete employee #" + id + ".", ex);
        }
    }

    public Employee findById(int id) {
        String sql = "SELECT * FROM employees WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException ex) {
            throw new DatabaseException("Could not fetch employee #" + id + ".", ex);
        }
    }

    public List<Employee> findAll() {
        String sql = "SELECT * FROM employees ORDER BY id DESC";
        List<Employee> result = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) result.add(map(rs));
            return result;
        } catch (SQLException ex) {
            throw new DatabaseException("Could not load the employee list.", ex);
        }
    }

    public List<Employee> search(String keyword) {
        String sql = "SELECT * FROM employees WHERE full_name LIKE ? OR department LIKE ? " +
                "OR designation LIKE ? OR email LIKE ? ORDER BY id DESC";
        String like = "%" + keyword + "%";
        List<Employee> result = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(map(rs));
            }
            return result;
        } catch (SQLException ex) {
            throw new DatabaseException("Search failed.", ex);
        }
    }

    // --- helpers ---
    private void bind(PreparedStatement ps, Employee e) throws SQLException {
        ps.setString(1, e.getFullName());
        ps.setString(2, e.getEmail());
        ps.setString(3, e.getPhone());
        ps.setString(4, e.getDepartment());
        ps.setString(5, e.getDesignation());
        ps.setDate(6, Date.valueOf(e.getDateOfJoining()));
        ps.setDouble(7, e.getBasicSalary());
        ps.setString(8, e.getStatus().name());
    }

    private Employee map(ResultSet rs) throws SQLException {
        LocalDate doj = rs.getDate("date_of_joining").toLocalDate();
        return new Employee(
                rs.getInt("id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("department"),
                rs.getString("designation"),
                doj,
                rs.getDouble("basic_salary"),
                Employee.Status.valueOf(rs.getString("status"))
        );
    }
}
