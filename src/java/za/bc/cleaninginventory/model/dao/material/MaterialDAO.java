/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.bc.cleaninginventory.model.dao.material;
import za.bc.cleaninginventory.database.DBConnection;
import za.bc.cleaninginventory.model.entity.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author BC-STUDENT
 */
public class MaterialDAO {
    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT prod_id, bus_id, name, price, description FROM products ORDER BY name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("prod_id"));
                p.setBussID(rs.getInt("bus_id"));
                p.setName(rs.getString("name"));
                p.setPrice(rs.getBigDecimal("price"));
                p.setDescription(rs.getString("description"));
                products.add(p);
            }
        }
        return products;
    }
}
