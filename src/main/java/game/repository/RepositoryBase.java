package game.repository;

import game.db.DatabaseConnectionProvider;
import game.objects.CardBase;
import game.objects.card.factory.CardFactory;
import game.objects.enums.CardsEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class RepositoryBase {

    protected static final String GET_CARD_SQL = "SELECT * FROM card WHERE id = ?;";


    protected RepositoryBase() {
    }

    protected List<CardBase> getCardsFromResultSet(ResultSet rs) throws SQLException {
        List<CardBase> out = new ArrayList<>();
        while (rs.next()) {
            String id = rs.getString(1);
            String cardEnumName = rs.getString(2);
            out.add(CardFactory.createCard(CardsEnum.valueOf(cardEnumName.toUpperCase().replace(' ', '_')), id));
        }
        return out;
    }

    protected CardBase getCardFromId(String id) throws SQLException {
        try (Connection connection = DatabaseConnectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_CARD_SQL)) {
            statement.setString(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                String cardEnumName = rs.getString(2);
                return CardFactory.createCard(CardsEnum.valueOf(cardEnumName.toUpperCase().replace(' ', '_')), id);
            }
        }
    }
}
