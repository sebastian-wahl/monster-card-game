package dbobjects;

import lombok.Data;

import java.util.List;

@Data
public class User {

    private String username;
    private String password;
    private String name;


    private int coins = 20;

    private List<CardBase> stack;
    private List<CardBase> deck;

    // Elo
    private int elo;

    // security
    private String token;
}
