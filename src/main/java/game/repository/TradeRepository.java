package game.repository;

import game.objects.CardBase;
import game.objects.Trade;
import game.objects.User;
import game.objects.card.factory.CardFactory;
import game.objects.enums.CardsEnum;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TradeRepository extends RepositoryBase {

    private static final String GET_ALL_TRADES_SQL = "SELECT * FROM trade WHERE (trade_finished = false OR trade_finished=?);";
    private static final String GET_TRADES_FOR_USER_SQL = "SELECT * FROM trade WHERE trade_user = ? AND (trade_finished = false OR trade_finished=?);";
    private static final String GET_TRADE_BY_ID_SQL = "SELECT * FROM trade WHERE id = ?;";

    private static final String GET_TRADES_FOR_OTHER_THAN_USER_SQL = "SELECT * FROM trade WHERE trade_user != ? AND (trade_finished = false OR trade_finished=?);";

    private static final String ADD_TRADE_SQL = "INSERT INTO trade(id, trade_card_id, trade_card_name, trade_user, desired_card_name, desired_coins)" +
            "VALUES(?, ?, ?, ?,?,?);";

    private static final String REMOVE_TRADE_SQL = "DELETE FROM trade WHERE id = ?;";

    private static final String UPDATE_AND_FINISH_TRADE_SQL = "UPDATE trade SET trade_finished = ?,  traded_to_user = ?,  traded_for_card_id = ?, traded_at = ? WHERE id = ? RETURNING *;";


    public Optional<Trade> getTradeById(String tradeId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_TRADE_BY_ID_SQL)) {
            preparedStatement.setString(1, tradeId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(this.getTradeFromResultSet(rs));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    public boolean addTrade(Trade trade) throws SQLException {
        return this.addTrade(trade.getId().toString(), trade.getTradeCard(), trade.getTradeUser(), trade.getDesiredCard(), trade.getDesiredCoins());
    }

    public boolean addTrade(String tradeId, CardBase tradeCard, User tradeUser, CardsEnum desiredCard, int desiredCoins) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(ADD_TRADE_SQL)) {
            preparedStatement.setString(1, tradeId);
            preparedStatement.setString(2, tradeCard.getId().toString());
            preparedStatement.setString(3, tradeCard.getName().toLowerCase().replace(' ', '_'));
            preparedStatement.setString(4, tradeUser.getUsername());
            preparedStatement.setString(5, desiredCard != null ? desiredCard.getName().toLowerCase().replace(' ', '_') : null);
            preparedStatement.setInt(6, Math.max(desiredCoins, 0));

            return preparedStatement.executeUpdate() > 0;
        }
    }


    public Optional<Trade> finishTrade(Trade trade, User tradedTo, String tradedForCardId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_AND_FINISH_TRADE_SQL)) {
            preparedStatement.setBoolean(1, true);
            preparedStatement.setString(2, tradedTo.getUsername());
            preparedStatement.setString(3, tradedForCardId);
            preparedStatement.setTimestamp(4, Timestamp.from(Instant.now()));
            preparedStatement.setString(5, trade.getId().toString());
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(this.getTradeFromResultSet(rs));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    public boolean removeTrade(Trade trade) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(REMOVE_TRADE_SQL)) {
            preparedStatement.setString(1, trade.getId().toString());
            return preparedStatement.executeUpdate() > 0;
        }
    }


    public List<Trade> getAllTrades(boolean openOnly) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_TRADES_SQL)) {
            preparedStatement.setBoolean(1, !openOnly);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                return this.getTradesFromResultSet(rs);
            }
        }
    }


    public List<Trade> getAllTradesForUser(User user, boolean openOnly) throws SQLException {
        return getTrades(user, GET_TRADES_FOR_USER_SQL, openOnly);
    }


    public List<Trade> getOtherTradesExceptUserTrades(User user, boolean openOnly) throws SQLException {
        return getTrades(user, GET_TRADES_FOR_OTHER_THAN_USER_SQL, openOnly);
    }

    private List<Trade> getTrades(User user, String statement, boolean openOnly) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setBoolean(2, !openOnly);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                return this.getTradesFromResultSet(rs);
            }
        }
    }

    private List<Trade> getTradesFromResultSet(ResultSet rs) throws SQLException {
        List<Trade> out = new ArrayList<>();
        while (rs.next()) {
            out.add(this.getTradeFromResultSet(rs));
        }
        return out;
    }

    private Trade getTradeFromResultSet(ResultSet rs) throws SQLException {
        return Trade.builder()
                .id(UUID.fromString(rs.getString(1)))
                .tradeCard(CardFactory.createCard(CardsEnum.valueOf(rs.getString(3).toUpperCase()), rs.getString(2)))
                .tradeUser(User.builder().username(rs.getString(4)).build())
                .desiredCard(rs.getString(5) != null ? CardsEnum.valueOf(rs.getString(5).toUpperCase()) : null)
                .desiredCoins(rs.getInt(6))
                .tradFinished(rs.getBoolean(7))
                .tradedToUser(User.builder().username(rs.getString(8)).build())
                .tradedForCard(rs.getString(5) != null ? CardFactory.createCard(CardsEnum.valueOf(rs.getString(3).toUpperCase()), rs.getString(9)) : null)
                .tradedAt(rs.getTimestamp(10))
                .build();
    }

}
