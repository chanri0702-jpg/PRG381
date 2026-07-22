package za.bc.cleaninginventory.model.dao.report;

import za.bc.cleaninginventory.database.DBConnection;
import za.bc.cleaninginventory.model.dto.ReportDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    public List<ReportDTO> getProductsReport() {

        List<ReportDTO> products = new ArrayList<>();

        String sql = """
                SELECT
                    p.prod_id,
                    p.name AS product_name,
                    sb.name AS supplier,
                    p.price,
                    COALESCE(SUM(ps.stock),0) AS stock
                FROM products p
                INNER JOIN supplier_businesses sb
                    ON p.bus_id = sb.bus_id
                LEFT JOIN product_stock ps
                    ON p.prod_id = ps.prod_id
                GROUP BY
                    p.prod_id,
                    p.name,
                    sb.name,
                    p.price
                ORDER BY p.name;
                """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {

                ReportDTO report = new ReportDTO();

                report.setProductId(rs.getInt("prod_id"));
                report.setProductName(rs.getString("product_name"));
                report.setSupplier(rs.getString("supplier"));
                report.setPrice(rs.getDouble("price"));
                report.setStock(rs.getInt("stock"));

                products.add(report);

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return products;

    }
    
    
    public List<ReportDTO> getRequestsReport() {

        List<ReportDTO> requests = new ArrayList<>();

        String sql = """
                SELECT
                    e.name,
                    e.surname,
                    p.name AS product_name,
                    r.quantity,
                    r.status,
                    r.priority,
                    r.req_date
                FROM requests r
                INNER JOIN employees e
                    ON r.emp_id = e.emp_id
                INNER JOIN products p
                    ON r.prod_id = p.prod_id
                ORDER BY r.req_date DESC;
                """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {

                ReportDTO report = new ReportDTO();

                report.setEmployee(
                        rs.getString("name") + " " +
                        rs.getString("surname"));

                report.setProductName(
                        rs.getString("product_name"));

                report.setQuantity(
                        rs.getInt("quantity"));

                report.setStatus(
                        rs.getString("status"));

                report.setPriority(
                        rs.getString("priority"));

                report.setRequestDate(
                        rs.getDate("req_date").toString());

                requests.add(report);

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return requests;

    }
    
    
    
    public List<ReportDTO> getStockReport() {

        List<ReportDTO> stock = new ArrayList<>();

        String sql = """
                SELECT
                    p.name AS product_name,
                    c.name AS campus,
                    ps.stock
                FROM product_stock ps
                INNER JOIN products p
                    ON ps.prod_id = p.prod_id
                INNER JOIN campuses c
                    ON ps.camp_id = c.camp_id
                ORDER BY c.name, p.name;
                """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {

                ReportDTO report = new ReportDTO();

                report.setProductName(rs.getString("product_name"));
                report.setCampus(rs.getString("campus"));
                report.setStock(rs.getInt("stock"));

                stock.add(report);

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return stock;
    }
    
    
    public List<ReportDTO> getSuppliersReport() {

        List<ReportDTO> suppliers = new ArrayList<>();

        String sql = """
                SELECT
                    sb.name,
                    sb.description,
                    so.city,
                    so.province
                FROM supplier_businesses sb
                INNER JOIN supplier_offices so
                    ON sb.bus_id = so.bus_id
                ORDER BY sb.name;
                """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {

                ReportDTO report = new ReportDTO();

                report.setSupplier(rs.getString("name"));
                report.setSupplierDescription(rs.getString("description"));
                report.setCity(rs.getString("city"));
                report.setProvince(rs.getString("province"));

                suppliers.add(report);

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return suppliers;
    }
    
    
    public List<ReportDTO> getOrdersReport() {

        List<ReportDTO> orders = new ArrayList<>();

        String sql = """
                SELECT
                    o.ord_id,
                    o.ord_date,
                    p.name AS product_name,
                    op.quantity,
                    op.total
                FROM orders o
                INNER JOIN order_products op
                    ON o.ord_id = op.ord_id
                INNER JOIN products p
                    ON op.prod_id = p.prod_id
                ORDER BY o.ord_date DESC, o.ord_id;
                """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {

                ReportDTO report = new ReportDTO();

                report.setOrderId(rs.getInt("ord_id"));
                report.setRequestDate(rs.getDate("ord_date").toString());
                report.setProductName(rs.getString("product_name"));
                report.setQuantity(rs.getInt("quantity"));
                report.setTotal(rs.getDouble("total"));

                orders.add(report);

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return orders;
    }
}