package game.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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
    private String image;

    private int coins = -1;
    private double elo = -1;

    // stats
    UserStatistics userStatistics;

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
        this.image = user.getImage();
        this.coins = user.getCoins();
        this.elo = user.getElo();

        this.stack = user.getStack();
        this.deck = user.getDeck();

        this.userStatistics = user.getUserStatistics() != null ? user.getUserStatistics().copy() : new UserStatistics();
    }

    /**
     * @return Returns a deep copy of this object
     */
    public User copy() {
        return new User(this);
    }

    public int compareEloToHighToLow(User another) {
        if (this.elo == another.getElo())
            return 0;
        return this.elo > another.elo ? -1 : 1;
    }

    public int compareEloToLowToHigh(User another) {
        if (this.elo == another.getElo())
            return 0;
        return this.elo > another.elo ? 1 : -1;
    }

    private String replaceNullWithUndefined(String text) {
        return text != null ? text : "Undefined";
    }

    public String toNameString() {
        return "{\"User\": {" +
                "\"Username\": \"" + this.username + "\", " +
                "\"Displayname\": \"" + this.replaceNullWithUndefined(this.displayName) + "\"" +
                "}}";
    }

    @Override
    public String toString() {
        return "{\"User\": {" +
                "\"Username\": \"" + this.username + "\", " +
                "\"Display Name\": \"" + replaceNullWithUndefined(this.displayName) + "\", " +
                "\"Bio\": \"" + replaceNullWithUndefined(this.bio) + "\", " +
                "\"Image\": \"" + replaceNullWithUndefined(this.image) + "\", " +
                "\"Elo\": \"" + this.elo + "\", " +
                "\"Statistics:\": { \"Games played\": " + this.userStatistics.getGamesPlayed() + ", " +
                "\"Total Wins\": " + this.userStatistics.getWinCount() + ", " +
                "\"Total Loses\": " + this.userStatistics.getLoseCount() + ", " +
                "\"Win/Lose Ratio:\": " + this.userStatistics.getWinRatio() + ", " +
                "\"Total Ties\": " + this.userStatistics.getTieCount() + "}" +
                "}}";
    }

    public String getStatisticString() {
        return "{\"Statistics:\": { " +
                "\"Elo\": " + this.elo + ", " +
                "\"Games played\": " + this.userStatistics.getGamesPlayed() + ", " +
                "\"Total Wins\": " + this.userStatistics.getWinCount() + ", " +
                "\"Total Loses\": " + this.userStatistics.getLoseCount() + ", " +
                "\"Win/Lose Ratio:\": " + this.userStatistics.getWinRatio() + ", " +
                "\"Total Ties\": " + this.userStatistics.getTieCount() +
                "}}";
    }
}
