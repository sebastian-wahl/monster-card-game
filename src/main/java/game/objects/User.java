package game.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@AllArgsConstructor
@Data
public class User {
    /* Basic attr. */
    private Long id;
    private String username;
    private String password;
    private String displayName;
    private String bio;

    private int coins;
    private double elo;
    // security
    private String securityToken;
    private Timestamp securityTokenTimestamp;

    /* Advanced attr. */
    private Stack stack;
    private Deck deck;

    public User() {
    }

    public void buyPackage() {
        if (coins - Package.PACKAGE_COST >= 0) {
            this.stack.addPackageToStack(new Package());
        } else {
            // Not enough coins
        }
    }
}
