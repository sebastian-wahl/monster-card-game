package game.controller;

import game.helper.RepositoryHelper;
import game.http.request.Request;
import game.http.response.Response;
import game.http.url.ConcreteUrl;
import game.objects.User;
import game.objects.UserStatistics;
import game.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static game.controller.ControllerBase.WRONG_SECURITY_TOKEN_ERROR_MESSAGE;
import static game.http.enums.StatusCodeEnum.SC_200;
import static game.http.enums.StatusCodeEnum.SC_401;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class ScoreboardControllerTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RepositoryHelper repositoryHelper;

    @Mock
    private Request userRequest;

    private ScoreboardController scoreboardController;

    private static final String SECURITY_TOKEN = "TOKEN123";
    private static final String WRONG_SEC_TOKEN = "12345";

    private static final String SIMPLE_FORMAT_URL = "score?format=simple";
    private static final String NORMAL_FORMAT_URL = "score";

    private static final String USERNAME_1 = "user1";
    private static final String USERNAME_2 = "user2";
    private static final String USERNAME_3 = "user3";
    private final User user1 = User.builder().username(USERNAME_1).elo(120).userStatistics(
                    UserStatistics.builder()
                            .winCount(1)
                            .loseCount(1)
                            .tieCount(1)
                            .build())
            .build();

    private final User user2 = User.builder().username(USERNAME_2).elo(140).userStatistics(
                    UserStatistics.builder()
                            .winCount(2)
                            .loseCount(2)
                            .tieCount(2)
                            .build())
            .build();

    private final User user3 = User.builder().username(USERNAME_3).elo(160).userStatistics(
                    UserStatistics.builder()
                            .winCount(3)
                            .loseCount(3)
                            .tieCount(3)
                            .build())
            .build();

    @BeforeEach
    void setUp() {
        try {
            lenient().when(this.repositoryHelper.getUserRepository()).thenReturn(userRepository);
            lenient().when(this.userRequest.getAuthorizationToken()).thenReturn(SECURITY_TOKEN);
            lenient().when(this.userRepository.checkTokenAndGetUser(SECURITY_TOKEN)).thenReturn(Optional.of(user1));
            lenient().when(this.userRepository.getAllUsers()).thenReturn(List.of(this.user1, this.user2, this.user3));
            this.scoreboardController = new ScoreboardController(this.userRequest, repositoryHelper);
        } catch (SQLException e) {
            fail("An exception was thrown during the setUp: " + e.getMessage());
        }
    }

    @Test
    void testDoWork_Ok200_SimpleFormat() {
        try {
            Response response = null;
            lenient().when(this.userRequest.getUrl()).thenReturn(new ConcreteUrl(SIMPLE_FORMAT_URL));
            response = this.scoreboardController.doWorkIntern();
            assertThat(response.getStatus()).isEqualTo(SC_200);
            assertThat(response.getContent()).isEqualTo("{\"Scoreboard\": {\"1.\": \"" + USERNAME_3 + "\", \"2.\": \"" + USERNAME_2 + "\", \"3.\": \"" + USERNAME_1 + "\"}}");
        } catch (SQLException e) {
            fail("An exception was thrown during the execution: " + e.getMessage());
        }
    }


    @Test
    void testDoWork_Ok200_Extended() {
        try {
            lenient().when(this.userRequest.getUrl()).thenReturn(new ConcreteUrl(NORMAL_FORMAT_URL));
            Response response = this.scoreboardController.doWorkIntern();
            assertThat(response.getStatus()).isEqualTo(SC_200);
            assertThat(response.getContent()).isEqualTo("{\"Scoreboard\": {\"1.\": " + user3.toString() + ", \"2.\": " + user2.toString() + ", \"3.\": " + user1.toString() + "}}");
        } catch (SQLException e) {
            fail("An exception was thrown during the execution: " + e.getMessage());
        }
    }

    @Test
    void testDoWork_Ok401_WrongLogin() {
        try {
            lenient().when(this.userRequest.getAuthorizationToken()).thenReturn(WRONG_SEC_TOKEN);
            Response response = this.scoreboardController.doWorkIntern();

            assertThat(response.getStatus()).isEqualTo(SC_401);
            assertThat(response.getContent()).isEqualTo(WRONG_SECURITY_TOKEN_ERROR_MESSAGE);
        } catch (SQLException e) {
            fail("An exception was thrown during the execution: " + e.getMessage());
        }
    }
}