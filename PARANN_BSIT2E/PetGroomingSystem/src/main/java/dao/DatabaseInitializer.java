package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/** Ensures required database tables exist before the application starts. */
public final class DatabaseInitializer {
    private DatabaseInitializer() { }

    public static void initialize() {
        String usersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "roles TEXT NOT NULL, " +
                "status TEXT NOT NULL DEFAULT 'PENDING')";
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(usersTable);
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to initialize database: " + e.getMessage(), e);
        }
    }
}
