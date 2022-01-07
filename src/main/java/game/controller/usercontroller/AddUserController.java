package game.controller.usercontroller;

import game.controller.ControllerBase;
import game.helper.RepositoryHelper;
import game.http.enums.StatusCodeEnum;
import game.http.models.UserModel;
import game.http.request.Request;
import game.http.response.ConcreteResponse;
import game.http.response.Response;
import game.objects.exceptions.repositories.UserOrPasswordEmptyException;

import java.sql.SQLException;

import static game.http.enums.StatusCodeEnum.SC_201;
import static game.http.enums.StatusCodeEnum.SC_400;

public class AddUserController extends ControllerBase {

    public AddUserController(Request request, RepositoryHelper repositoryHelper) {
        super(request, repositoryHelper);
    }

    private static final String USERNAME_PASSWORD_ERROR_MESSAGE = "Username and Password must be longer than 4 characters!";

    @Override
    public Response doWorkIntern() throws SQLException {
        Response response = new ConcreteResponse();
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
        return response;
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
