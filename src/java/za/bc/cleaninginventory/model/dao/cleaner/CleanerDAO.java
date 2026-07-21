/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.bc.cleaninginventory.model.dao.cleaner;

import za.bc.cleaninginventory.database.DBConnection;
import za.bc.cleaninginventory.model.entity.Cleaner;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author BC-STUDENT
 */
public class CleanerDAO {
    public List<Cleaner> getAllCleaners() throws SQLException {
        List<Cleaner> cleaners = new ArrayList<>();
        String sql = "SELECT cleaner_id, name, surname, phone, email, camp_id FROM cleaners ORDER BY name, surname";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

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
        return cleaners;
    }

    public Integer getCampIdForCleaner(int cleanerId) throws SQLException {
        String sql = "SELECT camp_id FROM cleaners WHERE cleaner_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cleanerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("camp_id");
                }
            }
        }
        return null;
    }
}
