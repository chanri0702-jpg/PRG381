/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.bc.cleaninginventory.model.dao.issuance;
import za.bc.cleaninginventory.model.entity.Request;

import za.bc.cleaninginventory.database.ConnectionPool;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author chanr
 */
public class RequestDAO {

    public int insertRequest(Request r) throws SQLException {
        String sql = "INSERT INTO requests (emp_id, prod_id, quantity, priority, description) "
                + "VALUES (?, ?, ?, ?, ?) RETURNING req_id";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getEmpID());
            ps.setInt(2, r.getProdID());
            ps.setInt(3, r.getQuantity());
            ps.setString(4, r.getPriority());
            ps.setString(5, r.getDescription());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("req_id");
            }
        }
    }

    public UrgentNotificationInfo getNotificationInfo(int reqId) throws SQLException {
        String sql = "SELECT e.name, e.surname, e.camp_id, p.name AS product_name, r.quantity, r.description "
                + "FROM requests r "
                + "JOIN employees e ON r.emp_id = e.emp_id "
                + "JOIN products p ON r.prod_id = p.prod_id "
                + "WHERE r.req_id = ?";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reqId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UrgentNotificationInfo(
                            rs.getString("name") + " " + rs.getString("surname"),
                            rs.getInt("camp_id"),
                            rs.getString("product_name"),
                            rs.getInt("quantity"),
                            rs.getString("description")
                    );
                }
            }
        }
        return null;
    }

    public List<String> getStorekeeperEmailsByCampus(int campId) throws SQLException {
        List<String> emails = new ArrayList<>();
        String sql = "SELECT email FROM employees WHERE camp_id = ? AND role = 'STOREKEEPER'";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, campId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) emails.add(rs.getString("email"));
            }
        }
        return emails;
    }

    public static class UrgentNotificationInfo {
        public final String requesterName;
        public final int campId;
        public final String productName;
        public final int quantity;
        public final String description;

        public UrgentNotificationInfo(String requesterName, int campId, String productName, int quantity, String description) {
            this.requesterName = requesterName;
            this.campId = campId;
            this.productName = productName;
            this.quantity = quantity;
            this.description = description;
        }
    }

    public List<Request> findByEmployee(int empId) throws SQLException {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT r.req_id, r.emp_id, r.prod_id, p.name AS product_name, "
                + "r.quantity, r.status, r.priority, r.description, r.req_date "
                + "FROM requests r JOIN products p ON r.prod_id = p.prod_id "
                + "WHERE r.emp_id = ? ORDER BY r.req_date DESC, r.req_id DESC";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, empId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) requests.add(mapRow(rs));
            }
        }
        return requests;
    }

    public Request findById(int reqId) throws SQLException {
        String sql = "SELECT r.req_id, r.emp_id, r.prod_id, p.name AS product_name, "
                + "r.quantity, r.status, r.priority, r.description, r.req_date "
                + "FROM requests r JOIN products p ON r.prod_id = p.prod_id "
                + "WHERE r.req_id = ?";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reqId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public boolean updateRequest(Request r) throws SQLException {
        String sql = "UPDATE requests SET prod_id = ?, quantity = ?, priority = ?, description = ? "
                + "WHERE req_id = ? AND emp_id = ? AND status = 'PENDING'";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getProdID());
            ps.setInt(2, r.getQuantity());
            ps.setString(3, r.getPriority());
            ps.setString(4, r.getDescription());
            ps.setInt(5, r.getId());
            ps.setInt(6, r.getEmpID());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteRequest(int reqId, int empId) throws SQLException {
        String sql = "DELETE FROM requests WHERE req_id = ? AND emp_id = ? AND status = 'PENDING'";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reqId);
            ps.setInt(2, empId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean rejectRequest(int reqId) throws SQLException {
        String sql = "UPDATE requests SET status = 'REJECTED' WHERE req_id = ? AND status = 'PENDING'";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reqId);
            return ps.executeUpdate() > 0;
        }
    }

    public Integer getEmployeeCampId(int empId) throws SQLException {
        String sql = "SELECT camp_id FROM employees WHERE emp_id = ?";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, empId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int campId = rs.getInt("camp_id");
                    return rs.wasNull() ? null : campId;
                }
            }
        }
        return null;
    }

    public List<Request> findAllPendingByCampus(int campId) throws SQLException {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT r.req_id, r.emp_id, r.prod_id, p.name AS product_name, p.price, "
                + "p.bus_id, sb.name AS business_name, "
                + "r.quantity, r.status, r.priority, r.description, r.req_date, "
                + "e.name AS emp_name, e.surname AS emp_surname "
                + "FROM requests r "
                + "JOIN products p ON r.prod_id = p.prod_id "
                + "JOIN supplier_businesses sb ON p.bus_id = sb.bus_id "
                + "JOIN employees e ON r.emp_id = e.emp_id "
                + "WHERE r.status = 'PENDING' AND e.camp_id = ? "
                + "ORDER BY sb.name, r.req_date ASC, r.req_id ASC";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, campId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Request r = mapRow(rs);
                    r.setRequesterName(rs.getString("emp_name") + " " + rs.getString("emp_surname"));
                    r.setBusId(rs.getInt("bus_id"));
                    r.setBusinessName(rs.getString("business_name"));
                    r.setPrice(rs.getBigDecimal("price"));
                    requests.add(r);
                }
            }
        }
        return requests;
    }

    public List<Request> findAllApproved() throws SQLException {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT r.req_id, r.emp_id, r.prod_id, p.name AS product_name, "
                + "r.quantity, r.status, r.priority, r.description, r.req_date, "
                + "e.name AS emp_name, e.surname AS emp_surname "
                + "FROM requests r "
                + "JOIN products p ON r.prod_id = p.prod_id "
                + "JOIN employees e ON r.emp_id = e.emp_id "
                + "WHERE r.status = 'APPROVED' "
                + "ORDER BY r.req_date ASC, r.req_id ASC";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Request r = mapRow(rs);
                r.setRequesterName(rs.getString("emp_name") + " " + rs.getString("emp_surname"));
                requests.add(r);
            }
        }
        return requests;
    }

    private Request mapRow(ResultSet rs) throws SQLException {
        Request r = new Request();
        r.setId(rs.getInt("req_id"));
        r.setEmpID(rs.getInt("emp_id"));
        r.setProdID(rs.getInt("prod_id"));
        r.setName(rs.getString("product_name"));
        r.setQuantity(rs.getInt("quantity"));
        r.setStatus(rs.getString("status"));
        r.setPriority(rs.getString("priority"));
        r.setDescription(rs.getString("description"));
        r.setReqDate(rs.getDate("req_date"));
        return r;
    }
}
