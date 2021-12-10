package game.server;

import game.controller.AddUserController;
import game.controller.BattleController;
import game.controller.ControllerBase;
import game.controller.LoginUserController;
import game.http.request.ConcreteRequest;
import game.http.request.Request;
import game.http.response.Response;
import game.http.url.PathEnum;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class ClientGameRunner implements Runnable {
    Socket clientSocket;
    BlockingQueue<String> battleQueue;

    public ClientGameRunner(Socket clientSocket, BlockingQueue<String> battleQueue) {
        this.battleQueue = battleQueue;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            Request request = new ConcreteRequest(clientSocket.getInputStream());
            Response response = pickController(request);
            response.send(clientSocket.getOutputStream());
            clientSocket.close();
        } catch (IOException e) {
        }

    }

    public Response pickController(Request request) {
        PathEnum path = request.getUrl().getUrlPath();
        ControllerBase controller = switch (path) {
            case USERS -> new AddUserController(battleQueue, request);
            case BATTLES -> new BattleController(battleQueue, request);
            case SESSIONS -> new LoginUserController(battleQueue, request);
            case PACKAGES -> new LoginUserController(battleQueue, request);
            case CARDS -> new LoginUserController(battleQueue, request);
            case DECK -> new LoginUserController(battleQueue, request);
            case STATS -> new LoginUserController(battleQueue, request);
            case SCORE -> new LoginUserController(battleQueue, request);
            case TRADINGS -> new LoginUserController(battleQueue, request);
            case NOMATCH -> throw new IllegalArgumentException("Invalid path: " + path);
        };
        return controller.doWork();
    }
}
