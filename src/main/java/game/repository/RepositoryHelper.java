package game.repository;

import lombok.Getter;

public class RepositoryHelper {
    @Getter
    private DeckRepository deckRepository;
    @Getter
    private UserRepository userRepository;

    public RepositoryHelper() {
        this.deckRepository = new DeckRepository();
        this.userRepository = new UserRepository();
    }
}
