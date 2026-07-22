package za.bc.cleaninginventory.database;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {

    private DBConnection() {
    }

    public static Connection getConnection() {

        try {

            return ConnectionPool.getConnection();

        } catch (SQLException ex) {

            throw new RuntimeException(
                    "Unable to obtain database connection.",
                    ex);

        }
    }
}