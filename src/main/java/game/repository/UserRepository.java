package game.repository;

import game.objects.User;
import jBCrypt.BCrypt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class UserRepository extends RepositoryBase {

    private static final int DEFAULT_COINS = 20;
    private static final int DEFAULT_ELO = 100;

    private static final String ADD_USER_SQL = "INSERT INTO mcg_user (username, password, display_name, bio, coins, elo) VALUES (?, ?, ?, ?, ?, ?);";
    private static final String GET_USER_SQL = "SELECT * FROM mcg_user WHERE username = ?;";

    private static final String UPDATE_USER_SQL = "UPDATE mcg_user SET display_name = ?, bio = ?, coins = ?, elo = ?, security_token = ?, security_token_date = ? WHERE username = ?;";

    private static final String GET_USER_TOKEN_SQL = "SELECT * from mcg_user WHERE security_token = ?;";

    public UserRepository() {
        super();
    }

    public boolean addUserToDb(String username, String displayName, String bio, String password) {
        if (this.dbConnection != null) {
            try (PreparedStatement addUserStatement = this.dbConnection.prepareStatement(ADD_USER_SQL)) {
                addUserStatement.setString(1, username);
                addUserStatement.setString(2, this.hashPassword(password));
                addUserStatement.setString(3, displayName);
                addUserStatement.setString(4, bio);
                addUserStatement.setInt(5, DEFAULT_COINS);
                addUserStatement.setInt(6, DEFAULT_ELO);

                int suc = addUserStatement.executeUpdate();
                if (suc == 1) {
                    return true;
                }
            } catch (SQLException e) {
                System.out.println("User not added because: " + e.getSQLState());
            }
        }
        return false;
    }

    private String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    private Optional<User> loginAndGetUser(String username, String password) {
        try (PreparedStatement addUserStatement = this.dbConnection.prepareStatement(GET_USER_SQL)) {
            addUserStatement.setString(1, username);
            ResultSet ret = addUserStatement.executeQuery();
            User user = new User();
            this.setUserFromResultSet(ret, user);
            if (this.checkPass(password, user.getPassword())) {
                return Optional.of(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean login(String username, String password) {
        try (PreparedStatement addUserStatement = this.dbConnection.prepareStatement(GET_USER_SQL)) {
            addUserStatement.setString(1, username);
            ResultSet ret = addUserStatement.executeQuery();
            ret.next();
            String hashedPass = ret.getString(3);
            if (this.checkPass(password, hashedPass)) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean checkPass(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    public Optional<User> update(User userWrapper) {
        if (userWrapper.getUsername() != null && userWrapper.getPassword() != null) {
            return updateWithUsernameAndPassword(userWrapper);
        } else if (userWrapper.getSecurityToken() != null && userWrapper.getSecurityTokenTimestamp() != null) {
            return updateWithToken(userWrapper);
        }
        return Optional.empty();
    }

    private Optional<User> updateWithUsernameAndPassword(User userWrapper) {
        Optional<User> userOpt = this.loginAndGetUser(userWrapper.getUsername(), userWrapper.getPassword());
        if (userOpt.isPresent()) {
            return updateAndCheckIfValid(userOpt.get(), userWrapper);
        } else {
            return Optional.empty();
        }
    }

    private Optional<User> updateWithToken(User userWrapper) {
        Optional<User> userOpt = this.checkIfTokenIsValid(userWrapper.getSecurityToken());
        if (userOpt.isPresent()) {
            return updateAndCheckIfValid(userOpt.get(), userWrapper);
        } else {
            return Optional.empty();
        }
    }

    private Optional<User> updateAndCheckIfValid(User user, User userWrapper) {
        try {
            if (this.doUpdate(user, userWrapper)) {
                return Optional.of(user);
            }
        } catch (SQLException e) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    private boolean doUpdate(User user, User userWrapper) throws SQLException {
        try (PreparedStatement updateUserStatement = this.dbConnection.prepareStatement(UPDATE_USER_SQL)) {
            updateUserStatement.setString(1, Objects.requireNonNullElse(userWrapper.getDisplayName(), user.getDisplayName()));
            updateUserStatement.setString(2, Objects.requireNonNullElse(userWrapper.getBio(), user.getBio()));
            updateUserStatement.setDouble(3, Objects.requireNonNullElse(userWrapper.getElo(), user.getElo()));
            updateUserStatement.setInt(4, Objects.requireNonNullElse(userWrapper.getCoins(), user.getCoins()));
            updateUserStatement.setString(5, Objects.requireNonNullElse(userWrapper.getSecurityToken(), user.getSecurityToken()));
            updateUserStatement.setTimestamp(6, Objects.requireNonNullElse(userWrapper.getSecurityTokenTimestamp(), user.getSecurityTokenTimestamp()));
            updateUserStatement.setString(7, user.getUsername());
            return updateUserStatement.execute();
        } catch (SQLException ex) {
            throw ex;
        }
    }

    private Optional<User> checkIfTokenIsValid(String securityToken) {
        Timestamp now = Timestamp.from(Instant.now());

        try (PreparedStatement checkTokenStatement = this.dbConnection.prepareStatement(GET_USER_TOKEN_SQL)) {
            checkTokenStatement.setString(1, securityToken);
            ResultSet ret = checkTokenStatement.executeQuery();
            User user = new User();
            this.setUserFromResultSet(ret, user);
            Timestamp validUntil = Timestamp.valueOf(user.getSecurityTokenTimestamp().toLocalDateTime().plusDays(1));
            if (now.before(validUntil)) {
                return Optional.of(user);
            }
        } catch (SQLException |
                IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }

    private void setUserFromResultSet(ResultSet ret, User user) throws SQLException {
        ret.next();
        user.setId(ret.getLong(1));
        user.setUsername(ret.getString(2));
        // hashed password
        user.setPassword(ret.getString(3));
        user.setSecurityToken(ret.getString(4));
        user.setSecurityTokenTimestamp(ret.getTimestamp(5));
        user.setDisplayName(ret.getString(6));
        user.setBio(ret.getString(7));
        user.setCoins(ret.getInt(8));
        user.setElo(ret.getDouble(9));
    }

}
