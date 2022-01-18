package game.controller.battlecontroller;

import game.helper.RepositoryHelper;
import game.http.response.Response;
import game.objects.Deck;
import game.objects.User;
import game.objects.monstercards.*;
import game.objects.spellcards.FireSpell;
import game.objects.spellcards.WaterSpell;
import game.repository.DeckRepository;
import game.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static game.http.enums.StatusCodeEnum.SC_200;
import static game.http.enums.StatusCodeEnum.SC_400;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class BattleQueueHandlerTest {

    @Mock
    private DeckRepository deckRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RepositoryHelper repositoryHelper;

    private BattleQueueHandler battleQueueHandler;

    private static final String USERNAME_1 = "User1";
    private static final String USERNAME_2 = "User2";
    private static final String USERNAME_3 = "User3";

    private static final String USER_DOUBLE_QUEUE_ERROR_MSG = "User is already queued in another connection.";
    private static final String NO_DECK_ERROR_MESSAGE = "User: \"" + USERNAME_3 + "\" hasn't selected a deck yet! A battle cannot start if a user has not selected a deck!";

    private Deck user1Deck = new Deck(Arrays.asList(
            new DarkBat(),
            new FireElf(),
            new Orc(),
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

    private Deck user3Deck = new Deck();


    @BeforeEach
    void setUp() {
        try {
            lenient().when(repositoryHelper.getDeckRepository()).thenReturn(deckRepository);
            lenient().when(repositoryHelper.getUserRepository()).thenReturn(userRepository);
            // setup decks
            lenient().when(deckRepository.getDeckByUsername(USERNAME_1)).thenReturn(user1Deck);
            lenient().when(deckRepository.getDeckByUsername(USERNAME_2)).thenReturn(user2Deck);
            lenient().when(deckRepository.getDeckByUsername(USERNAME_3)).thenReturn(user3Deck);
            lenient().when(userRepository.updateTieAndGamesPlayed(any())).thenReturn(true);
            lenient().when(userRepository.updateEloAndGamesPlayed(any(), any())).thenReturn(true);
            lenient().when(userRepository.getUser(USERNAME_1)).thenReturn(Optional.of(User.builder().username(USERNAME_1).build()));
            lenient().when(userRepository.getUser(USERNAME_2)).thenReturn(Optional.of(User.builder().username(USERNAME_2).build()));

            // setup userrepo calls
            // empty request since the battle controller just needs the battleQueue
            this.battleQueueHandler = new BattleQueueHandler(repositoryHelper);
        } catch (SQLException e) {
            fail("An exception was thrown during the setUp: " + e.getMessage());
        }
    }

    @Test
    void testHandleBattleOk200() {
        try {
            CompletableFuture<Response> futureBattleUser1 = this.battleQueueHandler.addUserToBattleQueueAndHandleBattle(USERNAME_1);
            CompletableFuture<Response> futureBattleUser2 = this.battleQueueHandler.addUserToBattleQueueAndHandleBattle(USERNAME_2);
            // futureBattle user 1
            Response user1 = futureBattleUser1.get();
            Response user2 = futureBattleUser2.get();

            assertThat(user1.getStatus()).isEqualTo(SC_200);
            assertThat(user2.getStatus()).isEqualTo(SC_200);

            assertThat(user1.getContent()).isNotEmpty();
            assertThat(user2.getContent()).isNotEmpty();

            assertThat(user1.getContent()).isEqualTo(user2.getContent());
        } catch (InterruptedException | ExecutionException | SQLException e) {
            fail("An exception was thrown during the execution: " + e.getMessage());
        }
    }

    @Test
    void testHandleBattleError400_UserDoubleInQueue() {

        try {
            // first return can be ignored
            this.battleQueueHandler.addUserToBattleQueueAndHandleBattle(USERNAME_1);
            CompletableFuture<Response> futureBattleUser1_2 = this.battleQueueHandler.addUserToBattleQueueAndHandleBattle(USERNAME_1);
            // futureBattle user 1
            Response user1_2 = futureBattleUser1_2.get();

            assertThat(user1_2.getStatus()).isEqualTo(SC_400);
            assertThat(user1_2.getContent()).isEqualTo(USER_DOUBLE_QUEUE_ERROR_MSG);
        } catch (InterruptedException | ExecutionException | SQLException e) {
            fail("An exception was thrown during the execution: " + e.getMessage());
        }
    }

    @Test
    void testHandleBattleError400_NoDeckDefined() {
        try {
            // first return can be ignored
            this.battleQueueHandler.addUserToBattleQueueAndHandleBattle(USERNAME_1);
            CompletableFuture<Response> futureBattleUser = this.battleQueueHandler.addUserToBattleQueueAndHandleBattle(USERNAME_3);
            // futureBattle user 1
            Response user = futureBattleUser.get();

            assertThat(user.getStatus()).isEqualTo(SC_400);
            assertThat(user.getContent()).isEqualTo(NO_DECK_ERROR_MESSAGE);
        } catch (InterruptedException | ExecutionException | SQLException e) {
            fail("An exception was thrown during the execution: " + e.getMessage());
        }
    }
}