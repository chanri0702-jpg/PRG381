/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.bc.cleaninginventory.model.dao.issuance;

import za.bc.cleaninginventory.database.ConnectionPool;
import za.bc.cleaninginventory.model.entity.Order;
import za.bc.cleaninginventory.model.entity.Product;


import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
/**
 *
 * @author BC-STUDENT
 */
public class OrderDAO {

    public static class OrderLine {
        public final Integer reqId;
        public final int prodId;
        public final int quantity;
        public final BigDecimal price;
        public final Integer manualCampId; 

        public OrderLine(Integer reqId, int prodId, int quantity, BigDecimal price, Integer manualCampId) {
            this.reqId = reqId;
            this.prodId = prodId;
            this.quantity = quantity;
            this.price = price;
            this.manualCampId = manualCampId;
        }
    }

    public boolean placeMultiProductOrder(List<OrderLine> lines, int supervisorEmpId) throws SQLException {
        if (lines == null || lines.isEmpty()) return false;
        Connection conn = null;

        try {
            conn = ConnectionPool.getConnection();
            conn.setAutoCommit(false);

            Integer busId = null;
            for (OrderLine line : lines) {
                String busSql = "SELECT bus_id FROM products WHERE prod_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(busSql)) {
                    ps.setInt(1, line.prodId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            throw new SQLException("Product not found: " + line.prodId);
                        }
                        int lineBus = rs.getInt("bus_id");
                        if (busId == null) {
                            busId = lineBus;
                        } else if (busId != lineBus) {
                            conn.rollback();
                            throw new SQLException("All items in one order must be from the same business.");
                        }
                    }
                }
            }

            int ordId;
            String orderSql = "INSERT INTO orders (emp_id) VALUES (?) RETURNING ord_id";
            try (PreparedStatement orderPs = conn.prepareStatement(orderSql)) {
                orderPs.setInt(1, supervisorEmpId);
                try (ResultSet rs = orderPs.executeQuery()) {
                    rs.next();
                    ordId = rs.getInt("ord_id");
                }
            }

            for (OrderLine line : lines) {
                int campId;

                if (line.reqId != null) {
                    String checkSql = "SELECT status FROM requests WHERE req_id = ? FOR UPDATE";
                    try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                        checkPs.setInt(1, line.reqId);
                        try (ResultSet rs = checkPs.executeQuery()) {
                            if (!rs.next() || !"PENDING".equals(rs.getString("status"))) {
                                continue; 
                            }
                        }
                    }
                } else if (line.manualCampId == null) {
                    conn.rollback();
                    throw new SQLException("A campus must be selected for manually added items.");
                }

                BigDecimal total = line.price.multiply(BigDecimal.valueOf(line.quantity));

                String itemSql = "INSERT INTO order_products (prod_id, ord_id, quantity, total) VALUES (?, ?, ?, ?)";
                try (PreparedStatement itemPs = conn.prepareStatement(itemSql)) {
                    itemPs.setInt(1, line.prodId);
                    itemPs.setInt(2, ordId);
                    itemPs.setInt(3, line.quantity);
                    itemPs.setBigDecimal(4, total);
                    itemPs.executeUpdate();
                }

                if (line.reqId != null) {
                    String linkSql = "INSERT INTO request_orders (ord_id, req_id) VALUES (?, ?)";
                    try (PreparedStatement linkPs = conn.prepareStatement(linkSql)) {
                        linkPs.setInt(1, ordId);
                        linkPs.setInt(2, line.reqId);
                        linkPs.executeUpdate();
                    }

                    String approveSql = "UPDATE requests SET status = 'APPROVED' WHERE req_id = ?";
                    try (PreparedStatement approvePs = conn.prepareStatement(approveSql)) {
                        approvePs.setInt(1, line.reqId);
                        approvePs.executeUpdate();
                    }

                    String campSql = "SELECT e.camp_id FROM requests r JOIN employees e ON r.emp_id = e.emp_id WHERE r.req_id = ?";
                    try (PreparedStatement campPs = conn.prepareStatement(campSql)) {
                        campPs.setInt(1, line.reqId);
                        try (ResultSet rs = campPs.executeQuery()) {
                            if (!rs.next() || rs.getObject("camp_id") == null) {
                                conn.rollback();
                                throw new SQLException("Requesting employee has no assigned campus; cannot credit stock.");
                            }
                            campId = rs.getInt("camp_id");
                        }
                    }
                } else {
                    campId = line.manualCampId;
                }

