package game.controller.usercontroller;

import game.controller.ControllerBase;
import game.http.request.Request;
import game.http.response.ConcreteResponse;
import game.http.response.Response;
import game.objects.exceptions.repositories.UserOrPasswordEmptyException;
import game.repository.RepositoryHelper;

import java.util.Objects;

import static game.http.enums.StatusCodeEnum.SC_201;
import static game.http.enums.StatusCodeEnum.SC_400;
import static game.http.request.Request.*;

public class AddUserController extends ControllerBase {

    public AddUserController(Request request, RepositoryHelper repositoryHelper) {
        super(request, repositoryHelper);
    }

    @Override
    public Response doWork() {
        Response response = new ConcreteResponse();
        try {
            if (this.addUser()) {
                System.out.println("User added successfully");
                // username is present since addUser() returned true
                response.setContent("User with Username \"" + userRequest.getContent().get(USERNAME_KEY) + "\" was added.");
                response.setStatus(SC_201);
            } else {
                // no exception so username should be present
                response.setContent("User \"" + userRequest.getContent().get(USERNAME_KEY) + "\" already exists!");
                response.setStatus(SC_400);
                System.out.println("User not added");
            }
        } catch (UserOrPasswordEmptyException ex) {
            response.setStatus(SC_400);
            response.setContent(ex.getMessage());
        }
        return response;
    }

    private boolean addUser() {
        // first two are mandatory,
        String username = this.userRequest.getContent().get(USERNAME_KEY);
        String password = this.userRequest.getContent().get(PASSWORD_KEY);
        if (username == null || password == null) {
            throw new UserOrPasswordEmptyException("Username and Password must not be empty!");
        }
        String displayName = Objects.requireNonNullElse(this.userRequest.getContent().get(DISPLAYNAME_KEY), "");
        String bio = Objects.requireNonNullElse(this.userRequest.getContent().get(BIO_KEY), "");

        return this.repositoryHelper.getUserRepository().addUserToDb(username, displayName, bio, password);
    }
}
