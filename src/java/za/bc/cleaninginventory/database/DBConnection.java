/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.bc.cleaninginventory.database;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
/**
 *
 * @author BC-STUDENT
 */
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
        Connection con = null;
        try{
            con = DriverManager.getConnection(URL, user,password);
            System.out.println("Connection successful: " + con.getCatalog());
        } catch (SQLException e) {
            System.out.println("Connection failed:");
            e.getMessage();
        }
        return con;
    }


    }
