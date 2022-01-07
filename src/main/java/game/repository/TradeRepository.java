package game.repository;

import game.objects.CardBase;
import game.objects.Trade;
import game.objects.User;
import game.objects.card.factory.CardFactory;
import game.objects.enums.CardsEnum;
import lombok.Synchronized;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TradeRepository extends RepositoryBase {

    private static final String GET_ALL_TRADES_SQL = "SELECT * FROM trade;";
    private static final String GET_TRADES_FOR_USER_SQL = "SELECT * FROM trade WHERE trade_user = ?;";
    private static final String GET_TRADES_FOR_OTHER_THAN_USER_SQL = "SELECT * FROM trade WHERE trade_user != ?";

    private static final String ADD_TRADE_SQL = "INSERT INTO trade(trade_card_id, trade_user, desired_card_name, desired_coins)" +
            "VALUES(?,?,?,?)";

    private static final String REMOVE_TRADE_SQL = "DELETE FROM trade WHERE id = ?;";

    private static final String UPDATE_AND_FINISH_TRADE_SQL = "UPDATE trade SET trad_finished = ?,  traded_to_user = ?,  traded_for_card_id = ?, traded_at = ?";

    public boolean addTrade(Trade trade) throws SQLException {
        return this.addTrade(trade.getTradeCard(), trade.getTradeUser(), trade.getDesiredCard(), trade.getDesiredCoins());
    }

    public boolean addTrade(CardBase tradeCard, User tradeUser, CardsEnum desiredCard, int desiredCoins) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(ADD_TRADE_SQL)) {
            preparedStatement.setString(1, tradeCard.getId().toString());
            preparedStatement.setString(2, tradeUser.getUsername());
            preparedStatement.setString(3, desiredCard != null ? desiredCard.getName().toLowerCase().replace(' ', '_') : null);
            preparedStatement.setInt(4, Math.max(desiredCoins, 0));

            return preparedStatement.executeUpdate() > 0;
        }
    }

    @Synchronized
    public boolean finishTrade(Trade trade, User tradedTo, CardBase tradedFor, int tradedForCoins) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_AND_FINISH_TRADE_SQL)) {
            preparedStatement.setBoolean(1, true);
            preparedStatement.setString(2, tradedTo.getUsername());
            preparedStatement.setString(3, tradedFor != null ? tradedFor.getId().toString() : null);
            preparedStatement.setInt(4, Math.max(tradedForCoins, 0));
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public boolean removeTrade(Trade trade) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(REMOVE_TRADE_SQL)) {
            preparedStatement.setString(1, trade.getId().toString());
            return preparedStatement.executeUpdate() > 0;
        }
    }

    @Synchronized
    public List<Trade> getAllTrades() throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_TRADES_SQL)) {
            try (ResultSet rs = preparedStatement.executeQuery()) {
                return this.getTradesFromResultSet(rs);
            }
        }
    }

    @Synchronized
    public List<Trade> getAllTradesForUser(User user) throws SQLException {
        return getTrades(user, GET_TRADES_FOR_USER_SQL);
    }

    @Synchronized
    public List<Trade> getOtherTradesExceptUserTrades(User user) throws SQLException {
        return getTrades(user, GET_TRADES_FOR_OTHER_THAN_USER_SQL);
    }

    private List<Trade> getTrades(User user, String statement) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.setString(1, user.getUsername());
            try (ResultSet rs = preparedStatement.executeQuery()) {
                return this.getTradesFromResultSet(rs);
            }
        }
    }

    private List<Trade> getTradesFromResultSet(ResultSet rs) throws SQLException {
        List<Trade> out = new ArrayList<>();
        while (rs.next()) {
            out.add(Trade.builder()
                    .id(UUID.fromString(rs.getString(1)))
                    .tradeCard(CardFactory.createCard(CardsEnum.valueOf(rs.getString(3).toUpperCase()), rs.getString(2)))
                    .tradeUser(User.builder().username(rs.getString(4)).build())
                    .desiredCard(rs.getString(5) != null ? CardsEnum.valueOf(rs.getString(5).toUpperCase()) : null)
                    .desiredCoins(rs.getInt(6))
                    .tradFinished(rs.getBoolean(7))
                    .tradedToUser(User.builder().username(rs.getString(8)).build())
                    .tradedForCard(rs.getString(5) != null ? CardFactory.createCard(CardsEnum.valueOf(rs.getString(3).toUpperCase()), rs.getString(9)) : null)
                    .tradedAt(rs.getTimestamp(10))
                    .build());
        }
        return out;
    }

}
