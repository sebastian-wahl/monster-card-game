package game.helper;

import game.repository.*;
import lombok.Getter;

import java.sql.Connection;

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
    @Getter
    private TradeRepository tradeRepository;

    public RepositoryHelper() {
        this.deckRepository = new DeckRepository();
        this.userRepository = new UserRepository();
        this.cardRepository = new CardRepository();
        this.stackRepository = new StackRepository();
        this.packageRepository = new PackageRepository();
        this.tradeRepository = new TradeRepository();
    }

    public void setConnection(Connection connection) {
        this.deckRepository.setConnection(connection);
        this.userRepository.setConnection(connection);
        this.cardRepository.setConnection(connection);
        this.stackRepository.setConnection(connection);
        this.packageRepository.setConnection(connection);
        this.tradeRepository.setConnection(connection);
    }
}
