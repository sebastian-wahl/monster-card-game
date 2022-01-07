package game.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PackageRepository extends RepositoryBase {

    private static final String ADD_PACKAGE_SQL = "INSERT INTO admin_package (is_bought) VALUES (?) RETURNING package_number;";
    private static final String GET_IS_BOUGHT_PACKAGE_SQL = "SELECT is_bought FROM admin_package WHERE package_number = ?;";
    private static final String GET_FIST_NOT_BOUGHT_PACKAGE_SQL = "SELECT package_number FROM admin_package WHERE is_bought = false ORDER BY package_number ASC LIMIT 1;";
    private static final String SET_PACKAGE_TO_BOUGHT_SQL = "UPDATE admin_package SET is_bought = true WHERE package_number = ?;";


    public int addAdminPackage() throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(ADD_PACKAGE_SQL)) {
            preparedStatement.setBoolean(1, false);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }


    public boolean isAdminPackageBought(int packageNumber) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_IS_BOUGHT_PACKAGE_SQL)) {
            preparedStatement.setInt(1, packageNumber);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                rs.next();
                return rs.getBoolean(1);
            }
        }
    }


    public int getFirstAvailablePackageNumber() throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_FIST_NOT_BOUGHT_PACKAGE_SQL)) {
            try (ResultSet rs = preparedStatement.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }


    public boolean setAdminPackageToBought(int packageNumber) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SET_PACKAGE_TO_BOUGHT_SQL)) {
            preparedStatement.setInt(1, packageNumber);
            return preparedStatement.executeUpdate() > 0;
        }
    }
}
