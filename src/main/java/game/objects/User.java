package game.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@AllArgsConstructor
@Data
@Builder
public class User {
    /* Basic attr. */
    private Long id;
    private String username;
    private String password;
    private String displayName;
    private String bio;

    private int coins;
    private double elo;

    // stats
    UserStatistics userStatistics;

    // security
    private String securityToken;
    private Timestamp securityTokenTimestamp;

    /* Advanced attr. */
    private Stack stack;
    private Deck deck;

    public User() {
        this.userStatistics = new UserStatistics();
    }

    /**
     * Create a new User Object with a new reference but the same properties (same reference)
     *
     * @param user
     */
    public User(User user) {
        this.id = user.id;
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.displayName = user.getDisplayName();
        this.bio = user.getBio();
        this.coins = user.getCoins();
        this.elo = user.getElo();
        this.securityToken = user.getSecurityToken();
        this.securityTokenTimestamp = user.getSecurityTokenTimestamp();

        this.stack = user.getStack();
        this.deck = user.getDeck();

        this.userStatistics = user.getUserStatistics();
    }

    public void buyPackage() {
        if (coins - Package.PACKAGE_COST >= 0) {
            this.stack.addPackageToStack(new Package());
        } else {
            // Not enough coins
        }
    }

    public User copy() {
        return new User(this);
    }
}
