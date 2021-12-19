package game.repository;

import game.db.DatabaseConnectionProvider;
import game.objects.CardBase;
import game.objects.card.factory.CardFactory;
import game.objects.enums.CardsEnum;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class RepositoryBase {
    protected Connection dbConnection;

    protected RepositoryBase() {
        try {
            this.dbConnection = DatabaseConnectionProvider.getConnection();
        } catch (SQLException e) {
            this.dbConnection = null;
        }
    }


    protected List<CardBase> getCardsFromResultSet(ResultSet rs) throws SQLException {
        List<CardBase> out = new ArrayList<>();
        while (rs.next()) {
            String cardEnumName = rs.getString(2);
            out.add(CardFactory.createCard(CardsEnum.valueOf(cardEnumName.toUpperCase())));
        }
        return out;
    }
}
