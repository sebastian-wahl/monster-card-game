package game.repository;

import game.objects.CardBase;
import game.objects.card.factory.CardFactory;
import game.objects.enums.CardsEnum;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class RepositoryBase {

    protected static final String GET_CARD_SQL = "SELECT * FROM card WHERE id = ?;";

    @Getter
    @Setter
    protected Connection connection;

    protected RepositoryBase() {
    }

    protected List<CardBase> getCardsFromResultSet(ResultSet rs) throws SQLException {
        List<CardBase> out = new ArrayList<>();
        while (rs.next()) {
            out.add(this.getCardFromResultSet(rs));
        }
        return out;
    }

    protected CardBase getCardFromResultSet(ResultSet rs) throws SQLException {
        String id = rs.getString(1);
        String cardEnumName = rs.getString(2);
        int packageNumber = rs.getInt(4);
        CardBase card = CardFactory.createCard(CardsEnum.valueOf(cardEnumName.toUpperCase().replace(' ', '_')), id);
        card.setAdminPackageNumber(packageNumber);
        return card;
    }

    public Optional<CardBase> getCardById(String id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(GET_CARD_SQL)) {
            statement.setString(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    String cardEnumName = rs.getString(2);
                    int packageNumber = rs.getInt(4);
                    CardBase card = CardFactory.createCard(CardsEnum.valueOf(cardEnumName.toUpperCase().replace(' ', '_')), id);
                    card.setAdminPackageNumber(packageNumber);
                    card.setInTradeInvolved(rs.getBoolean(5));
                    return Optional.of(card);
                } else {
                    return Optional.empty();
                }
            }
        }
    }


}
