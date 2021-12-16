package game.repository;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import game.http.models.UserModel;
import game.objects.User;
import game.objects.UserStatistics;
import jBCrypt.BCrypt;
import lombok.Synchronized;

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

    private static final String UPDATE_USER_SQL = "UPDATE mcg_user SET display_name = ?, bio = ?, coins = ?, elo = ?, security_token = ?, security_token_date = ?, win_count = ?, lose_count = ?, tie_count = ? WHERE username = ?;";

    private static final String GET_USER_TOKEN_SQL = "SELECT * from mcg_user WHERE security_token = ?;";

    // elo K-factor, mean maximum amount of elo points that the user can gain or loose;
    private static final int K_FACTOR = 16;

    private final Algorithm algorithm;

    public UserRepository() {
        super();
        algorithm = Algorithm.HMAC256("secret");
    }

    @Synchronized
    public boolean addUserToDb(UserModel userModel) {
        if (this.dbConnection != null) {
            try (PreparedStatement addUserStatement = this.dbConnection.prepareStatement(ADD_USER_SQL)) {
                addUserStatement.setString(1, userModel.getUsername());
                addUserStatement.setString(2, this.hashPassword(userModel.getPassword()));
                addUserStatement.setString(3, userModel.getDisplayName());
                addUserStatement.setString(4, userModel.getBio());
                // ToDo add image
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
            User user = this.setUserFromResultSet(ret);
            if (this.checkPass(password, user.getPassword())) {
                return Optional.of(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Synchronized
    public boolean updateElo(String winnerUsername, String loserUsername) {
        try (PreparedStatement getWinnerUserStatement = this.dbConnection.prepareStatement(GET_USER_SQL);
             PreparedStatement getLoserUserStatement = this.dbConnection.prepareStatement(GET_USER_SQL)) {
            getWinnerUserStatement.setString(1, winnerUsername);
            ResultSet retWinner = getWinnerUserStatement.executeQuery();
            User winner = this.setUserFromResultSet(retWinner);

            getLoserUserStatement.setString(1, loserUsername);
            ResultSet retLoser = getLoserUserStatement.executeQuery();
            User loser = this.setUserFromResultSet(retLoser);

            double eloUserWinnerNew = calculateNewEloWinner(winner.getElo(), loser.getElo());
            double eloUserLoserNew = calculateNewEloLoser(loser.getElo(), winner.getElo());

            // update winner
            User updatedUser = winner.copy();
            updatedUser.setElo(eloUserWinnerNew);
            this.doUpdate(winner, updatedUser);

            // update loser
            updatedUser = loser.copy();
            updatedUser.setElo(eloUserLoserNew);
            this.doUpdate(loser, updatedUser);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    protected double calculateNewEloWinner(double eloWinner, double eloLoser) {
        return Math.round(100 * (eloWinner + K_FACTOR * (1 - eloCalculationVariable(eloWinner, eloLoser)))) / 100.0;
    }

    protected double calculateNewEloLoser(double eloLoser, double eloWinner) {
        return Math.round(100 * (eloLoser + K_FACTOR * (0 - eloCalculationVariable(eloLoser, eloWinner))) / 100.0);
    }

    /**
     * Used for Elo calculation
     * EloWinner = EloWinnerOld + K * (1 - return from this method)
     * EloLoser = EloLoserOld + K * (0 - return from this method)
     *
     * @param eloA Elo from user for whom this variable is calculated
     * @param eloB Elo from other user
     */
    private double eloCalculationVariable(double eloA, double eloB) {
        return Math.round(1000 * 1.0 / (1.0 + Math.pow(10, (eloB - eloA) / 400.0))) / 1000.0;
    }

    @Synchronized
    public boolean loginToken(String token) {
        Optional<String> username = this.checkToken(token);
        if (username.isPresent()) {
            System.out.println("Token for User: \"" + username.get() + "\" is valid");
            return true;
        } else {
            System.out.println("Token is invalid");
            return false;
        }
    }

    @Synchronized
    public boolean login(UserModel userModel) {
        try (PreparedStatement addUserStatement = this.dbConnection.prepareStatement(GET_USER_SQL)) {
            addUserStatement.setString(1, userModel.getUsername());
            ResultSet ret = addUserStatement.executeQuery();
            ret.next();
            String hashedPass = ret.getString(3);
            if (this.checkPass(userModel.getPassword(), hashedPass)) {
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

    @Synchronized
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
        Optional<User> userOpt = this.checkIfTokenIsValidAndGetUser(userWrapper.getSecurityToken());
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

    /**
     * @param defaultUserData data from this user will be used when the updateUser has an empty attribute
     * @param updatedUser     used to update the user
     * @return true when successful, false when not
     * @throws SQLException when an SQLException occurs
     */
    private boolean doUpdate(User defaultUserData, User updatedUser) throws SQLException {
        try (PreparedStatement updateUserStatement = this.dbConnection.prepareStatement(UPDATE_USER_SQL)) {
            updateUserStatement.setString(1, Objects.requireNonNullElse(updatedUser.getDisplayName(), defaultUserData.getDisplayName()));
            updateUserStatement.setString(2, Objects.requireNonNullElse(updatedUser.getBio(), defaultUserData.getBio()));
            updateUserStatement.setDouble(3, Objects.requireNonNullElse(updatedUser.getElo(), defaultUserData.getElo()));
            updateUserStatement.setInt(4, Objects.requireNonNullElse(updatedUser.getCoins(), defaultUserData.getCoins()));
            updateUserStatement.setString(5, Objects.requireNonNullElse(updatedUser.getSecurityToken(), defaultUserData.getSecurityToken()));
            updateUserStatement.setTimestamp(6, Objects.requireNonNullElse(updatedUser.getSecurityTokenTimestamp(), defaultUserData.getSecurityTokenTimestamp()));
            updateUserStatement.setString(7, defaultUserData.getUsername());
            return updateUserStatement.execute();
        } catch (SQLException ex) {
            throw ex;
        }
    }

    private boolean checkIfTokenIsValid(Timestamp securityTokenTimestamp) {
        Timestamp now = Timestamp.from(Instant.now());
        Timestamp validUntil = Timestamp.valueOf(securityTokenTimestamp.toLocalDateTime().plusDays(1));
        return now.before(validUntil);
    }

    private Optional<User> checkIfTokenIsValidAndGetUser(String securityToken) {
        Timestamp now = Timestamp.from(Instant.now());

        try (PreparedStatement checkTokenStatement = this.dbConnection.prepareStatement(GET_USER_TOKEN_SQL)) {
            checkTokenStatement.setString(1, securityToken);
            ResultSet ret = checkTokenStatement.executeQuery();
            User user = this.setUserFromResultSet(ret);
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

    private User setUserFromResultSet(ResultSet ret) throws SQLException {
        ret.next();
        return User.builder().id(ret.getLong(1))
                .username(ret.getString(2))
                .password(ret.getString(3))
                .securityToken(ret.getString(4))
                .securityTokenTimestamp(ret.getTimestamp(5))
                .displayName(ret.getString(6))
                .bio(ret.getString(7))
                .coins(ret.getInt(8))
                .elo(ret.getDouble(9))
                .userStatistics(UserStatistics.builder().winCount(ret.getInt(10))
                        .loseCount(ret.getInt(11))
                        .tieCount(ret.getInt(12)).build()
                ).build();
    }

    /* ------ User Token ------ */
    public Optional<String> generateSecurityToken(String username) {
        try {
            return Optional.of(JWT.create()
                    .withKeyId(username)
                    .withExpiresAt(Timestamp.from(Instant.now().plusSeconds(3600)))
                    .sign(algorithm));
        } catch (JWTCreationException exception) {
            //Invalid Signing configuration / Couldn't convert Claims.
        }
        return Optional.empty();
    }

    /**
     * Checks if token is valid
     *
     * @param token the token
     * @return optional with username from this token, or empty optional if invalid
     */
    public Optional<String> checkToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);
            return Optional.of(jwt.getKeyId());
        } catch (JWTCreationException exception) {
            System.out.println("Error");
        }
        return Optional.empty();
    }
}