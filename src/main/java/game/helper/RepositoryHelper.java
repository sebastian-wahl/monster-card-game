package game.helper;

import game.repository.CardRepositor;
import game.repository.DeckRepository;
import game.repository.StackRepository;
import game.repository.UserRepository;
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
