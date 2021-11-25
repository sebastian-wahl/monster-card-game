package game.server;

import game.controller.BattleController;
import game.controller.ControllerBase;
import game.http.request.ConcreteRequest;
import game.http.request.Request;
import game.http.response.ConcreteResponse;
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
            e.printStackTrace();
        }

    }

    public Response pickController(Request request) {
        PathEnum path = PathEnum.BATTLE;
        ControllerBase controller = switch (path) {
            case BATTLE -> new BattleController(battleQueue, null);
            default -> throw new IllegalArgumentException("Invalid path: " + path);
        };
        return new ConcreteResponse();
    }
}
