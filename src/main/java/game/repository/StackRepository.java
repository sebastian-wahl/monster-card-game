package game.repository;

import game.objects.CardBase;
import game.objects.Package;
import game.objects.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class StackRepository extends RepositoryBase {

    private static final String ADD_STACK_SQL = "INSERT INTO stack (card_id, username) VALUES (?, ?);";

    private static final String GET_STACK_SQL = "SELECT * FROM stack WHERE username=?";

    private static final String REMOVE_CARD_FROM_STACK_SQL = "DELETE FROM stack WHERE card_id=? AND username=?;";

    public Optional<User> removeCardsFromUserStack(User user, List<CardBase> toRemove) {
        try {
            for (CardBase cardBase : toRemove) {
                this.removeCardFromUserStack(user, cardBase);
            }
            user.getStack().removeCardsFromStack(toRemove);
            return Optional.of(user);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }

    private void removeCardFromUserStack(User user, CardBase toRemove) throws SQLException {
        try (PreparedStatement statement = this.dbConnection.prepareStatement(REMOVE_CARD_FROM_STACK_SQL)) {
            statement.setString(1, toRemove.getId().toString());
            statement.setString(2, user.getUsername());
        }
    }

    public Optional<User> addCardsToUserStack(User user, Package p) {
        return this.addCardsToUserStack(user, p.cards);
    }

    public Optional<User> addCardsToUserStack(User user, List<CardBase> toAdd) {
        try {
            for (CardBase cardBase : toAdd) {
                this.addCardToUserStack(user, cardBase);
            }

            user.getStack().addCardsToStack(toAdd);
            return Optional.of(user);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return Optional.empty();
    }

    private void addCardToUserStack(User user, CardBase toAdd) throws SQLException {
        try (PreparedStatement statement = this.dbConnection.prepareStatement(ADD_STACK_SQL)) {
            statement.setString(1, toAdd.getId().toString());
            statement.setString(2, user.getUsername());
        }
    }


    public boolean areCardsOwnedByUser(User user, List<CardBase> cardBases) {
        Optional<List<CardBase>> stackOpt = this.getUserStack(user);
        return stackOpt.map(bases -> bases.containsAll(cardBases)).orElse(false);
    }

    public Optional<List<CardBase>> getUserStack(User user) {
        try (PreparedStatement statement = this.dbConnection.prepareStatement(GET_STACK_SQL)) {
            statement.setString(1, user.getUsername());

            ResultSet rs = statement.executeQuery();

            List<CardBase> stack = this.getCardsFromResultSet(rs);
            return Optional.of(stack);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }
}
