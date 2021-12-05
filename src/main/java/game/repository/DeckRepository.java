package game.repository;

import game.objects.Deck;

import java.util.Optional;

public class DeckRepository extends RepositoryBase {


    public DeckRepository() {
        super();
    }

    public Optional<Deck> getDeckByUsername(String username) {
        if (this.dbConnection != null) {
        }
        return Optional.empty();
    }
}
