package game.controller;

import game.http.request.Request;
import game.http.response.ConcreteResponse;
import game.http.response.Response;
import game.objects.User;
import game.objects.exceptions.repositories.UserOrPasswordEmptyException;
import game.repository.UserRepository;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

import static game.http.HttpReady.PASSWORD_KEY;
import static game.http.HttpReady.USERNAME_KEY;
import static game.http.enums.StatusCodeEnum.*;

public class LoginUserController extends ControllerBase {

    private final UserRepository userRepository;

    public LoginUserController(BlockingQueue<String> queue, Request request) {
        this(queue, request, new UserRepository());
    }

    public LoginUserController(BlockingQueue<String> userQueue, Request request, UserRepository userRepository) {
        super(userQueue, request);
        this.userRepository = userRepository;
    }

    @Override
    public Response doWork() {
        Response response = new ConcreteResponse();
        try {
            if (this.doLogin()) {
                System.out.println("Login successful");
                response.setStatus(SC_200);
                // username is present, since this is checked in the login method
                String securityToken = generateSecurityToken(this.userRequest.getContent().get(USERNAME_KEY));
                System.out.println("Security token: " + securityToken);
                Timestamp timestamp = Timestamp.from(Instant.now());
                persistsTokenAndTimestamp(securityToken, timestamp);
                Timestamp validUntil = Timestamp.valueOf(timestamp.toLocalDateTime().plusDays(1));
                response.setContent("{'Authorization': '" + securityToken + "', 'ValidUntil': '" + validUntil.toString().substring(0, validUntil.toString().indexOf(".")) + "'}");
            } else {
                System.out.println("Login failed");
                response.setStatus(SC_401);
                response.setContent("Login failed. Please check username and password.");
            }
        } catch (UserOrPasswordEmptyException ex) {
            response.setContent(ex.getMessage());
            response.setStatus(SC_400);
        }
        return response;
    }

    private boolean doLogin() {
        String username = this.userRequest.getContent().get(USERNAME_KEY);
        String password = this.userRequest.getContent().get(PASSWORD_KEY);
        if (username == null || password == null) {
            throw new UserOrPasswordEmptyException("Username and Password must not be empty!");
        }
        return this.userRepository.login(username, password);
    }

    private String generateSecurityToken(String username) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijk"
                + "lmnopqrstuvwxyz"
                + "!@§$%&/()=?{[]}*+~#-_.,;:";
        try {
            Random rnd = SecureRandom.getInstanceStrong();
            StringBuilder sb = new StringBuilder(20);
            for (int i = 0; i < 20; i++) {
                sb.append(chars.charAt(rnd.nextInt(chars.length())));
            }
            return username + "-" + sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void persistsTokenAndTimestamp(String securityToken, Timestamp securityTokenTimestamp) {
        String username = this.userRequest.getContent().get(USERNAME_KEY);
        String password = this.userRequest.getContent().get(PASSWORD_KEY);
        User tempUser = new User();
        tempUser.setPassword(password);
        tempUser.setUsername(username);
        tempUser.setSecurityToken(securityToken);
        tempUser.setSecurityTokenTimestamp(securityTokenTimestamp);
        Optional<User> res = this.userRepository.update(tempUser);
        if (res.isPresent()) {
            System.out.println("Update successful");
        } else {
            System.out.println("Error occurred when updating user");
        }
    }

}