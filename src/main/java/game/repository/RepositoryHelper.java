package game.repository;

import lombok.Getter;

public class RepositoryHelper {
    @Getter
    private DeckRepository deckRepository;
    @Getter
    private UserRepository userRepository;
    @Getter
    private CardRepositor cardRepositor;
    @Getter
    private StackRepository stackRepository;

    public RepositoryHelper() {
        this.deckRepository = new DeckRepository();
        this.userRepository = new UserRepository();
        this.cardRepositor = new CardRepositor();
        this.stackRepository = new StackRepository();
    }
}
