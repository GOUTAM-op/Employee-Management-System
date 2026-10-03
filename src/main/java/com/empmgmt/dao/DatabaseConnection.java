package com.empmgmt.dao;

import com.empmgmt.exception.DatabaseException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Central place that knows how to talk to MySQL.
 *
 * Credentials are read from resources/db.properties so nobody has to
 * hard-code a password into source. If the file is missing, sensible
 * localhost defaults are used instead.
 */
public final class DatabaseConnection {

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/employee_payroll_db?useSSL=false&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "root";

    private static String url = DEFAULT_URL;
    private static String user = DEFAULT_USER;
    private static String password = DEFAULT_PASSWORD;

    static {
        loadProperties();
    }

    private DatabaseConnection() { }

    private static void loadProperties() {
        try (InputStream in = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in != null) {
                Properties p = new Properties();
                p.load(in);
                url = p.getProperty("db.url", DEFAULT_URL);
                user = p.getProperty("db.user", DEFAULT_USER);
                password = p.getProperty("db.password", DEFAULT_PASSWORD);
            }
        } catch (IOException ignored) {
            // fall back to defaults silently — this is a convenience file, not a requirement
        }
    }

    /** Opens a brand-new connection. Caller is responsible for closing it (use try-with-resources). */
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Could not connect to the database. Check that MySQL is running and that "
                            + "resources/db.properties has the correct URL/username/password.", e);
        }
    }

    /** Creates the schema (tables) if they don't already exist. Safe to call every startup. */
    public static void initializeSchema() {
        String employees =
                "CREATE TABLE IF NOT EXISTS employees (" +
                "  id INT AUTO_INCREMENT PRIMARY KEY," +
                "  full_name VARCHAR(120) NOT NULL," +
                "  email VARCHAR(150) NOT NULL UNIQUE," +
                "  phone VARCHAR(20) NOT NULL," +
                "  department VARCHAR(80) NOT NULL," +
                "  designation VARCHAR(80) NOT NULL," +
                "  date_of_joining DATE NOT NULL," +
                "  basic_salary DECIMAL(12,2) NOT NULL," +
                "  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'" +
                ")";

        String payroll =
                "CREATE TABLE IF NOT EXISTS payroll_records (" +
                "  id INT AUTO_INCREMENT PRIMARY KEY," +
                "  employee_id INT NOT NULL," +
                "  month VARCHAR(15) NOT NULL," +
                "  year INT NOT NULL," +
                "  basic_salary DECIMAL(12,2) NOT NULL," +
                "  hra DECIMAL(12,2) NOT NULL," +
                "  da DECIMAL(12,2) NOT NULL," +
                "  medical_allowance DECIMAL(12,2) NOT NULL," +
                "  gross_earnings DECIMAL(12,2) NOT NULL," +
                "  provident_fund DECIMAL(12,2) NOT NULL," +
                "  professional_tax DECIMAL(12,2) NOT NULL," +
                "  income_tax DECIMAL(12,2) NOT NULL," +
                "  total_deductions DECIMAL(12,2) NOT NULL," +
                "  net_pay DECIMAL(12,2) NOT NULL," +
                "  generated_on DATE NOT NULL," +
                "  FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE," +
                "  UNIQUE KEY unique_payroll_run (employee_id, month, year)" +
                ")";

        try (Connection c = getConnection(); Statement st = c.createStatement()) {
            st.execute(employees);
            st.execute(payroll);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to initialize database schema.", e);
        }
    }
}