                String stockSql = "INSERT INTO product_stock (prod_id, camp_id, stock) VALUES (?, ?, ?) "
                        + "ON CONFLICT (prod_id, camp_id) DO UPDATE SET stock = product_stock.stock + EXCLUDED.stock";
                try (PreparedStatement stockPs = conn.prepareStatement(stockSql)) {
                    stockPs.setInt(1, line.prodId);
                    stockPs.setInt(2, campId);
                    stockPs.setInt(3, line.quantity);
                    stockPs.executeUpdate();
                }
            }

            conn.commit();
            return true;

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

    public List<Order> getOrderHistory() throws SQLException {
        Map<Integer, Order> ordersById = new LinkedHashMap<>();

        try (Connection conn = ConnectionPool.getConnection()) {

            String sql = "SELECT o.ord_id, o.emp_id, o.ord_date, "
                    + "op.prod_id, p.name AS product_name, op.quantity, op.total "
                    + "FROM orders o "
                    + "JOIN order_products op ON o.ord_id = op.ord_id "
                    + "JOIN products p ON op.prod_id = p.prod_id "
                    + "ORDER BY o.ord_date DESC, o.ord_id DESC, p.name";

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    int ordId = rs.getInt("ord_id");
                    Order o = ordersById.get(ordId);
                    if (o == null) {
                        o = new Order();
                        o.setId(ordId);
                        o.setEmpID(rs.getInt("emp_id"));
                        o.setOrderDate(rs.getDate("ord_date"));
                        ordersById.put(ordId, o);
                    }
                    o.addItem(new Order.OrderItem(
                            rs.getInt("prod_id"),
                            rs.getString("product_name"),
                            rs.getInt("quantity"),
                            rs.getBigDecimal("total")
                    ));
                }
            }

            String reqSql = "SELECT ro.req_id, r.status FROM request_orders ro "
                    + "LEFT JOIN requests r ON ro.req_id = r.req_id WHERE ro.ord_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(reqSql)) {
                for (Order o : ordersById.values()) {
                    ps.setInt(1, o.getId());
                    try (ResultSet rs = ps.executeQuery()) {
                        List<Integer> reqIds = new ArrayList<>();
                        List<String> statuses = new ArrayList<>();
                        while (rs.next()) {
                            reqIds.add(rs.getInt("req_id"));
                            statuses.add(rs.getString("status"));
                        }
                        o.setReqIds(reqIds);
                        o.setStatus(statuses);
                    }
                }
            }
        }

        return new ArrayList<>(ordersById.values());
    }

    public Map<Integer, String> getAllCampuses() throws SQLException {
        Map<Integer, String> campuses = new LinkedHashMap<>();
        String sql = "SELECT camp_id, name FROM campuses ORDER BY name";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                campuses.put(rs.getInt("camp_id"), rs.getString("name"));
            }
        }
        return campuses;
    }

    public Map<String, List<Product>> getAllProductsGroupedByBusiness() throws SQLException {
        Map<String, List<Product>> result = new LinkedHashMap<>();
        String sql = "SELECT p.prod_id, p.bus_id, p.name, p.price, p.description, sb.name AS business_name "
                + "FROM products p JOIN supplier_businesses sb ON p.bus_id = sb.bus_id "
                + "ORDER BY sb.name, p.name";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("prod_id"));
                p.setBussID(rs.getInt("bus_id"));
                p.setName(rs.getString("name"));
                p.setPrice(rs.getBigDecimal("price"));
                p.setDescription(rs.getString("description"));
                p.setBusinessName(rs.getString("business_name"));
                result.computeIfAbsent(p.getBusinessName(), k -> new ArrayList<>()).add(p);
            }
        }
        return result;
    }
}
