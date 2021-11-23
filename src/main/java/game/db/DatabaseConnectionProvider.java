package game.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// https://dev.to/andre347/how-to-easily-create-a-postgres-database-in-docker-4moj
public class DatabaseConnectionProvider {
    static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    static final String USER = "mcg_user";
    static final String PASS = "password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
}
