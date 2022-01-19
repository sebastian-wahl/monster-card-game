package game.repository;

import game.objects.CardBase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class CardRepository extends RepositoryBase {

    private static final String ADD_CARD_SQL = "INSERT INTO card (id, name, damage, admin_package_number) VALUES (?, ?, ?, ?);";
    private static final String GET_CARD_WITH_PACKAGE_NUMBER_SQL = "SELECT * FROM card WHERE admin_package_number = ?;";
    private static final String GET_CARDS_BY_ID_START_SQL = "SELECT * FROM card WHERE";
    private static final String GET_CARDS_BY_ID_TEMP_SQL = "id = ?";
    private static final String REMOVE_CARDS_FROM_ADMIN_PACKAGE_SQL = "UPDATE card SET admin_package_number = NULL where admin_package_number = ?;";

    private static final String UPDATE_CARD_FOR_TRADING_SQL = "UPDATE card SET involved_in_trade = ? WHERE id = ? RETURNING *";


    public CardBase markCardForTrading(String cardId, boolean isInTrade) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_CARD_FOR_TRADING_SQL)) {
            statement.setBoolean(1, isInTrade);
            statement.setString(2, cardId);

            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                return this.getCardFromResultSet(rs);
            }
        }
    }

    public void addCards(List<CardBase> cards) throws SQLException {
        for (CardBase cardBase : cards) {
            this.addCard(cardBase);
        }
    }

    private void addCard(CardBase cardBase) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(ADD_CARD_SQL)) {
            statement.setString(1, cardBase.getId().toString());
            statement.setString(2, cardBase.getName());
            statement.setDouble(3, cardBase.getDamage());
            if (cardBase.getAdminPackageNumber() != 0) {
                statement.setInt(4, cardBase.getAdminPackageNumber());
            } else {
                statement.setNull(4, Types.INTEGER);
            }
            statement.executeUpdate();
        }
    }

    public List<CardBase> getCardsById(List<String> cardId) throws SQLException {
        String query = GET_CARDS_BY_ID_START_SQL + " " + (GET_CARDS_BY_ID_TEMP_SQL + " OR ").repeat(cardId.size() - 1) + GET_CARDS_BY_ID_TEMP_SQL;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (int i = 0; i < cardId.size(); i++) {
                statement.setString(i + 1, cardId.get(i));
            }
            try (ResultSet rs = statement.executeQuery()) {
                return this.getCardsFromResultSet(rs);
            }
        }
    }


    public List<CardBase> getCardsFromAdminPackage(int packageNumber) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(GET_CARD_WITH_PACKAGE_NUMBER_SQL)) {
            statement.setInt(1, packageNumber);
            try (ResultSet rs = statement.executeQuery()) {
                return this.getCardsFromResultSet(rs);
            }
        }
    }


    public boolean removeCardsFromAdminPackage(int packageNumber) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(REMOVE_CARDS_FROM_ADMIN_PACKAGE_SQL)) {
            statement.setInt(1, packageNumber);
            return statement.executeUpdate() > 0;
        }
    }
}
