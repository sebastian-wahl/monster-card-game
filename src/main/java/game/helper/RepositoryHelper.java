package game.helper;

import game.repository.*;
import lombok.Getter;

public class RepositoryHelper {
    @Getter
    private DeckRepository deckRepository;
    @Getter
    private UserRepository userRepository;
    @Getter
    private CardRepository cardRepository;
    @Getter
    private StackRepository stackRepository;
    @Getter
    private PackageRepository packageRepository;

    public RepositoryHelper() {
        this.deckRepository = new DeckRepository();
        this.userRepository = new UserRepository();
        this.cardRepository = new CardRepository();
        this.stackRepository = new StackRepository();
        this.packageRepository = new PackageRepository();
    }
}
