package za.bc.cleaninginventory.config;

public class DatabaseConfig {

    private DatabaseConfig() {
        // Prevent instantiation
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

    public static final String URL = "jdbc:postgresql://aws-1-eu-west-2.pooler.supabase.com:6543/postgres?prepareThreshold=0";

    public static final String USER = "postgres.uqkrlszurcjzchuelgbh";

    public static final String PASS = "BC2026@1stock";

}