package game.controller;

import game.controller.usercontroller.LoginUserController;
import game.http.HttpReady;
import game.http.request.Request;
import game.http.response.Response;
import game.repository.RepositoryHelper;
import game.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static game.http.enums.StatusCodeEnum.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUserControllerTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RepositoryHelper repositoryHelper;

    @Mock
    private Request userRequest;

    private LoginUserController userController;

    private static final String USERNAME_1 = "User1";
    private static final String PASSWORD_1 = "Password1";

    private static final String USERNAME_2 = "User2";
    private static final String PASSWORD_2 = "1234";

    @BeforeEach
    void setUp() {
        lenient().when(repositoryHelper.getUserRepository()).thenReturn(userRepository);
        this.userController = new LoginUserController(userRequest, repositoryHelper);
    }

    private void setUpLoginReturnTrue() {
        Map<String, String> map = Map.of(HttpReady.USERNAME_KEY, USERNAME_1, HttpReady.PASSWORD_KEY, PASSWORD_1);
        when(this.userRequest.getContent()).thenReturn(map);
        lenient().when(this.userRepository.login(eq(USERNAME_1), eq(PASSWORD_1))).thenReturn(true);
    }

    private void setUpLoginReturnFalse() {
        Map<String, String> map = Map.of(HttpReady.USERNAME_KEY, USERNAME_2, HttpReady.PASSWORD_KEY, PASSWORD_2);
        when(this.userRequest.getContent()).thenReturn(map);
        lenient().when(this.userRepository.login(eq(USERNAME_1), eq(PASSWORD_1))).thenReturn(true);
    }

    @Test
    void testDoWork201Response() {
        this.setUpLoginReturnTrue();
        Response response = this.userController.doWork();
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getStatus()).isEqualTo(SC_200);
        assertThat(response.getContent()).contains(USERNAME_1);
        assertThat(response.getContentType()).isEqualTo(HttpReady.CONTENT_TYPE_APPLICATION_JSON);
    }

    @Test
    void testDoWork401Response() {
        this.setUpLoginReturnFalse();
        Response response = this.userController.doWork();
        assertThat(response.getStatusCode()).isEqualTo(401);
        assertThat(response.getStatus()).isEqualTo(SC_401);
        assertThat(response.getContent()).contains("Login failed. Please check username and password.");
        assertThat(response.getContentType()).isEqualTo(HttpReady.CONTENT_TYPE_TEXT_PLAIN);
    }

    @Test
    void testDoWork400Response() {
        Map<String, String> map = Map.of("", "");
        when(this.userRequest.getContent()).thenReturn(map);
        Response response = this.userController.doWork();
        assertThat(response.getStatusCode()).isEqualTo(400);
        assertThat(response.getStatus()).isEqualTo(SC_400);
        assertThat(response.getContent()).contains("Username and Password must not be empty!");
        assertThat(response.getContentType()).isEqualTo(HttpReady.CONTENT_TYPE_TEXT_PLAIN);
    }
}