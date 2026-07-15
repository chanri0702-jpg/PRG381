package za.bc.cleaninginventory.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import za.bc.cleaninginventory.config.DatabaseConfig;

/**
 * Manages database connection lifecycle.
 */
public class DBConnection {
    static {
        try {
            Class.forName(DatabaseConfig.getDriver());
        } catch (ClassNotFoundException e) {
            System.err.println("Database driver not found: " + DatabaseConfig.getDriver());
            e.printStackTrace();
        }
    }

    /**
     * Gets a connection to the configured database.
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            DatabaseConfig.getUrl(),
            DatabaseConfig.getUsername(),
            DatabaseConfig.getPassword()
        );
    }
}
