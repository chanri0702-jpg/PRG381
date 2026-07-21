/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.bc.cleaninginventory.model.dao.issuance;

import za.bc.cleaninginventory.database.DBConnection;
import za.bc.cleaninginventory.model.entity.Order;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author BC-STUDENT
 */
public class OrderDAO {
    public boolean placeOrderForRequest(int reqId, int prodId, int quantity, int supervisorEmpId) throws SQLException {
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String checkSql = "SELECT status FROM requests WHERE req_id = ? FOR UPDATE";
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, reqId);
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (!rs.next() || !"PENDING".equals(rs.getString("status"))) {
                        conn.rollback();
                        return false;
                    }
                }
            }

            BigDecimal price;
            String priceSql = "SELECT price FROM products WHERE prod_id = ?";
            try (PreparedStatement pricePs = conn.prepareStatement(priceSql)) {
                pricePs.setInt(1, prodId);
                try (ResultSet rs = pricePs.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        throw new SQLException("Product not found: " + prodId);
                    }
                    price = rs.getBigDecimal("price");
                }
            }
            BigDecimal total = price.multiply(BigDecimal.valueOf(quantity));

            int ordId;
            String orderSql = "INSERT INTO orders (emp_id) VALUES (?) RETURNING ord_id";
            try (PreparedStatement orderPs = conn.prepareStatement(orderSql)) {
                orderPs.setInt(1, supervisorEmpId);
                try (ResultSet rs = orderPs.executeQuery()) {
                    rs.next();
                    ordId = rs.getInt("ord_id");
                }
            }

            String itemSql = "INSERT INTO order_products (prod_id, ord_id, quantity, total) VALUES (?, ?, ?, ?)";
            try (PreparedStatement itemPs = conn.prepareStatement(itemSql)) {
                itemPs.setInt(1, prodId);
                itemPs.setInt(2, ordId);
                itemPs.setInt(3, quantity);
                itemPs.setBigDecimal(4, total);
                itemPs.executeUpdate();
            }
            
            String linkSql = "INSERT INTO request_orders (ord_id, req_id) VALUES (?, ?)";
            try (PreparedStatement linkPs = conn.prepareStatement(linkSql)) {
                linkPs.setInt(1, ordId);
                linkPs.setInt(2, reqId);
                linkPs.executeUpdate();
            }
            
            String approveSql = "UPDATE requests SET status = 'APPROVED' WHERE req_id = ?";
            try (PreparedStatement approvePs = conn.prepareStatement(approveSql)) {
                approvePs.setInt(1, reqId);
                approvePs.executeUpdate();
            }
            
            int campId;
            String campSql = "SELECT e.camp_id FROM requests r "
                    + "JOIN employees e ON r.emp_id = e.emp_id "
                    + "WHERE r.req_id = ?";
            try (PreparedStatement campPs = conn.prepareStatement(campSql)) {
                campPs.setInt(1, reqId);
                try (ResultSet rs = campPs.executeQuery()) {
                    if (!rs.next() || rs.getObject("camp_id") == null) {
                        conn.rollback();
                        throw new SQLException("Requesting employee has no assigned campus; cannot credit stock.");
                    }
                    campId = rs.getInt("camp_id");
                }
            }
            
            String stockSql = "INSERT INTO product_stock (prod_id, camp_id, stock) VALUES (?, ?, ?) "
                    + "ON CONFLICT (prod_id, camp_id) DO UPDATE SET stock = product_stock.stock + EXCLUDED.stock";
            try (PreparedStatement stockPs = conn.prepareStatement(stockSql)) {
                stockPs.setInt(1, prodId);
                stockPs.setInt(2, campId);
                stockPs.setInt(3, quantity);
                stockPs.executeUpdate();
            }
            
            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    e.addSuppressed(rollbackEx);
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public List<Order> getOrderHistory() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.ord_id, o.emp_id, o.ord_date, "
                + "op.prod_id, p.name AS product_name, op.quantity, op.total, "
                + "ro.req_id, r.status "
                + "FROM orders o "
                + "JOIN order_products op ON o.ord_id = op.ord_id "
                + "JOIN products p ON op.prod_id = p.prod_id "
                + "LEFT JOIN request_orders ro ON o.ord_id = ro.ord_id "
                + "LEFT JOIN requests r ON ro.req_id = r.req_id "
                + "ORDER BY o.ord_date DESC, o.ord_id DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Order o = new Order();
                o.setId(rs.getInt("ord_id"));
                o.setEmpID(rs.getInt("emp_id"));
                o.setOrderDate(rs.getDate("ord_date"));
                o.setProdID(rs.getInt("prod_id"));
                o.setName(rs.getString("product_name"));
                o.setQuantity(rs.getInt("quantity"));
                o.setTotal(rs.getBigDecimal("total"));
                
                String sql2 = "SELECT ro.req_id, r.status "
                + "FROM request_orders ro "
                + "LEFT JOIN requests r ON ro.req_id = r.req_id "
                + "WHERE ro.ord_id = ?";
                
                PreparedStatement ps2 = conn.prepareStatement(sql2);
                ps2.setInt(1, rs.getInt("ord_id"));
                ResultSet rs2 = ps2.executeQuery();
                
                List<Integer> reqIds = new ArrayList();
                List<String> reqStatus = new ArrayList();
                while(rs2.next()){
                    reqIds.add(rs2.getInt("req_id"));
                    reqStatus.add(rs2.getString("status"));
                }

                o.setReqIds(reqIds);
                o.setStatus(reqStatus);
                
                orders.add(o);
            }
        }
        return orders;
    }
}
