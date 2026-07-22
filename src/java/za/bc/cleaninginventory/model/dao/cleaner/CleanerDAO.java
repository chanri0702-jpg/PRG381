package za.bc.cleaninginventory.model.dao.cleaner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import za.bc.cleaninginventory.database.DBConnection;
import za.bc.cleaninginventory.model.entity.Cleaner;

public class CleanerDAO {

    public List<Cleaner> getAllCleaners() throws SQLException {

        List<Cleaner> cleaners = new ArrayList<>();

        String sql = """
                SELECT
                    cl.cleaner_id,
                    cl.name,
                    cl.surname,
                    cl.phone,
                    cl.email,
                    cl.camp_id,
                    c.name AS campus_name
                FROM cleaners cl
                LEFT JOIN campuses c
                    ON cl.camp_id = c.camp_id
                ORDER BY cl.surname, cl.name
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                cleaners.add(mapCleaner(resultSet));
            }
        }

        return cleaners;
    }

    public Cleaner getCleanerById(int cleanerId) throws SQLException {

        String sql = """
                SELECT
                    cl.cleaner_id,
                    cl.name,
                    cl.surname,
                    cl.phone,
                    cl.email,
                    cl.camp_id,
                    c.name AS campus_name
                FROM cleaners cl
                LEFT JOIN campuses c
                    ON cl.camp_id = c.camp_id
                WHERE cl.cleaner_id = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, cleanerId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapCleaner(resultSet);
                }
            }
        }

        return null;
    }

    public boolean addCleaner(Cleaner cleaner) throws SQLException {

        String sql = """
                INSERT INTO cleaners
                    (name, surname, phone, email, camp_id)
                VALUES
                    (?, ?, ?, ?, ?)
                RETURNING cleaner_id
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            setCleanerParameters(statement, cleaner);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    cleaner.setCleanerId(
                            resultSet.getInt("cleaner_id")
                    );
                    return true;
                }
            }
        }

        return false;
    }

    public boolean updateCleaner(Cleaner cleaner) throws SQLException {

        String sql = """
                UPDATE cleaners
                SET
                    name = ?,
                    surname = ?,
                    phone = ?,
                    email = ?,
                    camp_id = ?
                WHERE cleaner_id = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            setCleanerParameters(statement, cleaner);
            statement.setInt(6, cleaner.getCleanerId());

            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteCleaner(int cleanerId) throws SQLException {

        String sql = """
                DELETE FROM cleaners
                WHERE cleaner_id = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, cleanerId);

            return statement.executeUpdate() > 0;
        }
    }

    public Map<Integer, String> getAllCampuses() throws SQLException {

        Map<Integer, String> campuses = new LinkedHashMap<>();

        String sql = """
                SELECT camp_id, name
                FROM campuses
                ORDER BY name
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                campuses.put(
                        resultSet.getInt("camp_id"),
                        resultSet.getString("name")
                );
            }
        }

        return campuses;
    }

    public boolean campusExists(int campusId) throws SQLException {

        String sql = """
                SELECT 1
                FROM campuses
                WHERE camp_id = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, campusId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public boolean emailExists(
            String email,
            int excludedCleanerId
    ) throws SQLException {

        String sql;

        if (excludedCleanerId > 0) {
            sql = """
                    SELECT 1
                    FROM cleaners
                    WHERE LOWER(email) = LOWER(?)
                      AND cleaner_id <> ?
                    """;
        } else {
            sql = """
                    SELECT 1
                    FROM cleaners
                    WHERE LOWER(email) = LOWER(?)
                    """;
        }

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, email);

            if (excludedCleanerId > 0) {
                statement.setInt(2, excludedCleanerId);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private Cleaner mapCleaner(ResultSet resultSet)
            throws SQLException {

        Cleaner cleaner = new Cleaner();

        cleaner.setCleanerId(
                resultSet.getInt("cleaner_id")
        );

        cleaner.setName(
                resultSet.getString("name")
        );

        cleaner.setSurname(
                resultSet.getString("surname")
        );

        cleaner.setPhone(
                resultSet.getString("phone")
        );

        cleaner.setEmail(
                resultSet.getString("email")
        );

        cleaner.setCampusId(
                resultSet.getInt("camp_id")
        );

        cleaner.setCampusName(
                resultSet.getString("campus_name")
        );

        return cleaner;
    }

    private void setCleanerParameters(
            PreparedStatement statement,
            Cleaner cleaner
    ) throws SQLException {

        statement.setString(1, cleaner.getName());
        statement.setString(2, cleaner.getSurname());
        statement.setString(3, cleaner.getPhone());
        statement.setString(4, cleaner.getEmail());
        statement.setInt(5, cleaner.getCampusId());
    }
}
