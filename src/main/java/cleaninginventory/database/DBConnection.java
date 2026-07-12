package cleaninginventory.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:postgresql://aws-1-eu-west-2.pooler.supabase.com:5432/postgres";
    private static final String user = "postgres.uqkrlszurcjzchuelgbh";
    private static final String password = "BC2026@1stock";

    static{
    try {
        Class.forName ("org.postgresql.Driver");
    } catch (ClassNotFoundException e){
        throw new RuntimeException ("PostgreSQL JDBC driver not found. "+e);
    }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, user,password);
    }
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("Connection successful: " + conn.getCatalog());
        } catch (SQLException e) {
            System.out.println("Connection failed:");
            e.printStackTrace();
        }
    }
}

