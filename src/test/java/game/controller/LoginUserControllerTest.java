package game.controller;

import game.controller.usercontroller.LoginUserController;
import game.helper.RepositoryHelper;
import game.http.models.UserModel;
import game.http.request.Request;
import game.http.response.Response;
import game.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static game.http.HttpReady.CONTENT_TYPE_APPLICATION_JSON;
import static game.http.HttpReady.CONTENT_TYPE_TEXT_PLAIN;
import static game.http.enums.StatusCodeEnum.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

    private static final String USERNAME_PASSWORD_TO_SHORT_ERROR_MESSAGE = "Username and Password must be longer than 4 characters!";
    private static final String USERNAME_PASSWORD_ERROR_MESSAGE = "Login failed. Please check username and password.";
    private static final String USERNAME_1 = "User1";
    private static final String PASSWORD_1 = "Password1";

    private static final String USERNAME_2 = "User2";
    private static final String PASSWORD_2 = "1234";

    @BeforeEach
    void setUp() {
        lenient().when(repositoryHelper.getUserRepository()).thenReturn(userRepository);
        lenient().when(userRepository.generateSecurityToken(any())).thenReturn(Optional.of("SecurityToken"));
        this.userController = new LoginUserController(userRequest, repositoryHelper);
    }

    private void setUpLoginReturnTrue() {
        UserModel userModel = UserModel.builder().username(USERNAME_1).password(PASSWORD_1).build();
        when(this.userRequest.getModel()).thenReturn(userModel);
        lenient().when(this.userRepository.login(userModel)).thenReturn(true);
    }

    private void setUpLoginReturnFalse() {
        UserModel userModel = UserModel.builder().username(USERNAME_2).password(PASSWORD_2).build();
        when(this.userRequest.getModel()).thenReturn(userModel);
        lenient().when(this.userRepository.login(userModel)).thenReturn(false);
    }

    @Test
    void testDoWork201Response() {
        this.setUpLoginReturnTrue();
        Response response = this.userController.doWork();
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getStatus()).isEqualTo(SC_200);
        assertThat(response.getContent()).contains("\"Authorization\"");
        assertThat(response.getContent()).contains("\"ValidUntil\"");
        assertThat(response.getContentType()).isEqualTo(CONTENT_TYPE_APPLICATION_JSON.toString());
    }

    @Test
    void testDoWork401Response() {
        this.setUpLoginReturnFalse();
        Response response = this.userController.doWork();
        assertThat(response.getStatusCode()).isEqualTo(401);
        assertThat(response.getStatus()).isEqualTo(SC_401);
        assertThat(response.getContent()).contains(USERNAME_PASSWORD_ERROR_MESSAGE);
        assertThat(response.getContentType()).isEqualTo(CONTENT_TYPE_TEXT_PLAIN.toString());
    }

    @Test
    void testDoWork400Response() {
        UserModel userModel = UserModel.builder().username("").password("").build();
        when(this.userRequest.getModel()).thenReturn(userModel);
        Response response = this.userController.doWork();
        assertThat(response.getStatusCode()).isEqualTo(400);
        assertThat(response.getStatus()).isEqualTo(SC_400);
        assertThat(response.getContent()).contains(USERNAME_PASSWORD_TO_SHORT_ERROR_MESSAGE);
        assertThat(response.getContentType()).isEqualTo(CONTENT_TYPE_TEXT_PLAIN.toString());
    }
}