package game.objects;

import lombok.Data;

@Data
public class User {

    private String username;
    private String password;
    private String displayName;
    private String bio;


    private int coins = 20;

    private Stack stack;
    private Deck deck;

    // Elo
    private int elo;

    // security
    private String token;
}
