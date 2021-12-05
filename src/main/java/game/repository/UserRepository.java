package game.repository;

import jBCrypt.BCrypt;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserRepository extends RepositoryBase {

    private static final int DEFAULT_COINS = 20;
    private static final int DEFAULT_ELO = 100;

    private static final String ADD_USER_SQL = "INSERT INTO mcg_user (username, password, displayName, bio, coins, elo) VALUES (?, ?, ?, ?, ?, ?);";
    private static final String GET_USER_SQL = "SELECT * FROM mcg_user WHERE username = ?;";


    public UserRepository() {
        super();
    }

    public boolean addUserToDb(String username, String displayName, String bio, String password) {
        if (this.dbConnection != null) {
            try {
                PreparedStatement addUserStatement = this.dbConnection.prepareStatement(ADD_USER_SQL);
                addUserStatement.setString(1, username);
                addUserStatement.setString(2, this.hashPassword(password));
                addUserStatement.setString(3, displayName);
                addUserStatement.setString(4, bio);
                addUserStatement.setInt(5, DEFAULT_COINS);
                addUserStatement.setInt(6, DEFAULT_ELO);

                int suc = addUserStatement.executeUpdate();
                System.out.println(suc);
                if (suc == 1) {
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    /*private void checkPass(String plainPassword, String hashedPassword) {
        if (BCrypt.checkpw(plainPassword, hashedPassword))
            System.out.println("The password matches.");
        else
            System.out.println("The password does not match.");
    }*/
}
