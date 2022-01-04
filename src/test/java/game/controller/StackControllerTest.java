package game.controller;

import game.helper.RepositoryHelper;
import game.http.request.Request;
import game.http.response.Response;
import game.objects.Stack;
import game.objects.User;
import game.objects.monstercards.*;
import game.objects.spellcards.WaterSpell;
import game.repository.StackRepository;
import game.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static game.controller.ControllerBase.WRONG_SECURITY_TOKEN_ERROR_MESSAGE;
import static game.http.enums.StatusCodeEnum.SC_200;
import static game.http.enums.StatusCodeEnum.SC_401;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class StackControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StackRepository stackRepository;

    @Mock
    private RepositoryHelper repositoryHelper;

    @Mock
    private Request userRequest;

    private StackController stackController;

    private static final String SECURITY_TOKEN = "TOKEN123";
    private static final String WRONG_SEC_TOKEN = "12345";

    private static final String USERNAME_1 = "user1";


    private Stack user1Stack = new Stack(Arrays.asList(
            new WaterWitch(),
            new WaterSpell(),
            new FireWizard(),
            new Dragon(),
            new FireElf(),
            new DarkEnt(),
            new Kraken()
    ));

    private final User user1 = User.builder().username(USERNAME_1).stack(user1Stack).build();

    @BeforeEach
    void setUp() {
        lenient().when(this.repositoryHelper.getUserRepository()).thenReturn(userRepository);
        lenient().when(this.repositoryHelper.getStackRepository()).thenReturn(stackRepository);

        lenient().when(this.userRequest.getAuthorizationToken()).thenReturn(SECURITY_TOKEN);

        lenient().when(this.userRepository.checkTokenAndGetUser(SECURITY_TOKEN)).thenReturn(Optional.of(user1));
        lenient().when(this.stackRepository.getUserStack(user1)).thenReturn(Optional.of(user1));
        this.stackController = new StackController(this.userRequest, repositoryHelper);
    }

    @Test
    void testDoWork_Ok200() {
        Response response = this.stackController.doWork();

        assertThat(response.getStatus()).isEqualTo(SC_200);
        assertThat(response.getContent()).isEqualTo(this.user1Stack.toString());
    }

    @Test
    void testDoWork_Ok401_WrongLogin() {
        lenient().when(this.userRequest.getAuthorizationToken()).thenReturn(WRONG_SEC_TOKEN);
        Response response = this.stackController.doWork();

        assertThat(response.getStatus()).isEqualTo(SC_401);
        assertThat(response.getContent()).isEqualTo(WRONG_SECURITY_TOKEN_ERROR_MESSAGE);
    }
}