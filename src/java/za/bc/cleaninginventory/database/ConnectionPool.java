package za.bc.cleaninginventory.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import za.bc.cleaninginventory.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {

    private static final HikariDataSource dataSource;

    static {

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(DatabaseConfig.URL);
        config.setUsername(DatabaseConfig.USER);
        config.setPassword(DatabaseConfig.PASS);

        config.setDriverClassName("org.postgresql.Driver");

        // Pool Configuration
        config.setMaximumPoolSize(3);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(10000);
        config.setIdleTimeout(60000);
        config.setMaxLifetime(300000);

        // Optional (helps identify your app in PostgreSQL)
        config.setPoolName("CleaningInventoryPool");
        config.setAutoCommit(true);

        dataSource = new HikariDataSource(config);
    }

    private ConnectionPool() {
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void shutdown() {
        dataSource.close();
    }
}