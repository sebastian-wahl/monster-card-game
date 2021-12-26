package game.repository;

import game.db.DatabaseConnectionProvider;
import game.http.models.DeckModel;
import game.objects.Deck;
import game.objects.User;
import lombok.Synchronized;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class DeckRepository extends RepositoryBase {
    private Connection db2Connection;

    private static final String GET_CARDS_FROM_DECK_SQL = "SELECT * FROM card WHERE id IN (SELECT card_id FROM deck WHERE username=?);";


    private static final String SET_DECK_SQL = "DELETE FROM deck WHERE username=?; INSERT INTO deck (card_id, username) " +
            "VALUES (?, ?)," +
            "(?, ?)," +
            "(?, ?)," +
            "(?, ?);";

    public DeckRepository() {
        super();
    }

    @Synchronized
    public Optional<Deck> getDeckByUsername(String username) {
        try (Connection connection = DatabaseConnectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_CARDS_FROM_DECK_SQL);) {
            preparedStatement.setString(1, username);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                return Optional.of(new Deck(getCardsFromResultSet(rs)));
            }
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * @param user User for this deck
     * @param deck should only be cards that the user also owns
     */
    @Synchronized
    public Optional<User> setUserDeck(User user, DeckModel deck) {
        if (deck.getIds().length > Deck.INIT_MAX_DECK_SIZE) {
            return Optional.empty();
        }
        try (Connection connection = DatabaseConnectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(SET_DECK_SQL)) {
            statement.setString(1, user.getUsername());

            int i = 2;
            for (String id : deck.getDeckIds()) {
                statement.setString(i, id);
                statement.setString(i + 1, user.getUsername());
                i += 2;
            }
            int updates = statement.executeUpdate();
            System.out.println(updates + " row(s) updated");

            Optional<Deck> deckOpt = this.getDeckByUsername(user.getUsername());
            if (deckOpt.isPresent()) {
                user.setDeck(deckOpt.get());
                return Optional.of(user);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }
}
