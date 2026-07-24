package za.bc.cleaninginventory.model.dao.auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import za.bc.cleaninginventory.database.DBConnection;
import za.bc.cleaninginventory.model.entity.Employee;

/**
 * Data Access Object for Employee database operations.
 */
public class EmployeeDAO {

    /**
     * Retrieves an employee by their email.
     * @param email the email to search for
     * @return Employee object or null if not found
     */
    public Employee getEmployeeByEmail(String email) {
        String sql = "SELECT emp_id, camp_id, name, surname, role, password, email " +
                     "FROM employees " +
                     "WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Employee(
                        rs.getInt("emp_id"),
                        rs.getInt("camp_id"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getString("role"),
                        rs.getString("password"),
                        rs.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Inserts a new employee record.
     * @param employee the employee to create
     * @return true if created successfully, false otherwise
     */
    public boolean createEmployee(Employee employee) {
        String sql = "INSERT INTO employees (camp_id, name, surname, role, password, email) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"emp_id"})) {
            ps.setInt(1, employee.getCampId());
            ps.setString(2, employee.getName());
            ps.setString(3, employee.getSurname());
            ps.setString(4, employee.getRole());
            ps.setString(5, employee.getPassword());
            ps.setString(6, employee.getEmail());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        employee.setEmpId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if an email address already exists.
     * @param email the email to check
     * @return true if exists, false otherwise
     */
    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM employees WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
