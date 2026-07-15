/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.bc.cleaninginventory.model.dao.issuance;
import za.bc.cleaninginventory.model.entity.Request;

import za.bc.cleaninginventory.database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author chanr
 */
public class RequestDAO {
    public void insertRequest(Request r) throws SQLException {
        String sql = "INSERT INTO requests (emp_id, prod_id, quantity, priority, description) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, r.getEmpID());
            ps.setString(2, r.getProdID());
            ps.setInt(3, r.getQuantity());
            ps.setString(4, r.getPriority());
            ps.setString(5, r.getDescription());

            ps.executeUpdate();
        }
    }

    // All requests belonging to one employee, most recent first, with the product name joined in
    public List<Request> findByEmployee(int empId) throws SQLException {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT r.req_id, r.emp_id, r.prod_id, p.name AS product_name, "
                + "r.quantity, r.status, r.priority, r.description, r.req_date "
                + "FROM requests r "
                + "JOIN products p ON r.prod_id = p.prod_id "
                + "WHERE r.emp_id = ? "
                + "ORDER BY r.req_date DESC, r.req_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, empId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapRow(rs));
                }
            }
        }
        return requests;
    }

    // Look up a single request by ID (used when loading the Edit form)
    public Request findById(int reqId) throws SQLException {
        String sql = "SELECT r.req_id, r.emp_id, r.prod_id, p.name AS product_name, "
                + "r.quantity, r.status, r.priority, r.description, r.req_date "
                + "FROM requests r "
                + "JOIN products p ON r.prod_id = p.prod_id "
                + "WHERE r.req_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reqId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null; // not found
    }

    // Update a request. Only allowed if it belongs to this employee AND is still PENDING.
    // Returns true if a row was actually updated.
    public boolean updateRequest(Request r) throws SQLException {
        String sql = "UPDATE requests SET prod_id = ?, quantity = ?, priority = ?, description = ? "
                + "WHERE req_id = ? AND emp_id = ? AND status = 'PENDING'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, r.getProdID());
            ps.setInt(2, r.getQuantity());
            ps.setString(3, r.getPriority());
            ps.setString(4, r.getDescription());
            ps.setString(5, r.getId());
            ps.setString(6, r.getEmpID());

            return ps.executeUpdate() > 0;
        }
    }

    // Delete a request. Only allowed if it belongs to this employee AND is still PENDING.
    // Returns true if a row was actually deleted.
    public boolean deleteRequest(int reqId, int empId) throws SQLException {
        String sql = "DELETE FROM requests WHERE req_id = ? AND emp_id = ? AND status = 'PENDING'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reqId);
            ps.setInt(2, empId);

            return ps.executeUpdate() > 0;
        }
    }

    private Request mapRow(ResultSet rs) throws SQLException {
        Request r = new Request();
        r.setEmpID("emp_id");
        r.setProdID("prod_id");
        r.setQuantity(rs.getInt("quantity"));
        r.setStatus(rs.getString("status"));
        r.setPriority(rs.getString("priority"));
        r.setDescription(rs.getString("description"));
        r.setReqDate(rs.getDate("req_date"));
        return r;
    }
}
