package za.bc.cleaninginventory.model.dao.dashboard;

import za.bc.cleaninginventory.database.DBConnection;
import za.bc.cleaninginventory.model.dto.DashboardDTO;
import za.bc.cleaninginventory.model.dto.LowStockDTO;
import za.bc.cleaninginventory.model.dto.RecentRequestDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardDAO {

    private static final int LOW_STOCK_THRESHOLD = 10;

    public DashboardDTO getDashboardStatistics() {

        DashboardDTO dashboard = new DashboardDTO();

        try (Connection conn = DBConnection.getConnection()) {

            dashboard.setTotalProducts(
                    getCount(conn, "SELECT COUNT(*) FROM products"));

            dashboard.setTotalEmployees(
                    getCount(conn, "SELECT COUNT(*) FROM employees"));

            dashboard.setTotalSuppliers(
                    getCount(conn, "SELECT COUNT(*) FROM supplier_businesses"));

            dashboard.setPendingRequests(
                    getCount(conn, "SELECT COUNT(*) FROM requests WHERE status='PENDING'"));

            dashboard.setLowStockProducts(
                    getCount(conn, "SELECT COUNT(*) FROM product_stock WHERE stock < 10"));

            dashboard.setLowStockList(
                    getLowStockProducts(conn));

            dashboard.setRecentRequests(
                    getRecentRequests(conn));

        } catch (SQLException ex) {

            throw new RuntimeException(ex);

        }

        return dashboard;
    }

    private int getCount(Connection conn, String sql) throws SQLException {

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                return rs.getInt(1);

            }

        }

        return 0;
    }

    private List<LowStockDTO> getLowStockProducts(Connection conn) throws SQLException {

        List<LowStockDTO> lowStockProducts = new ArrayList<>();

        String sql = """
                SELECT
                    p.name AS product_name,
                    c.name AS campus_name,
                    ps.stock
                FROM product_stock ps
                INNER JOIN products p
                    ON ps.prod_id = p.prod_id
                INNER JOIN campuses c
                    ON ps.camp_id = c.camp_id
                WHERE ps.stock <= ?
                ORDER BY ps.stock ASC
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, LOW_STOCK_THRESHOLD);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    LowStockDTO product = new LowStockDTO();

                    product.setProductName(rs.getString("product_name"));
                    product.setCampus(rs.getString("campus_name"));
                    product.setStock(rs.getInt("stock"));

                    lowStockProducts.add(product);

                }

            }

        }

        return lowStockProducts;

    }

    private List<RecentRequestDTO> getRecentRequests(Connection conn) throws SQLException {

        List<RecentRequestDTO> recentRequests = new ArrayList<>();

        String sql = """
                SELECT
                    e.name,
                    e.surname,
                    p.name AS product_name,
                    r.quantity,
                    r.status,
                    r.priority
                FROM requests r
                INNER JOIN employees e
                    ON r.emp_id = e.emp_id
                INNER JOIN products p
                    ON r.prod_id = p.prod_id
                ORDER BY r.req_date DESC
                LIMIT 5
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                RecentRequestDTO request = new RecentRequestDTO();

                request.setEmployee(
                        rs.getString("name") + " " + rs.getString("surname"));

                request.setProduct(rs.getString("product_name"));

                request.setQuantity(rs.getInt("quantity"));

                request.setStatus(rs.getString("status"));

                request.setPriority(rs.getString("priority"));

                recentRequests.add(request);

            }

        }

        return recentRequests;

    }

}