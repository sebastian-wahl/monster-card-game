package game.repository;

import game.db.DatabaseConnectionProvider;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class RepositoryBase {
    protected Connection dbConnection;

    protected RepositoryBase() {
        try {
            this.dbConnection = DatabaseConnectionProvider.getConnection();
        } catch (SQLException e) {
            this.dbConnection = null;
        }
    }
}
