package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Creates SQLite connections with foreign-key enforcement enabled. */
public final class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:petgrooming.db";

    private DatabaseConnection() { }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON;");
            return connection;
        } catch (SQLException e) {
            connection.close();
            throw e;
        }
    }
}
