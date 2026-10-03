package com.empmgmt.dao;

import com.empmgmt.exception.DatabaseException;
import com.empmgmt.model.PayrollRecord;

import java.sql.*;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class PayrollDAO {

    public PayrollRecord insert(PayrollRecord r) {
        String sql = "INSERT INTO payroll_records " +
                "(employee_id, month, year, basic_salary, hra, da, medical_allowance, gross_earnings, " +
                "provident_fund, professional_tax, income_tax, total_deductions, net_pay, generated_on) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getEmployeeId());
            ps.setString(2, r.getMonth().name());
            ps.setInt(3, r.getYear());
            ps.setDouble(4, r.getBasicSalary());
            ps.setDouble(5, r.getHra());
            ps.setDouble(6, r.getDa());
            ps.setDouble(7, r.getMedicalAllowance());
            ps.setDouble(8, r.getGrossEarnings());
            ps.setDouble(9, r.getProvidentFund());
            ps.setDouble(10, r.getProfessionalTax());
            ps.setDouble(11, r.getIncomeTax());
            ps.setDouble(12, r.getTotalDeductions());
            ps.setDouble(13, r.getNetPay());
            ps.setDate(14, Date.valueOf(r.getGeneratedOn()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) r.setId(keys.getInt(1));
            }
            return r;
        } catch (SQLIntegrityConstraintViolationException dup) {
            throw new DatabaseException(
                    "Payroll for " + r.getMonth() + " " + r.getYear() + " was already generated for this employee.", dup);
        } catch (SQLException ex) {
            throw new DatabaseException("Could not save the payroll record.", ex);
        }
    }

    public List<PayrollRecord> findByEmployee(int employeeId) {
        String sql = "SELECT p.*, e.full_name FROM payroll_records p " +
                "JOIN employees e ON e.id = p.employee_id WHERE p.employee_id=? ORDER BY p.year DESC, p.id DESC";
        List<PayrollRecord> result = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(map(rs));
            }
            return result;
        } catch (SQLException ex) {
            throw new DatabaseException("Could not load payroll history.", ex);
        }
    }

    public List<PayrollRecord> findAll() {
        String sql = "SELECT p.*, e.full_name FROM payroll_records p " +
                "JOIN employees e ON e.id = p.employee_id ORDER BY p.generated_on DESC, p.id DESC";
        List<PayrollRecord> result = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) result.add(map(rs));
            return result;
        } catch (SQLException ex) {
            throw new DatabaseException("Could not load payroll records.", ex);
        }
    }

    private PayrollRecord map(ResultSet rs) throws SQLException {
        PayrollRecord r = new PayrollRecord();
        r.setId(rs.getInt("id"));
        r.setEmployeeId(rs.getInt("employee_id"));
        r.setEmployeeName(rs.getString("full_name"));
        r.setMonth(Month.valueOf(rs.getString("month")));
        r.setYear(rs.getInt("year"));
        r.setBasicSalary(rs.getDouble("basic_salary"));
        r.setHra(rs.getDouble("hra"));
        r.setDa(rs.getDouble("da"));
        r.setMedicalAllowance(rs.getDouble("medical_allowance"));
        r.setGrossEarnings(rs.getDouble("gross_earnings"));
        r.setProvidentFund(rs.getDouble("provident_fund"));
        r.setProfessionalTax(rs.getDouble("professional_tax"));
        r.setIncomeTax(rs.getDouble("income_tax"));
        r.setTotalDeductions(rs.getDouble("total_deductions"));
        r.setNetPay(rs.getDouble("net_pay"));
        r.setGeneratedOn(rs.getDate("generated_on").toLocalDate());
        return r;
    }
}
