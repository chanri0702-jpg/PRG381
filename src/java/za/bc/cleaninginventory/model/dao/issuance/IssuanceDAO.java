/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.bc.cleaninginventory.model.dao.issuance;

import za.bc.cleaninginventory.database.ConnectionPool;
import za.bc.cleaninginventory.model.entity.Issuance;
import za.bc.cleaninginventory.model.dto.ProductStockDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author chanr
 */
public class IssuanceDAO {
    public IssueResult issueStock(int prodId, int cleanerId, int quantity, int issuedByEmpId) throws SQLException {
        Connection conn = null;

        try {
            conn = ConnectionPool.getConnection();
            conn.setAutoCommit(false);

            int campId;
            String campSql = "SELECT camp_id FROM cleaners WHERE cleaner_id = ?";
            try (PreparedStatement campPs = conn.prepareStatement(campSql)) {
                campPs.setInt(1, cleanerId);
                try (ResultSet rs = campPs.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return IssueResult.CLEANER_NOT_FOUND;
                    }
                    campId = rs.getInt("camp_id");
                }
            }

            int currentStock;
            String stockSql = "SELECT stock FROM product_stock WHERE prod_id = ? AND camp_id = ? FOR UPDATE";
            try (PreparedStatement stockPs = conn.prepareStatement(stockSql)) {
                stockPs.setInt(1, prodId);
                stockPs.setInt(2, campId);
                try (ResultSet rs = stockPs.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return IssueResult.INSUFFICIENT_STOCK;
                    }
                    currentStock = rs.getInt("stock");
                }
            }

            if (currentStock < quantity) {
                conn.rollback();
                return IssueResult.INSUFFICIENT_STOCK;
            }

            String deductSql = "UPDATE product_stock SET stock = stock - ? WHERE prod_id = ? AND camp_id = ?";
            try (PreparedStatement deductPs = conn.prepareStatement(deductSql)) {
                deductPs.setInt(1, quantity);
                deductPs.setInt(2, prodId);
                deductPs.setInt(3, campId);
                deductPs.executeUpdate();
            }

            String issueSql = "INSERT INTO issuance (cleaner_id, prod_id, issued_by, quantity) VALUES (?, ?, ?, ?)";
            try (PreparedStatement issuePs = conn.prepareStatement(issueSql)) {
                issuePs.setInt(1, cleanerId);
                issuePs.setInt(2, prodId);
                issuePs.setInt(3, issuedByEmpId);
                issuePs.setInt(4, quantity);
                issuePs.executeUpdate();
            }

            conn.commit();
            return IssueResult.SUCCESS;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException rollbackEx) { e.addSuppressed(rollbackEx); }
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public List<Issuance> getIssuanceHistory() throws SQLException {
        List<Issuance> history = new ArrayList<>();
        String sql = "SELECT i.issuance_id, i.cleaner_id, c.name AS cleaner_name, c.surname AS cleaner_surname, "
                + "i.prod_id, p.name AS product_name, i.issued_by, e.name AS emp_name, e.surname AS emp_surname, "
                + "i.quantity, i.issue_date "
                + "FROM issuance i "
                + "JOIN cleaners c ON i.cleaner_id = c.cleaner_id "
                + "JOIN products p ON i.prod_id = p.prod_id "
                + "JOIN employees e ON i.issued_by = e.emp_id "
                + "ORDER BY i.issue_date DESC";

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Issuance i = new Issuance();
                i.setIssuanceId(rs.getInt("issuance_id"));
                i.setCleanerId(rs.getInt("cleaner_id"));
                i.setCleanerName(rs.getString("cleaner_name") + " " + rs.getString("cleaner_surname"));
                i.setProdId(rs.getInt("prod_id"));
                i.setProductName(rs.getString("product_name"));
                i.setIssuedBy(rs.getInt("issued_by"));
                i.setIssuedByName(rs.getString("emp_name") + " " + rs.getString("emp_surname"));
                i.setQuantity(rs.getInt("quantity"));
                i.setIssueDate(rs.getTimestamp("issue_date"));
                history.add(i);
            }
        }
        return history;
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

    /** Products currently in stock (>0) at the given campus, for the "issue stock" dropdown. */
    public List<ProductStockDTO> getProductStockForCampus(int campId) throws SQLException {
        List<ProductStockDTO> stockList = new ArrayList<>();
        String sql = "SELECT ps.prod_id, p.name, ps.stock "
                + "FROM product_stock ps JOIN products p ON ps.prod_id = p.prod_id "
                + "WHERE ps.camp_id = ? AND ps.stock > 0 "
                + "ORDER BY p.name";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, campId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stockList.add(new ProductStockDTO(
                            rs.getInt("prod_id"),
                            rs.getString("name"),
                            rs.getInt("stock")
                    ));
                }
            }
        }
        return stockList;
    }

    public enum IssueResult {
        SUCCESS,
        CLEANER_NOT_FOUND,
        INSUFFICIENT_STOCK
    }
}
