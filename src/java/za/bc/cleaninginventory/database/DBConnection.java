package za.bc.cleaninginventory.database;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection{
    
    private DBConnection(){
        //Private constructor to prevent instantiation
    }
    
    public static Connection getConnection() throws SQLException{
        return ConnectionPool.getConnection();
    }
    
    public static void closeConnection(){
        ConnectionPool.shutdown();
    }
}