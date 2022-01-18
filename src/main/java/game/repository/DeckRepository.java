package game.repository;

import game.http.models.DeckModel;
import game.objects.Deck;
import game.objects.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class DeckRepository extends RepositoryBase {
    private static final String GET_CARDS_FROM_DECK_SQL = "SELECT * FROM card WHERE id IN (SELECT card_id FROM deck WHERE username=?);";
    private static final String GET_CARD_COUNT_FOR_ID_AND_USERNAME = "SELECT COUNT(card_id) FROM deck WHERE card_id = ? AND username = ?;";


    private static final String SET_DECK_SQL = "DELETE FROM deck WHERE username=?; INSERT INTO deck (card_id, username) " +
            "VALUES (?, ?)," +
            "(?, ?)," +
            "(?, ?)," +
            "(?, ?);";

    public DeckRepository() {
        super();
    }


    public boolean isCardInUserDeck(String username, String cardId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_CARD_COUNT_FOR_ID_AND_USERNAME)) {
            preparedStatement.setString(1, cardId);
            preparedStatement.setString(2, username);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 1;
                } else {
                    return false;
                }

            }
        }
    }


    public Deck getDeckByUsername(String username) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_CARDS_FROM_DECK_SQL);) {
            preparedStatement.setString(1, username);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                return new Deck(getCardsFromResultSet(rs));
            }
        }
    }

    /**
     * @param user User for this deck
     * @param deck should only be cards that the user also owns
     */

    public Optional<User> setUserDeck(User user, DeckModel deck) throws SQLException {
        if (deck.getIds().length > Deck.INIT_MAX_DECK_SIZE) {
            return Optional.empty();
        }
        try (PreparedStatement statement = connection.prepareStatement(SET_DECK_SQL)) {
            statement.setString(1, user.getUsername());

            int i = 2;
            for (String id : deck.getDeckIds()) {
                statement.setString(i, id);
                statement.setString(i + 1, user.getUsername());
                i += 2;
            }
            statement.executeUpdate();

            user.setDeck(this.getDeckByUsername(user.getUsername()));
            return Optional.of(user);
        }
    }
}
