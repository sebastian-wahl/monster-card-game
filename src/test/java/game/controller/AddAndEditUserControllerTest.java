package game.controller;

import game.controller.usercontroller.AddAndEditUserController;
import game.helper.RepositoryHelper;
import game.http.models.UserModel;
import game.http.request.Request;
import game.http.response.Response;
import game.http.url.ConcreteUrl;
import game.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;

import static game.http.HttpReady.CONTENT_TYPE_TEXT_PLAIN;
import static game.http.enums.StatusCodeEnum.SC_201;
import static game.http.enums.StatusCodeEnum.SC_400;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddAndEditUserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RepositoryHelper repositoryHelper;

    @Mock
    private Request userRequest;

    private AddAndEditUserController userController;

    private static final String USERNAME_PASSWORD_ERROR_MESSAGE = "Username and Password must be longer than 4 characters!";

    private static final String USERNAME_1 = "User1";
    private static final String PASSWORD_1 = "Password1";

    private static final String USERNAME_2 = "User2";
    private static final String PASSWORD_2 = "1234";


    @BeforeEach
    void setUp() {
        lenient().when(this.repositoryHelper.getUserRepository()).thenReturn(this.userRepository);
        lenient().when(this.userRequest.getUrl()).thenReturn(new ConcreteUrl("deck"));
        this.userController = new AddAndEditUserController(userRequest, repositoryHelper);
    }

    private void setUpLoginReturnTrue() {
        try {
            UserModel userModel = UserModel.builder().username(USERNAME_1).password(PASSWORD_1).build();
            when(this.userRequest.getModel()).thenReturn(userModel);
            lenient().when(this.userRepository.addUserToDb(userModel)).thenReturn(true);
        } catch (SQLException e) {
            fail("An exception was thrown during the execution: " + e.getMessage());
        }
    }

    private void setUpLoginReturnFalse() {
        try {
            UserModel userModel = UserModel.builder().username(USERNAME_2).password(PASSWORD_2).build();
            when(this.userRequest.getModel()).thenReturn(userModel);
            lenient().when(this.userRepository.addUserToDb(userModel)).thenReturn(false);
        } catch (SQLException e) {
            fail("An exception was thrown during the execution: " + e.getMessage());
        }
    }

    @Test
    void testDoWork201Response() {
        try {
            this.setUpLoginReturnTrue();
            Response response = this.userController.doWorkIntern();
            assertThat(response.getStatusCode()).isEqualTo(201);
            assertThat(response.getStatus()).isEqualTo(SC_201);
            assertThat(response.getContent()).contains(USERNAME_1);
            assertThat(response.getContentType()).isEqualTo(CONTENT_TYPE_TEXT_PLAIN.toString());
        } catch (SQLException e) {
            fail("An exception was thrown during the execution: " + e.getMessage());
        }
    }

    @Test
    void testDoWork400Response() {
        try {
            this.setUpLoginReturnFalse();
            Response response = this.userController.doWorkIntern();
            assertThat(response.getStatusCode()).isEqualTo(400);
            assertThat(response.getStatus()).isEqualTo(SC_400);
            assertThat(response.getContent()).contains(USERNAME_2);
            assertThat(response.getContentType()).isEqualTo(CONTENT_TYPE_TEXT_PLAIN.toString());
        } catch (SQLException e) {
            fail("An exception was thrown during the execution: " + e.getMessage());
        }
    }

    @Test
    void testDoWork400WhenEmptyResponse() {
        try {
            UserModel userModel = UserModel.builder().username("").password("").build();
            when(this.userRequest.getModel()).thenReturn(userModel);
            Response response = this.userController.doWorkIntern();
            assertThat(response.getStatusCode()).isEqualTo(400);
            assertThat(response.getStatus()).isEqualTo(SC_400);
            assertThat(response.getContent()).contains(USERNAME_PASSWORD_ERROR_MESSAGE);
            assertThat(response.getContentType()).isEqualTo(CONTENT_TYPE_TEXT_PLAIN.toString());
        } catch (SQLException e) {
            fail("An exception was thrown during the execution: " + e.getMessage());
        }
    }
}