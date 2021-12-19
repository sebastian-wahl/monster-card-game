package game.repository;

import game.objects.CardBase;
import game.objects.Deck;
import game.objects.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class DeckRepository extends RepositoryBase {

    private static final String GET_CARDS_FROM_DECK_SQL = "SELECT * FROM card WHERE id IN (SELECT card_id FROM deck WHERE username=?);";


    private static final String SET_DECK_SQL = "DELETE FROM deck WHERE username=?; INSERT INTO deck (card_id, username) " +
            "VALUES (?, ?)," +
            "VALUES (?, ?)," +
            "VALUES (?, ?)," +
            "VALUES (?, ?);";

    public DeckRepository() {
        super();
    }

    public Optional<Deck> getDeckByUsername(String username) {
        try (PreparedStatement preparedStatement = this.dbConnection.prepareStatement(GET_CARDS_FROM_DECK_SQL)) {
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();

            return Optional.of(new Deck(getCardsFromResultSet(rs)));
        } catch (SQLException | IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * @param user User for this deck
     * @param deck should only be cards that the user also owns
     */
    public Optional<User> setUserDeck(User user, Deck deck) {
        if (user.getDeck().getDeckSize() > Deck.INIT_MAX_DECK_SIZE) {
            return Optional.empty();
        }
        try (PreparedStatement statement = this.dbConnection.prepareStatement(SET_DECK_SQL)) {
            statement.setString(1, user.getUsername());

            int i = 2;
            for (CardBase cardBase : deck.getCards()) {
                statement.setString(i, cardBase.getId().toString());
                statement.setString(i + 1, user.getUsername());
                i++;
            }
            user.setDeck(deck);

            int updates = statement.executeUpdate();
            System.out.println(updates + " row(s) updated");
            return Optional.of(user);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }
}
