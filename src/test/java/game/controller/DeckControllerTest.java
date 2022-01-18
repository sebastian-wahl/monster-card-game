package game.controller;

import game.helper.RepositoryHelper;
import game.http.HttpMethod;
import game.http.models.DeckModel;
import game.http.request.Request;
import game.http.response.Response;
import game.http.url.ConcreteUrl;
import game.objects.CardBase;
import game.objects.Deck;
import game.objects.User;
import game.objects.monstercards.*;
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
import java.util.UUID;
import java.util.stream.Collectors;

import static game.controller.ControllerBase.WRONG_SECURITY_TOKEN_ERROR_MESSAGE;
import static game.http.enums.StatusCodeEnum.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class DeckControllerTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private DeckRepository deckRepository;

    @Mock
    private RepositoryHelper repositoryHelper;

    @Mock
    private Request userRequest;

    private DeckController deckController;

    private Deck deckSet = new Deck(Arrays.asList(
            new DarkBat(),
            new FireElf(),
            new Orc(),
            new GreyGoblin(),
            new Knight()
    ));
    private Deck deckNew = new Deck(Arrays.asList(
            new WaterWitch(),
            new WaterSpell(),
            new FireWizard(),
            new Dragon(),
            new FireElf()
    ));

    private static final String SECURITY_TOKEN = "TOKEN123";
    private static final String WRONG_SEC_TOKEN = "12345";

    private static final String USERNAME_1 = "user1";

    private final User user1 = User.builder().username(USERNAME_1).deck(this.deckSet).build();

    @BeforeEach
    void setUp() {
        try {
            lenient().when(this.repositoryHelper.getUserRepository()).thenReturn(this.userRepository);
            lenient().when(this.repositoryHelper.getDeckRepository()).thenReturn(this.deckRepository);
            lenient().when(this.userRequest.getAuthorizationToken()).thenReturn(SECURITY_TOKEN);
            lenient().when(this.userRequest.getUrl()).thenReturn(new ConcreteUrl("deck"));
            lenient().when(this.repositoryHelper.getUserRepository().checkTokenAndGetUser(SECURITY_TOKEN)).thenReturn(Optional.of(user1));
            lenient().when(this.deckRepository.getDeckByUsername(USERNAME_1)).thenReturn(this.deckSet);
            this.deckController = new DeckController(userRequest, repositoryHelper);
        } catch (SQLException e) {
            fail("An exception was thrown during the setUp: " + e.getMessage());
        }
    }

    @Test
    void test_GET_DoWorkOk200() {
        try {
            lenient().when(this.userRequest.getMethod()).thenReturn(HttpMethod.GET);
            Response response = this.deckController.doWorkIntern();
            assertThat(response.getStatus()).isEqualTo(SC_200);
            assertThat(response.getContent()).isEqualTo(this.deckSet.toString());
        } catch (SQLException e) {
            fail("An exception was thrown during the execution: " + e.getMessage());
        }
    }

    @Test
    void test_PUT_DoWorkOk200() {
        try {
            lenient().when(this.userRequest.getMethod()).thenReturn(HttpMethod.PUT);
            lenient().when(this.userRequest.getModel()).thenReturn(DeckModel.builder()
                    .ids(this.deckNew.getCards().stream()
                            .map(CardBase::getId)
                            .map(UUID::toString)
                            .collect(Collectors.toList())
                            .toArray(new String[this.deckNew.getDeckSize()]))
                    .build());
            User userChangedDeck = user1.copy();
            userChangedDeck.setDeck(deckNew);
            lenient().when(this.deckRepository.setUserDeck(eq(user1), any())).thenReturn(Optional.of(userChangedDeck));
            Response response = this.deckController.doWorkIntern();
            assertThat(response.getStatus()).isEqualTo(SC_200);
            assertThat(response.getContent()).isEqualTo(this.deckNew.toString());
        } catch (SQLException e) {
            fail("An exception was thrown during the execution: " + e.getMessage());
        }
    }

    @Test
    void test_POST_DoWorkError400() {
        try {
            lenient().when(this.userRequest.getMethod()).thenReturn(HttpMethod.PUT);
            Response response = this.deckController.doWorkIntern();
            assertThat(response.getStatus()).isEqualTo(SC_400);
        } catch (SQLException e) {
            fail("An exception was thrown during the execution: " + e.getMessage());
        }
    }

    @Test
    void testDoWork_Ok401_WrongLogin() {
        try {
            lenient().when(this.userRequest.getAuthorizationToken()).thenReturn(WRONG_SEC_TOKEN);
            Response response = this.deckController.doWorkIntern();
            assertThat(response.getStatus()).isEqualTo(SC_401);
            assertThat(response.getContent()).isEqualTo(WRONG_SECURITY_TOKEN_ERROR_MESSAGE);
        } catch (SQLException e) {
            fail("An exception was thrown during the execution: " + e.getMessage());
        }
    }
}