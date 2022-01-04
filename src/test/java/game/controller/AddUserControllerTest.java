package game.controller;

import game.controller.usercontroller.AddUserController;
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

import static game.http.HttpReady.CONTENT_TYPE_TEXT_PLAIN;
import static game.http.enums.StatusCodeEnum.SC_201;
import static game.http.enums.StatusCodeEnum.SC_400;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddUserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RepositoryHelper repositoryHelper;

    @Mock
    private Request userRequest;

    private AddUserController userController;

    private static final String USERNAME_PASSWORD_ERROR_MESSAGE = "Username and Password must be longer than 4 characters!";

    private static final String USERNAME_1 = "User1";
    private static final String PASSWORD_1 = "Password1";

    private static final String USERNAME_2 = "User2";
    private static final String PASSWORD_2 = "1234";


    @BeforeEach
    void setUp() {
        lenient().when(this.repositoryHelper.getUserRepository()).thenReturn(this.userRepository);
        this.userController = new AddUserController(userRequest, repositoryHelper);
    }

    private void setUpLoginReturnTrue() {
        UserModel userModel = UserModel.builder().username(USERNAME_1).password(PASSWORD_1).build();
        when(this.userRequest.getModel()).thenReturn(userModel);
        lenient().when(this.userRepository.addUserToDb(userModel)).thenReturn(true);
    }

    private void setUpLoginReturnFalse() {
        UserModel userModel = UserModel.builder().username(USERNAME_2).password(PASSWORD_2).build();
        when(this.userRequest.getModel()).thenReturn(userModel);
        lenient().when(this.userRepository.addUserToDb(userModel)).thenReturn(false);
    }

    @Test
    void testDoWork201Response() {
        this.setUpLoginReturnTrue();
        Response response = this.userController.doWork();
        assertThat(response.getStatusCode()).isEqualTo(201);
        assertThat(response.getStatus()).isEqualTo(SC_201);
        assertThat(response.getContent()).contains(USERNAME_1);
        assertThat(response.getContentType()).isEqualTo(CONTENT_TYPE_TEXT_PLAIN.toString());
    }

    @Test
    void testDoWork400Response() {
        this.setUpLoginReturnFalse();
        Response response = this.userController.doWork();
        assertThat(response.getStatusCode()).isEqualTo(400);
        assertThat(response.getStatus()).isEqualTo(SC_400);
        assertThat(response.getContent()).contains(USERNAME_2);
        assertThat(response.getContentType()).isEqualTo(CONTENT_TYPE_TEXT_PLAIN.toString());
    }

    @Test
    void testDoWork400WhenEmptyResponse() {
        UserModel userModel = UserModel.builder().username("").password("").build();
        when(this.userRequest.getModel()).thenReturn(userModel);
        Response response = this.userController.doWork();
        assertThat(response.getStatusCode()).isEqualTo(400);
        assertThat(response.getStatus()).isEqualTo(SC_400);
        assertThat(response.getContent()).contains(USERNAME_PASSWORD_ERROR_MESSAGE);
        assertThat(response.getContentType()).isEqualTo(CONTENT_TYPE_TEXT_PLAIN.toString());
    }
}