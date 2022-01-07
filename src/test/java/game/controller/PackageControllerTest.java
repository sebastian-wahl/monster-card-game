package game.controller;

import game.helper.RepositoryHelper;
import game.http.request.Request;
import game.http.response.Response;
import game.http.url.ConcreteUrl;
import game.objects.Package;
import game.objects.User;
import game.repository.CardRepository;
import game.repository.StackRepository;
import game.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.Optional;

import static game.controller.ControllerBase.WRONG_SECURITY_TOKEN_ERROR_MESSAGE;
import static game.http.enums.StatusCodeEnum.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class PackageControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private StackRepository stackRepository;

    @Mock
    private RepositoryHelper repositoryHelper;

    @Mock
    private Request userRequest;

    private PackageController packageController;

    private static final String SECURITY_TOKEN = "TOKEN123";
    private static final String WRONG_SEC_TOKEN = "12345";

    private static final String USERNAME_1 = "user1";
    private static final String NOT_ENOUGH_COINS_ERROR_MESSAGE = "Not enough coins for this action.";

    private final User user1 = User.builder().username(USERNAME_1).coins(20).build();

    @BeforeEach
    void setUp() {
        try {
            lenient().when(this.repositoryHelper.getUserRepository()).thenReturn(this.userRepository);
            lenient().when(this.repositoryHelper.getStackRepository()).thenReturn(this.stackRepository);
            lenient().when(this.repositoryHelper.getCardRepository()).thenReturn(this.cardRepository);


            lenient().when(this.userRequest.getAuthorizationToken()).thenReturn(SECURITY_TOKEN);
            lenient().when(this.userRequest.getUrl()).thenReturn(new ConcreteUrl("packages"));
            lenient().when(this.repositoryHelper.getUserRepository().checkTokenAndGetUser(SECURITY_TOKEN)).thenReturn(Optional.of(user1));
            this.packageController = new PackageController(userRequest, repositoryHelper);
        } catch (SQLException e) {
            fail("An exception was thrown during the setUp: " + e.getMessage());
        }
    }

    @Test
    void testDoWork_buyPackage_OK200() {
        try {
            lenient().when(this.userRepository.update(user1)).thenReturn(Optional.of(user1));
            lenient().when(this.stackRepository.addCardsToUserStack(eq(user1), any(Package.class))).thenReturn(Optional.of(user1));

            Response response = this.packageController.doWorkIntern();

            assertThat(response.getStatus()).isEqualTo(SC_200);
        } catch (SQLException e) {
            fail("An exception was thrown during the execution: " + e.getMessage());
        }

    }

    @Test
    void testDoWork_buyPackage_Error400_NoCoins() {
        try {
            User user = user1.copy();
            user.setCoins(0);
            lenient().when(this.repositoryHelper.getUserRepository().checkTokenAndGetUser(SECURITY_TOKEN)).thenReturn(Optional.of(user));
            Response response = this.packageController.doWorkIntern();
            assertThat(response.getStatus()).isEqualTo(SC_400);
            assertThat(response.getContent()).isEqualTo(NOT_ENOUGH_COINS_ERROR_MESSAGE);
        } catch (SQLException e) {
            fail("An exception was thrown during the execution: " + e.getMessage());
        }
    }

    @Test
    void testDoWork_Ok401_WrongLogin() {
        try {
            lenient().when(this.userRequest.getAuthorizationToken()).thenReturn(WRONG_SEC_TOKEN);
            Response response = this.packageController.doWorkIntern();
            assertThat(response.getStatus()).isEqualTo(SC_401);
            assertThat(response.getContent()).isEqualTo(WRONG_SECURITY_TOKEN_ERROR_MESSAGE);
        } catch (SQLException e) {
            fail("An exception was thrown during the execution: " + e.getMessage());
        }
    }
}