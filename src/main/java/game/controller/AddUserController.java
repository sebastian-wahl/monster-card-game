package game.controller;

import game.http.request.Request;
import game.http.response.ConcreteResponse;
import game.http.response.Response;
import game.repository.UserRepository;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;

import static game.http.request.Request.*;

public class AddUserController extends ControllerBase {


    private UserRepository userRepository;

    public AddUserController(BlockingQueue<String> userQueue, Request request) {
        this(userQueue, request, new UserRepository());
    }

    public AddUserController(BlockingQueue<String> userQueue, Request request, UserRepository userRepository) {
        super(userQueue, request);
        this.userRepository = userRepository;
    }

    @Override
    public Response doWork() {
        Response response = new ConcreteResponse();
        if (this.addUser()) {

        }
        return response;
    }

    public boolean addUser() {
        // first two are mandatory,
        String username = this.userRequest.getContent().get(USERNAME_KEY);
        String password = this.userRequest.getContent().get(PASSWORD_KEY);
        if (username == null || password == null) {
            // ToDo custom exception
            throw new IllegalArgumentException();
        }
        String displayName = Objects.requireNonNullElse(this.userRequest.getContent().get(DISPLAYNAME_KEY), "");
        String bio = Objects.requireNonNullElse(this.userRequest.getContent().get(BIO_KEY), "");

        return this.userRepository.addUserToDb(username, displayName, bio, password);
    }
}
