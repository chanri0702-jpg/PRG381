package za.bc.cleaninginventory.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection{
    
    //Supabase PostgreSQL Connection Details
    public static final String URL = "jdbc:postgresql://aws-1-eu-west-2.pooler.supabase.com:6543/postgres?prepareThreshold=0";
    public static final String USER = "postgres.uqkrlszurcjzchuelgbh";
    public static final String PASS = "BC2026@1stock";
    
    private static HikariDataSource dataSource;
    
    static{
        try{
            HikariConfig config = new HikariConfig();
            
            //Database connection settings
            config.setJdbcUrl(URL);
            config.setUsername(USER);
            config.setPassword(PASS);
            config.setDriverClassName("org.postgresql.Driver");
            
            //Connection pool settings
            config.setMaximumPoolSize(3);
            config.setMinimumIdle(1);
            config.setConnectionTimeout(10000);
            config.setIdleTimeout(60000);
            config.setMaxLifetime(300000);
            
            //Performance settings for Supabase
            config.setLeakDetectionThreshold(60000);
            config.setConnectionTestQuery("SELECT 1");
            
            //Additional settings for Supabase
            config.addDataSourceProperty("ssl", "true");
            config.addDataSourceProperty("sslmode", "require");
            
            dataSource = new HikariDataSource(config);
            
        } catch (Exception e){
            e.printStackTrace();
            throw new ExceptionInInitializerError("Failed to initialize database connection pool: " + e.getMessage());
        }
    }
    
    private DBConnection(){
        //Private constructor to prevent instantiation
    }
    
    public static Connection getConnection() throws SQLException {
        if (dataSource == null){
            throw new SQLException("Data source is not initialized");
        }
        return dataSource.getConnection();
    }
    
    public static HikariDataSource getDataSource(){
        return dataSource;
    }
    
    public static void closeConnection(){
        if (dataSource != null && !dataSource.isClosed()){
            dataSource.close();
        }
    }
}