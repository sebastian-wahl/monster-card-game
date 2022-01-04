package game.repository;

import game.db.DatabaseConnectionProvider;
import game.objects.CardBase;
import lombok.Synchronized;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class CardRepository extends RepositoryBase {

    private static final String ADD_CARD_SQL = "INSERT INTO card (id, name, damage, admin_package_number) VALUES (?, ?, ?, ?);";
    private static final String GET_CARD_WITH_PACKAGE_NUMBER_SQL = "SELECT * FROM card WHERE admin_package_number = ?;";
    private static final String REMOVE_CARDS_FROM_ADMIN_PACKAGE = "UPDATE card SET admin_package_number = NULL where admin_package_number = ?;";

    @Synchronized
    public boolean addCards(List<CardBase> cards) {
        try {
            for (CardBase cardBase : cards) {
                this.addCard(cardBase);
            }
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private boolean addCard(CardBase cardBase) throws SQLException {
        try (Connection connection = DatabaseConnectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(ADD_CARD_SQL)) {
            statement.setString(1, cardBase.getId().toString());
            statement.setString(2, cardBase.getName());
            statement.setDouble(3, cardBase.getDamage());
            statement.setInt(4, cardBase.getAdminPackageNumber());

            return statement.executeUpdate() > 1;
        }
    }

    @Synchronized
    public List<CardBase> getCardsFromAdminPackage(int packageNumber) {
        try (Connection connection = DatabaseConnectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_CARD_WITH_PACKAGE_NUMBER_SQL)) {
            statement.setInt(1, packageNumber);
            ResultSet rs = statement.executeQuery();
            return this.getCardsFromResultSet(rs);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Synchronized
    public boolean removeCardsFromAdminPackage(int packageNumber) {
        try (Connection connection = DatabaseConnectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(REMOVE_CARDS_FROM_ADMIN_PACKAGE)) {
            statement.setInt(1, packageNumber);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
