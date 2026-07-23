/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.bc.cleaninginventory.model.dao.cleaner;

import za.bc.cleaninginventory.database.ConnectionPool;
import za.bc.cleaninginventory.model.entity.Cleaner;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author BC-STUDENT
 */
public class CleanerDAO {
    public List<Cleaner> getCleanersByCampus(int campId) throws SQLException {
        List<Cleaner> cleaners = new ArrayList<>();
        String sql = "SELECT cleaner_id, name, surname, phone, email, camp_id FROM cleaners "
                + "WHERE camp_id = ? ORDER BY name, surname";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, campId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cleaner c = new Cleaner();
                    c.setCleanerId(rs.getInt("cleaner_id"));
                    c.setName(rs.getString("name"));
                    c.setSurname(rs.getString("surname"));
                    c.setPhone(rs.getString("phone"));
                    c.setEmail(rs.getString("email"));
                    c.setCampId(rs.getInt("camp_id"));
                    cleaners.add(c);
                }
            }
        }
        return cleaners;
    }
}
