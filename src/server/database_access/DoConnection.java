package server.database_access;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DoConnection {
    private static final String URL = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres?user=postgres.qtncwvznrznqnrrdlkfb&password=StoreAutomation777!!";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}