package za.bc.cleaninginventory.model.dao.supplier;

import za.bc.cleaninginventory.database.DBConnection;
import za.bc.cleaninginventory.model.entity.Supplier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {

    public List<Supplier> getAllSuppliers() throws SQLException {

        List<Supplier> suppliers = new ArrayList<>();

        String sql = """
                SELECT
                    sb.bus_id,
                    sb.name AS business_name,
                    sb.description,

                    so.off_id,
                    so.address,
                    so.area,
                    so.city,
                    so.province,
                    so.postal_code,

                    se.sup_id,
                    se.name AS contact_name,
                    se.surname AS contact_surname,
                    se.email AS contact_email,
                    se.phone AS contact_phone

                FROM supplier_businesses sb

                LEFT JOIN supplier_offices so
                    ON sb.bus_id = so.bus_id

                LEFT JOIN supplier_employees se
                    ON sb.bus_id = se.bus_id

                ORDER BY sb.name
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {

                Supplier supplier = new Supplier();

                supplier.setBusinessId(resultSet.getInt("bus_id"));
                supplier.setBusinessName(resultSet.getString("business_name"));
                supplier.setDescription(resultSet.getString("description"));

                supplier.setOfficeId(resultSet.getInt("off_id"));
                supplier.setAddress(resultSet.getString("address"));
                supplier.setArea(resultSet.getString("area"));
                supplier.setCity(resultSet.getString("city"));
                supplier.setProvince(resultSet.getString("province"));
                supplier.setPostalCode(resultSet.getString("postal_code"));

                supplier.setSupplierEmployeeId(resultSet.getInt("sup_id"));
                supplier.setContactName(resultSet.getString("contact_name"));
                supplier.setContactSurname(resultSet.getString("contact_surname"));
                supplier.setContactEmail(resultSet.getString("contact_email"));
                supplier.setContactPhone(resultSet.getString("contact_phone"));

                suppliers.add(supplier);
            }
        }

        return suppliers;
    }

    public Supplier getSupplierById(int businessId) throws SQLException {

        String sql = """
                SELECT
                    sb.bus_id,
                    sb.name AS business_name,
                    sb.description,

                    so.off_id,
                    so.address,
                    so.area,
                    so.city,
                    so.province,
                    so.postal_code,

                    se.sup_id,
                    se.name AS contact_name,
                    se.surname AS contact_surname,
                    se.email AS contact_email,
                    se.phone AS contact_phone

                FROM supplier_businesses sb

                LEFT JOIN supplier_offices so
                    ON sb.bus_id = so.bus_id

                LEFT JOIN supplier_employees se
                    ON sb.bus_id = se.bus_id

                WHERE sb.bus_id = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, businessId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    Supplier supplier = new Supplier();

                    supplier.setBusinessId(resultSet.getInt("bus_id"));
                    supplier.setBusinessName(resultSet.getString("business_name"));
                    supplier.setDescription(resultSet.getString("description"));

                    supplier.setOfficeId(resultSet.getInt("off_id"));
                    supplier.setAddress(resultSet.getString("address"));
                    supplier.setArea(resultSet.getString("area"));
                    supplier.setCity(resultSet.getString("city"));
                    supplier.setProvince(resultSet.getString("province"));
                    supplier.setPostalCode(resultSet.getString("postal_code"));

                    supplier.setSupplierEmployeeId(resultSet.getInt("sup_id"));
                    supplier.setContactName(resultSet.getString("contact_name"));
                    supplier.setContactSurname(resultSet.getString("contact_surname"));
                    supplier.setContactEmail(resultSet.getString("contact_email"));
                    supplier.setContactPhone(resultSet.getString("contact_phone"));

                    return supplier;
                }
            }
        }

        return null;
    }

    public boolean addSupplier(Supplier supplier) throws SQLException {

        String businessSql = """
                INSERT INTO supplier_businesses (name, description)
                VALUES (?, ?)
                RETURNING bus_id
                """;

        String officeSql = """
                INSERT INTO supplier_offices
                (address, area, city, province, postal_code, bus_id)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        String employeeSql = """
                INSERT INTO supplier_employees
                (name, surname, email, phone, bus_id)
                VALUES (?, ?, ?, ?, ?)
                """;

        Connection connection = null;

        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            int businessId;

            // Insert the supplier business
            try (PreparedStatement statement =
                         connection.prepareStatement(businessSql)) {

                statement.setString(1, supplier.getBusinessName());
                statement.setString(2, supplier.getDescription());

                try (ResultSet resultSet = statement.executeQuery()) {

                    if (!resultSet.next()) {
                        connection.rollback();
                        return false;
                    }

                    businessId = resultSet.getInt("bus_id");
                    supplier.setBusinessId(businessId);
                }
            }

            // Insert the supplier office
            try (PreparedStatement statement =
                         connection.prepareStatement(officeSql)) {

                statement.setString(1, supplier.getAddress());
                statement.setString(2, supplier.getArea());
                statement.setString(3, supplier.getCity());
                statement.setString(4, supplier.getProvince());
                statement.setString(5, supplier.getPostalCode());
                statement.setInt(6, businessId);

                statement.executeUpdate();
            }

            // Insert the supplier contact person
            try (PreparedStatement statement =
                         connection.prepareStatement(employeeSql)) {

                statement.setString(1, supplier.getContactName());
                statement.setString(2, supplier.getContactSurname());
                statement.setString(3, supplier.getContactEmail());
                statement.setString(4, supplier.getContactPhone());
                statement.setInt(5, businessId);

                statement.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (SQLException exception) {

            if (connection != null) {
                connection.rollback();
            }

            throw exception;

        } finally {

            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    public boolean updateSupplier(Supplier supplier) throws SQLException {

        String businessSql = """
                UPDATE supplier_businesses
                SET name = ?, description = ?
                WHERE bus_id = ?
                """;

        String officeSql = """
                UPDATE supplier_offices
                SET address = ?,
                    area = ?,
                    city = ?,
                    province = ?,
                    postal_code = ?
                WHERE bus_id = ?
                """;

        String employeeSql = """
                UPDATE supplier_employees
                SET name = ?,
                    surname = ?,
                    email = ?,
                    phone = ?
                WHERE bus_id = ?
                """;

        Connection connection = null;

        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            int businessRows;

            try (PreparedStatement statement =
                         connection.prepareStatement(businessSql)) {

                statement.setString(1, supplier.getBusinessName());
                statement.setString(2, supplier.getDescription());
                statement.setInt(3, supplier.getBusinessId());

                businessRows = statement.executeUpdate();
            }

            if (businessRows == 0) {
                connection.rollback();
                return false;
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(officeSql)) {

                statement.setString(1, supplier.getAddress());
                statement.setString(2, supplier.getArea());
                statement.setString(3, supplier.getCity());
                statement.setString(4, supplier.getProvince());
                statement.setString(5, supplier.getPostalCode());
                statement.setInt(6, supplier.getBusinessId());

                statement.executeUpdate();
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(employeeSql)) {

                statement.setString(1, supplier.getContactName());
                statement.setString(2, supplier.getContactSurname());
                statement.setString(3, supplier.getContactEmail());
                statement.setString(4, supplier.getContactPhone());
                statement.setInt(5, supplier.getBusinessId());

                statement.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (SQLException exception) {

            if (connection != null) {
                connection.rollback();
            }

            throw exception;

        } finally {

            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    public boolean deleteSupplier(int businessId) throws SQLException {

        String deleteEmployeesSql = """
                DELETE FROM supplier_employees
                WHERE bus_id = ?
                """;

        String deleteOfficesSql = """
                DELETE FROM supplier_offices
                WHERE bus_id = ?
                """;

        String deleteBusinessSql = """
                DELETE FROM supplier_businesses
                WHERE bus_id = ?
                """;

        Connection connection = null;

        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            // Delete supplier contact people
            try (PreparedStatement statement =
                         connection.prepareStatement(deleteEmployeesSql)) {

                statement.setInt(1, businessId);
                statement.executeUpdate();
            }

            // Delete supplier offices
            try (PreparedStatement statement =
                         connection.prepareStatement(deleteOfficesSql)) {

                statement.setInt(1, businessId);
                statement.executeUpdate();
            }

            // Delete supplier business
            int deletedRows;

            try (PreparedStatement statement =
                         connection.prepareStatement(deleteBusinessSql)) {

                statement.setInt(1, businessId);
                deletedRows = statement.executeUpdate();
            }

            if (deletedRows == 0) {
                connection.rollback();
                return false;
            }

            connection.commit();
            return true;

        } catch (SQLException exception) {

            if (connection != null) {
                connection.rollback();
            }

            throw exception;

        } finally {

            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }
}