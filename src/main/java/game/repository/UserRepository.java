package game.repository;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import game.http.models.UserModel;
import game.objects.User;
import game.objects.UserStatistics;
import jBCrypt.BCrypt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserRepository extends RepositoryBase {

    private static final int DEFAULT_COINS = 20;
    private static final int DEFAULT_ELO = 100;

    private static final String ADD_USER_SQL = "INSERT INTO mcg_user (username, password, display_name, bio, image, coins, elo) VALUES (?, ?, ?, ?, ?, ?, ?);";
    private static final String GET_USER_SQL = "SELECT * FROM mcg_user WHERE username = ?;";
    private static final String GET_ALL_USER_SQL = "SELECT * FROM mcg_user;";

    private static final String UPDATE_USER_SQL = "UPDATE mcg_user SET display_name = ?, bio = ?, image = ?, coins = ?, elo = ?, win_count = ?, lose_count = ?, tie_count = ? WHERE username = ?;";


    /* elo K-factor, mean maximum amount of elo points that the user can gain or loose */
    private static final int K_FACTOR = 16;
    public static final int TOKEN_VALID_PERIOD_SECONDS = 3600;

    private final Algorithm algorithm;

    public UserRepository() {
        super();
        algorithm = Algorithm.HMAC256("secret");
    }


    public List<User> getAllUsers() throws SQLException {
        List<User> userList = new ArrayList<>();
        try (PreparedStatement addUserStatement = connection.prepareStatement(GET_ALL_USER_SQL)) {
            try (ResultSet ret = addUserStatement.executeQuery()) {
                while (ret.next()) {
                    userList.add(setUserFromResultSetNoNext(ret));
                }
                return userList;
            }
        }
    }


    public boolean addUserToDb(UserModel userModel) throws SQLException {
        try (PreparedStatement addUserStatement = connection.prepareStatement(ADD_USER_SQL)) {
            addUserStatement.setString(1, userModel.getUsername());
            addUserStatement.setString(2, this.hashPassword(userModel.getPassword()));
            addUserStatement.setString(3, userModel.getDisplayName());
            addUserStatement.setString(4, userModel.getBio());
            addUserStatement.setString(5, userModel.getImage());
            addUserStatement.setInt(6, DEFAULT_COINS);
            addUserStatement.setInt(7, DEFAULT_ELO);

            return addUserStatement.executeUpdate() == 1;
        }
    }

    private String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    private Optional<User> loginAndGetUser(String username, String password) throws SQLException {
        Optional<User> userOpt = this.getUser(username);
        if (userOpt.isPresent() && this.checkPass(password, userOpt.get().getPassword())) {
            return userOpt;
        }
        return Optional.empty();
    }

    public Optional<User> getUser(String username) throws SQLException {
        try (PreparedStatement addUserStatement = connection.prepareStatement(GET_USER_SQL)) {
            addUserStatement.setString(1, username);
            try (ResultSet ret = addUserStatement.executeQuery()) {
                if (ret.next()) {
                    return Optional.of(this.setUserFromResultSetNoNext(ret));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    public boolean updateTieAndGamesPlayed(List<String> players) throws SQLException {
        if (players.size() > 2) {
            return false;
        }
        List<User> playersUser = players.stream()
                .map(stringUsername -> {
                    try {
                        return this.getUser(stringUsername);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(userOpt -> userOpt.orElse(null))
                .collect(Collectors.toList());

        if (!playersUser.contains(null)) {
            User player1 = playersUser.get(0);
            User player2 = playersUser.get(1);
            User updatedUser = player1.copy();
            updatedUser.getUserStatistics().addTie();
            this.doUpdate(player1, updatedUser);

            updatedUser = player2.copy();
            updatedUser.getUserStatistics().addTie();
            this.doUpdate(player2, updatedUser);
            return true;
        }
        return false;
    }


    public boolean updateEloAndGamesPlayed(String winnerUsername, String loserUsername) throws SQLException {
        Optional<User> winnerOpt = this.getUser(winnerUsername);
        Optional<User> loserOpt = this.getUser(loserUsername);
        if (winnerOpt.isPresent() && loserOpt.isPresent()) {
            User winner = winnerOpt.get();
            User loser = loserOpt.get();

            double eloUserWinnerNew = calculateNewEloWinner(winner.getElo(), loser.getElo());
            double eloUserLoserNew = calculateNewEloLoser(loser.getElo(), winner.getElo());
            if (eloUserLoserNew < 0) {
                eloUserLoserNew = 0.0;
            }

            // update winner
            User updatedUser = winner.copy();
            updatedUser.getUserStatistics().addWin();
            updatedUser.setElo(eloUserWinnerNew);
            this.doUpdate(winner, updatedUser);

            // update loser
            updatedUser = loser.copy();
            updatedUser.getUserStatistics().addLose();
            updatedUser.setElo(eloUserLoserNew);
            this.doUpdate(loser, updatedUser);

            return true;
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

    public boolean login(UserModel userModel) throws SQLException {
        return this.loginAndGetUser(userModel.getUsername(), userModel.getPassword()).isPresent();
    }

    private boolean checkPass(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }


    public Optional<User> update(User user) throws SQLException {
        Optional<User> defaultUserOpt = this.getUser(user.getUsername());
        if (defaultUserOpt.isPresent() && this.doUpdate(defaultUserOpt.get(), user)) {
            this.updateUserObject(user, defaultUserOpt.get());
            return Optional.of(user);
        }
        return Optional.empty();
    }

    private void updateUserObject(User toUpdate, User defaultValues) {
        if (toUpdate.getId() == null) toUpdate.setId(defaultValues.getId());
        if (toUpdate.getUsername() == null) toUpdate.setUsername(defaultValues.getUsername());
        if (toUpdate.getPassword() == null) toUpdate.setPassword(defaultValues.getPassword());
        if (toUpdate.getDisplayName() == null) toUpdate.setDisplayName(defaultValues.getDisplayName());
        if (toUpdate.getBio() == null) toUpdate.setBio(defaultValues.getBio());
        if (toUpdate.getImage() == null) toUpdate.setImage(defaultValues.getImage());
        if (toUpdate.getCoins() == -1) toUpdate.setCoins(defaultValues.getCoins());
        if (toUpdate.getElo() == -1) toUpdate.setElo(defaultValues.getElo());
        if (toUpdate.getUserStatistics() == null) toUpdate.setUserStatistics(defaultValues.getUserStatistics());
    }

    /**
     * @param defaultUserData data from this user will be used when the updateUser has an empty attribute
     * @param updatedUser     used to update the user
     * @return true when successful, false when not
     * @throws SQLException when an SQLException occurs
     */
    private boolean doUpdate(User defaultUserData, User updatedUser) throws SQLException {
        try (PreparedStatement updateUserStatement = connection.prepareStatement(UPDATE_USER_SQL)) {
            updateUserStatement.setString(1, whenNullElse(updatedUser.getDisplayName(), defaultUserData.getDisplayName()));
            updateUserStatement.setString(2, whenNullElse(updatedUser.getBio(), defaultUserData.getBio()));
            updateUserStatement.setString(3, whenNullElse(updatedUser.getImage(), defaultUserData.getImage()));
            updateUserStatement.setInt(4, whenNegativElse(updatedUser.getCoins(), defaultUserData.getCoins()));
            updateUserStatement.setDouble(5, whenNegativElse(updatedUser.getElo(), defaultUserData.getElo()));
            updateUserStatement.setInt(6, updatedUser.getUserStatistics() != null ? updatedUser.getUserStatistics().getWinCount() : defaultUserData.getUserStatistics().getWinCount());
            updateUserStatement.setInt(7, updatedUser.getUserStatistics() != null ? updatedUser.getUserStatistics().getLoseCount() : defaultUserData.getUserStatistics().getLoseCount());
            updateUserStatement.setInt(8, updatedUser.getUserStatistics() != null ? updatedUser.getUserStatistics().getTieCount() : defaultUserData.getUserStatistics().getTieCount());
            updateUserStatement.setString(9, defaultUserData.getUsername());
            return updateUserStatement.executeUpdate() > 0;
        }
    }

    private static <T> T whenNullElse(T object, T defaultObject) {
        return object != null ? object : defaultObject;
    }

    private static int whenNegativElse(int a, int b) {
        return a < 0 ? b : a;
    }

    private static double whenNegativElse(double a, double b) {
        return a < 0 ? b : a;
    }

    private User setUserFromResultSetNoNext(ResultSet ret) throws SQLException {
        return User.builder().id(ret.getLong(1))
                .username(ret.getString(2))
                .password(ret.getString(3))
                .displayName(ret.getString(4))
                .bio(ret.getString(5))
                .image(ret.getString(6))
                .coins(ret.getInt(7))
                .elo(ret.getDouble(8))
                .userStatistics(UserStatistics.builder().winCount(ret.getInt(9))
                        .loseCount(ret.getInt(10))
                        .tieCount(ret.getInt(11)).build()
                ).build();
    }

    /* ------ User Token ------ */
    public Optional<String> generateSecurityToken(String username) {
        try {
            return Optional.of(JWT.create()
                    .withKeyId(username)
                    .withExpiresAt(Timestamp.from(Instant.now().plusSeconds(TOKEN_VALID_PERIOD_SECONDS)))
                    .sign(algorithm));
        } catch (JWTCreationException e) {
            e.printStackTrace();
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
        } catch (JWTCreationException | TokenExpiredException | SignatureVerificationException | JWTDecodeException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<User> checkTokenAndGetUser(String token) throws SQLException {
        Optional<String> usernameOpt = this.checkToken(token);
        if (usernameOpt.isPresent()) {
            String username = usernameOpt.get();
            return this.getUser(username);
        }
        return Optional.empty();
    }
}