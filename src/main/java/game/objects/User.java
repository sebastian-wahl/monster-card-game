package game.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class User {

    @Getter
    private String username;
    @Getter
    private String password;
    @Getter
    @Setter
    private String displayName;
    @Getter
    @Setter
    private String bio;


    @Getter
    private int coins;

    @Getter
    private Stack stack;
    @Getter
    private Deck deck;

    // Elo
    private int elo;

    // security
    private String token;

    public void buyPackage() {
        if (coins - Package.PACKAGE_COST >= 0) {
            this.stack.addPackageToStack(new Package());
        } else {
            // Not enough coins
        }
    }
}
