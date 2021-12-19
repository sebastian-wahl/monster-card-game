package game.repository;

public class CardRepositor extends RepositoryBase {

    private static final String ADD_CARD_SQL = "INSERT INTO card (id, name, damage) VALUES (?, ?, ?);";

}
