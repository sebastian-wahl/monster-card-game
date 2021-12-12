package game.controller.battlecontroller;

import game.controller.ControllerBase;
import game.http.HttpReady;
import game.http.enums.StatusCodeEnum;
import game.http.request.Request;
import game.http.response.ConcreteResponse;
import game.http.response.Response;
import game.repository.RepositoryHelper;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class BattleController extends ControllerBase {
    private BattleQueueHandler battleQueueHandler;

    public BattleController(Request request, RepositoryHelper repositoryHelper, BattleQueueHandler battleQueueHandler) {
        super(request, repositoryHelper);
        this.battleQueueHandler = battleQueueHandler;
    }

    @Override
    public Response doWork() {
        String token = userRequest.getHeaders().get(HttpReady.AUTHORIZATION_KEY);
        String username = token.substring(0, token.indexOf("-"));
        if (this.repositoryHelper.getUserRepository().loginToken(username, token)) {
            CompletableFuture<Response> responseFuture = battleQueueHandler.addUserToBattleQueueAndHandleBattle(username);
            try {
                return responseFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                Response response = new ConcreteResponse();
                response.setStatus(StatusCodeEnum.SC_500);
                return response;
            }

        } else {
            Response response = new ConcreteResponse();
            response.setContent("The Authorization Token is either expired or wrong.");
            response.setStatus(StatusCodeEnum.SC_400);
            return response;
        }
    }
}
