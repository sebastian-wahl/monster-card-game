package game.repository;

import game.db.DatabaseConnectionProvider;
import game.objects.CardBase;
import game.objects.Package;
import game.objects.Stack;
import game.objects.User;
import lombok.Synchronized;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StackRepository extends RepositoryBase {

    private static final String ADD_STACK_SQL = "INSERT INTO stack (card_id, username) VALUES (?, ?);";

    private static final String GET_STACK_SQL = "SELECT * FROM stack WHERE username=?";

    private static final String REMOVE_CARD_FROM_STACK_SQL = "DELETE FROM stack WHERE card_id=? AND username=?;";

    @Synchronized
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
        try (Connection connection = DatabaseConnectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(REMOVE_CARD_FROM_STACK_SQL)) {
            statement.setString(1, toRemove.getId().toString());
            statement.setString(2, user.getUsername());
        }
    }

    @Synchronized
    public Optional<User> addCardsToUserStack(User user, Package p) {
        return this.addCardsToUserStack(user, p.cards);
    }

    @Synchronized
    public Optional<User> addCardsToUserStack(User user, List<CardBase> toAdd) {
        try {
            Optional<User> userOpt = this.getUserStack(user);
            if (userOpt.isPresent()) {
                user = userOpt.get();
                for (CardBase cardBase : toAdd) {
                    this.addCardToUserStack(user, cardBase);
                }

                user.getStack().addCardsToStack(toAdd);
                return Optional.of(user);
            } else {
                return Optional.empty();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return Optional.empty();
    }

    private boolean addCardToUserStack(User user, CardBase toAdd) throws SQLException {
        try (Connection connection = DatabaseConnectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(ADD_STACK_SQL)) {
            statement.setString(1, toAdd.getId().toString());
            statement.setString(2, user.getUsername());

            return statement.executeUpdate() > 0;
        }
    }


    @Synchronized
    public boolean areCardsOwnedByUser(User user, List<CardBase> cardBases) {
        Optional<User> userOpt = this.getUserStack(user);
        return userOpt.map(userobj -> user.getStack().containsAll(cardBases)).orElse(false);
    }

    @Synchronized
    public Optional<User> getUserStack(User user) {
        try (Connection connection = DatabaseConnectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_STACK_SQL)) {
            statement.setString(1, user.getUsername());
            try (ResultSet rs = statement.executeQuery()) {
                List<CardBase> stack = this.getCardsFromModelResultSet(rs);
                user.setStack(new Stack(stack));
                return Optional.of(user);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }

    private List<CardBase> getCardsFromModelResultSet(ResultSet rs) throws SQLException {
        List<CardBase> out = new ArrayList<>();
        while (rs.next()) {
            String id = rs.getString(1);
            this.getCardFromId(id);
            out.add(this.getCardFromId(id));
        }
        return out;
    }
}
