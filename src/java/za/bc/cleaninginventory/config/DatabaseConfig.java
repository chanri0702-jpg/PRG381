package za.bc.cleaninginventory.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration class for loading database properties.
 */
public class DatabaseConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                // System.err logging or fallback default properties
                properties.setProperty("db.driver", "org.apache.derby.jdbc.ClientDriver");
                properties.setProperty("db.url", "jdbc:derby://localhost:1527/cleaning_db;create=true");
                properties.setProperty("db.username", "app");
                properties.setProperty("db.password", "app");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getDriver() {
        return properties.getProperty("db.driver");
    }

    public static String getUrl() {
        return properties.getProperty("db.url");
    }

    public static String getUsername() {
        return properties.getProperty("db.username");
    }

    public static String getPassword() {
        return properties.getProperty("db.password");
    }
}
