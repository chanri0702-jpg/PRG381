package za.bc.cleaninginventory.model.dao.material;

import za.bc.cleaninginventory.database.ConnectionPool;
import za.bc.cleaninginventory.model.entity.Material;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaterialDAO {

    // Create - Add new product
    public boolean createMaterial(Material material) {
        String productSql = "INSERT INTO products (bus_id, name, price, description) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        
        try {
            conn = ConnectionPool.getConnection();
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmt = conn.prepareStatement(productSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setLong(1, material.getBusId());
                pstmt.setString(2, material.getName());
                pstmt.setBigDecimal(3, material.getPrice());
                pstmt.setString(4, material.getDescription());
                
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            Long prodId = rs.getLong(1);
                            material.setProdId(prodId);
                            
                            // Also add stock for the campus
                            if (material.getCampId() != null && material.getStockQuantity() != null) {
                                addStockForCampus(conn, prodId, material.getCampId(), material.getStockQuantity());
                            }
                            
                            conn.commit();
                            return true;
                        }
                    }
                }
                
                conn.rollback();
                return false;
            }
            
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Helper method to add stock for a campus
    private void addStockForCampus(Connection conn, Long prodId, Integer campId, Integer quantity) throws SQLException {
        String stockSql = "INSERT INTO product_stock (prod_id, camp_id, stock) VALUES (?, ?, ?) " +
                         "ON CONFLICT (prod_id, camp_id) DO UPDATE SET stock = product_stock.stock + ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(stockSql)) {
            pstmt.setLong(1, prodId);
            pstmt.setInt(2, campId);
            pstmt.setInt(3, quantity);
            pstmt.setInt(4, quantity);
            pstmt.executeUpdate();
        }
    }

    // Read - Get material by ID
    public Material getMaterialById(Long prodId) {
        String sql = "SELECT p.prod_id, p.bus_id, p.name, p.price, p.description, " +
                     "ps.stock, ps.camp_id, sb.name as supplier_name, c.name as campus_name " +
                     "FROM products p " +
                     "LEFT JOIN product_stock ps ON p.prod_id = ps.prod_id " +
                     "LEFT JOIN supplier_businesses sb ON p.bus_id = sb.bus_id " +
                     "LEFT JOIN campuses c ON ps.camp_id = c.camp_id " +
                     "WHERE p.prod_id = ?";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, prodId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMaterial(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Read - Get all materials
    public List<Material> getAllMaterials() {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT p.prod_id, p.bus_id, p.name, p.price, p.description, " +
                     "ps.stock, ps.camp_id, sb.name as supplier_name, c.name as campus_name " +
                     "FROM products p " +
                     "LEFT JOIN product_stock ps ON p.prod_id = ps.prod_id " +
                     "LEFT JOIN supplier_businesses sb ON p.bus_id = sb.bus_id " +
                     "LEFT JOIN campuses c ON ps.camp_id = c.camp_id " +
                     "ORDER BY p.name";
        
        try (Connection conn = ConnectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                materials.add(mapResultSetToMaterial(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return materials;
    }

    // Read - Get materials with search and filtering
    public List<Material> searchMaterials(String searchTerm, String supplierId, String campusId) {
        List<Material> materials = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT p.prod_id, p.bus_id, p.name, p.price, p.description, " +
            "ps.stock, ps.camp_id, sb.name as supplier_name, c.name as campus_name " +
            "FROM products p " +
            "LEFT JOIN product_stock ps ON p.prod_id = ps.prod_id " +
            "LEFT JOIN supplier_businesses sb ON p.bus_id = sb.bus_id " +
            "LEFT JOIN campuses c ON ps.camp_id = c.camp_id " +
            "WHERE 1=1"
        );
        
        List<Object> params = new ArrayList<>();
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append(" AND (LOWER(p.name) LIKE ? OR LOWER(p.description) LIKE ?) ");
            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        if (supplierId != null && !supplierId.trim().isEmpty() && !supplierId.equals("All")) {
            sql.append(" AND p.bus_id = ? ");
            params.add(Long.parseLong(supplierId));
        }
        
        if (campusId != null && !campusId.trim().isEmpty() && !campusId.equals("All")) {
            sql.append(" AND ps.camp_id = ? ");
            params.add(Integer.parseInt(campusId));
        }
        
        sql.append(" ORDER BY p.name");
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    materials.add(mapResultSetToMaterial(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return materials;
    }

    // Read - Get low stock materials
    public List<Material> getLowStockMaterials() {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT p.prod_id, p.bus_id, p.name, p.price, p.description, " +
                     "ps.stock, ps.camp_id, sb.name as supplier_name, c.name as campus_name " +
                     "FROM products p " +
                     "JOIN product_stock ps ON p.prod_id = ps.prod_id " +
                     "LEFT JOIN supplier_businesses sb ON p.bus_id = sb.bus_id " +
                     "LEFT JOIN campuses c ON ps.camp_id = c.camp_id " +
                     "WHERE ps.stock <= 10 " +
                     "ORDER BY ps.stock ASC";
        
        try (Connection conn = ConnectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                materials.add(mapResultSetToMaterial(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return materials;
    }

    // Update - Update material details
    public boolean updateMaterial(Material material) {
        String sql = "UPDATE products SET name = ?, description = ?, price = ?, bus_id = ? " +
                     "WHERE prod_id = ?";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, material.getName());
            pstmt.setString(2, material.getDescription());
            pstmt.setBigDecimal(3, material.getPrice());
            pstmt.setLong(4, material.getBusId());
            pstmt.setLong(5, material.getProdId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update stock quantity for a specific campus
    public boolean updateStockQuantity(Long prodId, Integer campId, int newQuantity) {
        String sql = "UPDATE product_stock SET stock = ? WHERE prod_id = ? AND camp_id = ?";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, newQuantity);
            pstmt.setLong(2, prodId);
            pstmt.setInt(3, campId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete - Delete material (cascade will handle product_stock)
    public boolean deleteMaterial(Long prodId) {
        String sql = "DELETE FROM products WHERE prod_id = ?";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, prodId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get distinct suppliers
    public List<Supplier> getDistinctSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT bus_id, name FROM supplier_businesses ORDER BY name";
        
        try (Connection conn = ConnectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Supplier supplier = new Supplier();
                supplier.setBusId(rs.getLong("bus_id"));
                supplier.setName(rs.getString("name"));
                suppliers.add(supplier);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    // Get distinct campuses
    public List<Campus> getDistinctCampuses() {
        List<Campus> campuses = new ArrayList<>();
        String sql = "SELECT camp_id, name FROM campuses ORDER BY name";
        
        try (Connection conn = ConnectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Campus campus = new Campus();
                campus.setCampId(rs.getInt("camp_id"));
                campus.setName(rs.getString("name"));
                campuses.add(campus);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return campuses;
    }

    // Count total materials
    public int getTotalMaterialCount() {
        String sql = "SELECT COUNT(*) FROM products";
        
        try (Connection conn = ConnectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Count low stock items
    public int getLowStockCount() {
        String sql = "SELECT COUNT(*) FROM product_stock WHERE stock <= 10";
        
        try (Connection conn = ConnectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Helper method to map ResultSet to Material object
    private Material mapResultSetToMaterial(ResultSet rs) throws SQLException {
        Material material = new Material();
        material.setProdId(rs.getLong("prod_id"));
        material.setBusId(rs.getLong("bus_id"));
        material.setName(rs.getString("name"));
        material.setDescription(rs.getString("description"));
        material.setPrice(rs.getBigDecimal("price"));
        material.setStockQuantity(rs.getInt("stock"));
        material.setCampId(rs.getInt("camp_id"));
        material.setSupplierName(rs.getString("supplier_name"));
        material.setCampusName(rs.getString("campus_name"));
        return material;
    }

    // Inner class for Supplier
    public static class Supplier {
        private Long busId;
        private String name;

        public Long getBusId() { return busId; }
        public void setBusId(Long busId) { this.busId = busId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    // Inner class for Campus
    public static class Campus {
        private Integer campId;
        private String name;

        public Integer getCampId() { return campId; }
        public void setCampId(Integer campId) { this.campId = campId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}