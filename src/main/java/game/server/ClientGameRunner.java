package game.server;

import game.controller.*;
import game.controller.battlecontroller.BattleController;
import game.controller.battlecontroller.BattleQueueHandler;
import game.controller.usercontroller.AddUserController;
import game.controller.usercontroller.LoginUserController;
import game.controller.usercontroller.UserStatController;
import game.helper.RepositoryHelper;
import game.http.request.ConcreteRequest;
import game.http.request.Request;
import game.http.response.Response;
import game.http.url.PathEnum;

import java.io.IOException;
import java.net.Socket;

public class ClientGameRunner implements Runnable {
    private Socket clientSocket;
    private BattleQueueHandler battleQueueHandler;
    private RepositoryHelper repositoryHelper;

    public ClientGameRunner(Socket clientSocket, BattleQueueHandler battleQueueHandler, RepositoryHelper repositoryHelper) {
        this.battleQueueHandler = battleQueueHandler;
        this.repositoryHelper = repositoryHelper;
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
            case USERS -> new AddUserController(request, repositoryHelper);
            case BATTLES -> new BattleController(request, repositoryHelper, battleQueueHandler);
            case SESSIONS -> new LoginUserController(request, repositoryHelper);
            case PACKAGES -> new PackageController(request, repositoryHelper);
            //case TRANSACTIONS -> new LoginUserController(request, repositoryHelper);
            case CARDS -> new StackController(request, repositoryHelper);
            case DECK -> new DeckController(request, repositoryHelper);
            case STATS -> new UserStatController(request, repositoryHelper);
            case SCORE -> new ScoreboardController(request, repositoryHelper);
            case TRADINGS -> new LoginUserController(request, repositoryHelper);
            case NOMATCH -> throw new IllegalArgumentException("Invalid path: " + path);
        };
        return controller.doWork();
    }
}
