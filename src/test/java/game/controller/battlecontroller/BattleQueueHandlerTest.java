package game.controller.battlecontroller;

import game.helper.RepositoryHelper;
import game.http.response.Response;
import game.objects.Deck;
import game.objects.monstercards.*;
import game.objects.spellcards.FireSpell;
import game.objects.spellcards.WaterSpell;
import game.repository.DeckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.lenient;

class BattleQueueHandlerTest {

    @Mock
    private DeckRepository deckRepository;
    @Mock
    private RepositoryHelper repositoryHelper;

    private BattleQueueHandler battleQueueHandler;

    private static final String USERNAME_1 = "User1";
    private static final String USERNAME_2 = "User2";
    private static final String USERNAME_3 = "User3";

    private Deck user1Deck = new Deck(Arrays.asList(
            new DarkBat(),
            new FireElf(),
            new Ork(),
            new GreyGoblin(),
            new Knight()
    ));
    private Deck user2Deck = new Deck(Arrays.asList(
            new FireSpell(),
            new WaterSpell(),
            new WaterWitch(),
            new DarkEnt(),
            new FireWizard()
    ));

    @BeforeEach
    void setUp() {
        lenient().when(this.repositoryHelper.getDeckRepository()).thenReturn(deckRepository);
        lenient().when(deckRepository.getDeckByUsername(USERNAME_1)).thenReturn(Optional.of(user1Deck));
        lenient().when(deckRepository.getDeckByUsername(USERNAME_2)).thenReturn(Optional.of(user2Deck));
        lenient().when(deckRepository.getDeckByUsername(USERNAME_3)).thenReturn(Optional.empty());
        // empty request since the battle controller just needs the battleQueue
        this.battleQueueHandler = new BattleQueueHandler(repositoryHelper);
    }

    @Test
    void testHandleBattle() {
        CompletableFuture<Response> futureBattleUser1 = this.battleQueueHandler.addUserToBattleQueueAndHandleBattle(USERNAME_1);
        CompletableFuture<Response> futureBattleUser2 = this.battleQueueHandler.addUserToBattleQueueAndHandleBattle(USERNAME_2);


        assertThat(futureBattleUser1);
    }
}