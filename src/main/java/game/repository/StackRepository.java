package game.repository;

import game.objects.CardBase;
import game.objects.Package;
import game.objects.Stack;
import game.objects.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StackRepository extends RepositoryBase {

    private static final String ADD_STACK_INSERT_SQL = "INSERT INTO stack (card_id, username) VALUES ";
    private static final String ADD_STACK_VALUES_TEMPLATE_SQL = "(?, ?)";

    private static final String GET_STACK_SQL = "SELECT * FROM stack WHERE username=?";

    private static final String REMOVE_CARD_FROM_STACK_START_SQL = "DELETE FROM stack WHERE ";
    private static final String REMOVE_CARD_FROM_STACK_ID_TEMP_SQL = "card_id=?";
    private static final String REMOVE_CARD_FROM_STACK_END_SQL = "AND username=?;";


    public Optional<User> removeCardsFromUserStack(User user, List<CardBase> toRemove) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(REMOVE_CARD_FROM_STACK_START_SQL + REMOVE_CARD_FROM_STACK_ID_TEMP_SQL.repeat(toRemove.size()) + REMOVE_CARD_FROM_STACK_END_SQL)) {
            int i = 1;
            for (CardBase cardBase : toRemove) {
                preparedStatement.setString(i, cardBase.getId().toString());
                i++;
            }
            preparedStatement.setString(i, user.getUsername());

            if (preparedStatement.executeUpdate() > 0) {
                if (user.getStack() == null) {
                    Optional<User> userOptional = this.getUserStack(user);
                    if (userOptional.isPresent()) {
                        user = userOptional.get();
                    }
                }
                user.getStack().removeCardsFromStack(toRemove);
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public Optional<User> addCardsToUserStack(User user, Package p) throws SQLException {
        return this.addCardsToUserStack(user, p.cards);
    }

    public Optional<User> addCardsToUserStack(User user, List<CardBase> toAdd) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(ADD_STACK_INSERT_SQL + ADD_STACK_VALUES_TEMPLATE_SQL.repeat(toAdd.size()) + ";")) {
            int i = 1;
            for (CardBase cardBase : toAdd) {
                preparedStatement.setString(i, cardBase.getId().toString());
                preparedStatement.setString(i + 1, user.getUsername());
                i += 2;
            }

            if (preparedStatement.executeUpdate() > 0) {
                if (user.getStack() == null) {
                    Optional<User> userOptional = this.getUserStack(user);
                    if (userOptional.isPresent()) {
                        user = userOptional.get();
                    }
                }
                user.getStack().addCardsToStack(toAdd);
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }


    public boolean areCardsOwnedByUser(User user, List<CardBase> cardBases) throws SQLException {
        Optional<User> userOpt = this.getUserStack(user);
        return userOpt.map(userobj -> user.getStack().containsAll(cardBases)).orElse(false);
    }


    public Optional<User> getUserStack(User user) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(GET_STACK_SQL)) {
            statement.setString(1, user.getUsername());
            try (ResultSet rs = statement.executeQuery()) {
                List<CardBase> stack = this.getCardsFromModelResultSet(rs);
                user.setStack(new Stack(stack));
                return Optional.of(user);
            }
        }
    }

    private List<CardBase> getCardsFromModelResultSet(ResultSet rs) throws SQLException {
        List<CardBase> out = new ArrayList<>();
        while (rs.next()) {
            String id = rs.getString(1);
            Optional<CardBase> cardOpt = this.getCardById(id);
            cardOpt.ifPresent(out::add);
        }
        return out;
    }
}
