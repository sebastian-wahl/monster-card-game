package game.controller.usercontroller;

import game.controller.ControllerBase;
import game.helper.RepositoryHelper;
import game.http.HttpMethod;
import game.http.enums.StatusCodeEnum;
import game.http.models.UserModel;
import game.http.request.Request;
import game.http.response.ConcreteResponse;
import game.http.response.Response;
import game.objects.User;
import game.objects.exceptions.repositories.UserOrPasswordEmptyException;

import java.sql.SQLException;
import java.util.Optional;

import static game.http.enums.StatusCodeEnum.*;

public class AddAndEditUserController extends ControllerBase {

    private static final String OWN_PROFILE_ERROR_MESSAGE = "You can only edit your own profile.";

    public AddAndEditUserController(Request request, RepositoryHelper repositoryHelper) {
        super(request, repositoryHelper);
    }

    private static final String USERNAME_PASSWORD_ERROR_MESSAGE = "Username and Password must be longer than 4 characters!";

    @Override
    public Response doWorkIntern() throws SQLException {
        Response response = new ConcreteResponse();
        if (this.userRequest.getUrl().getUrlSegments().size() == 1) {
            addUser(response);
        }
        if (this.userRequest.getUrl().getUrlSegments().size() == 2) {
            Optional<User> userOpt = this.repositoryHelper.getUserRepository().checkTokenAndGetUser(this.userRequest.getAuthorizationToken());
            if (userOpt.isPresent()) {
                editOrGetUser(response, userOpt.get());
            } else {
                response.setStatus(StatusCodeEnum.SC_401);
                response.setContent(WRONG_SECURITY_TOKEN_ERROR_MESSAGE);
            }
        }
        return response;
    }

    private void editOrGetUser(Response response, User user) throws SQLException {
        String username = this.userRequest.getUrl().getUrlSegments().get(1);
        if (this.userRequest.getMethod() == HttpMethod.GET) {
            // return user
            Optional<User> userOpt = this.repositoryHelper.getUserRepository().getUser(username);
            if (userOpt.isPresent()) {
                response.setStatus(SC_200);
                response.setContent(userOpt.get().toString());
            }
        } else if (this.userRequest.getMethod() == HttpMethod.PUT && username.equals(user.getUsername())) {
            if (this.userRequest.getModel() instanceof UserModel) {
                UserModel newUserModel = (UserModel) this.userRequest.getModel();
                User newUser = User.builder().username(username)
                        .displayName(newUserModel.getDisplayName())
                        .bio(newUserModel.getBio())
                        .image(newUserModel.getImage())
                        .build();
                Optional<User> userOpt = this.repositoryHelper.getUserRepository().update(newUser);
                if (userOpt.isPresent()) {
                    response.setStatus(SC_200);
                    response.setContent(userOpt.get().toString());
                }
            }
        } else {
            response.setContent(OWN_PROFILE_ERROR_MESSAGE);
        }

    }


    private void addUser(Response response) throws SQLException {
        try {
            if (this.userRequest.getModel() instanceof UserModel) {
                UserModel userModel = (UserModel) this.userRequest.getModel();
                if (this.addUser(userModel)) {
                    System.out.println("User added successfully");
                    // username is present since addUser() returned true
                    response.setContent("User with Username \"" + userModel.getUsername() + "\" was added.");
                    response.setStatus(SC_201);
                } else {
                    // no exception so username should be present
                    response.setContent("User \"" + userModel.getUsername() + "\" already exists!");
                    response.setStatus(SC_400);
                    System.out.println("User not added");
                }
            } else {
                response.setStatus(StatusCodeEnum.SC_400);
                response.setContent(WRONG_BODY_MESSAGE);
            }
        } catch (UserOrPasswordEmptyException ex) {
            response.setStatus(SC_400);
            response.setContent(ex.getMessage());
        }
    }

    private boolean addUser(UserModel userModel) throws SQLException {
        // first two are mandatory,
        if (this.userRequest.getModel() instanceof UserModel) {
            if (this.throwUsernameAndPasswordException(userModel)) {
                throw new UserOrPasswordEmptyException(USERNAME_PASSWORD_ERROR_MESSAGE);
            }
            return this.repositoryHelper.getUserRepository().addUserToDb(userModel);
        }
        return false;

    }

    private boolean throwUsernameAndPasswordException(UserModel userModel) {
        return userModel.getUsername() == null || userModel.getUsername().length() < 4 ||
                userModel.getPassword() == null || userModel.getPassword().length() < 4;
    }
}
