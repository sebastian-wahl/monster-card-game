package card.game.server;

import card.game.controller.BattleController;
import card.game.controller.ControllerBase;
import card.game.http.URL.PathEnum;
import card.game.http.request.Request;
import card.game.http.response.ConcreteResponse;
import card.game.http.response.Response;

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
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Response pickController(Request request) {
        PathEnum path = PathEnum.BATTLE;
        ControllerBase controller = switch (path) {
            case BATTLE -> new BattleController(battleQueue);
            default -> throw new IllegalArgumentException("Invalid path: " + path);
        };
        return new ConcreteResponse();
    }
}
