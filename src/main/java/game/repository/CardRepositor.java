package game.repository;

import game.db.DatabaseConnectionProvider;
import game.objects.CardBase;
import lombok.Synchronized;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class CardRepositor extends RepositoryBase {

    private static final String ADD_CARD_SQL = "INSERT INTO card (id, name, damage) VALUES (?, ?, ?);";


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

            return statement.executeUpdate() > 1;
        }
    }
}
