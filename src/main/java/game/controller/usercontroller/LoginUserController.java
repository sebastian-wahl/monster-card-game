package game.controller.usercontroller;

import game.controller.ControllerBase;
import game.http.models.UserModel;
import game.http.request.Request;
import game.http.response.ConcreteResponse;
import game.http.response.Response;
import game.objects.exceptions.repositories.UserOrPasswordEmptyException;
import game.repository.RepositoryHelper;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import static game.http.enums.StatusCodeEnum.*;

public class LoginUserController extends ControllerBase {

    public LoginUserController(Request request, RepositoryHelper repositoryHelper) {
        super(request, repositoryHelper);
    }

    @Override
    public Response doWork() {
        Response response = new ConcreteResponse();
        try {
            if (this.userRequest.getModel() instanceof UserModel) {
                UserModel userModel = (UserModel) this.userRequest.getModel();
                if (this.doLogin(userModel)) {
                    // username is present, since this is checked in the login method
                    Optional<String> securityTokenOpt = this.repositoryHelper.getUserRepository().generateSecurityToken(userModel.getUsername());
                    if (securityTokenOpt.isPresent()) {
                        System.out.println("Login successful");
                        response.setStatus(SC_200);

                        String securityToken = securityTokenOpt.get();
                        System.out.println("Security token: " + securityToken);
                        // validity period
                        Timestamp timestamp = Timestamp.from(Instant.now());
                        Timestamp validUntil = Timestamp.valueOf(timestamp.toLocalDateTime().plusMinutes(60));
                        response.setContent("{\"Authorization\": \"" + securityToken + "\", \"ValidUntil\": \"" + validUntil.toString().substring(0, validUntil.toString().indexOf(".")) + "\"}");
                    } else {
                        response.setStatus(SC_500);
                    }
                } else {
                    System.out.println("Login failed");
                    response.setStatus(SC_401);
                    response.setContent("Login failed. Please check username and password.");
                }
            } else {
                response.setStatus(SC_500);
            }
        } catch (UserOrPasswordEmptyException ex) {
            response.setContent(ex.getMessage());
            response.setStatus(SC_400);
        }
        return response;
    }

    private boolean doLogin(UserModel userModel) {
        if (throwUsernameAndPasswordException(userModel)) {
            throw new UserOrPasswordEmptyException("Username and Password must be longer than 4 characters!");
        }
        return this.repositoryHelper.getUserRepository().login(userModel);
    }

    private boolean throwUsernameAndPasswordException(UserModel userModel) {
        return userModel.getUsername() == null || userModel.getUsername().length() < 4 ||
                userModel.getPassword() == null || userModel.getPassword().length() < 4;
    }
}