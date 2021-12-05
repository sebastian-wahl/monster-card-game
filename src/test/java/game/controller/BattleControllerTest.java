package game.controller;

import game.objects.Deck;
import game.objects.exceptions.DeckNotFoundException;
import game.objects.monstercards.*;
import game.objects.spellcards.FireSpell;
import game.objects.spellcards.WaterSpell;
import game.repository.DeckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class BattleControllerTest {

    @Mock
    private BlockingQueue<String> battleQueue;
    @Mock
    private DeckRepository deckRepository;

    private BattleController battleController;

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

    private void setUpBattleQueue(String user1, String user2) {
        when(battleQueue.poll()).thenReturn(user1).thenReturn(user2);
    }

    @BeforeEach
    void setUp() {
        lenient().when(deckRepository.getDeckByUsername(USERNAME_1)).thenReturn(Optional.of(user1Deck));
        lenient().when(deckRepository.getDeckByUsername(USERNAME_2)).thenReturn(Optional.of(user2Deck));
        lenient().when(deckRepository.getDeckByUsername(USERNAME_3)).thenReturn(Optional.empty());
        // empty request since the battle controller just needs the battleQueue
        this.battleController = new BattleController(battleQueue, null, deckRepository);
    }

    @Test
    void testHandleBattle() {
        this.setUpBattleQueue(USERNAME_1, USERNAME_2);
        assertDoesNotThrow(() -> this.battleController.handleBattle());
    }

    @Test
    void testHandleBattleExpectDeckNotFoundException() {

        DeckNotFoundException ex = assertThrows(DeckNotFoundException.class, () -> {
            this.setUpBattleQueue(USERNAME_1, USERNAME_3);

            this.battleController.handleBattle();
        });
        assertThat(ex).hasMessageContaining(USERNAME_3);
    }


}