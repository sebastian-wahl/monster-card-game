package game.controller;

import game.http.HttpMethod;
import game.http.enums.StatusCodeEnum;
import game.http.request.Request;
import game.http.response.ConcreteResponse;
import game.http.response.Response;
import game.objects.Deck;
import game.objects.User;
import game.repository.RepositoryHelper;

import java.util.Optional;

public class DeckController extends ControllerBase {

    public DeckController(Request request, RepositoryHelper repositoryHelper) {
        super(request, repositoryHelper);
    }

    @Override
    public Response doWork() {
        Response response = new ConcreteResponse();
        Optional<User> userOpt = this.repositoryHelper.getUserRepository().checkTokenAndGetUser(userRequest.getAuthorizationToken());
        if (userOpt.isPresent()) {
            if (userRequest.getMethod() == HttpMethod.GET) {
                Optional<Deck> deckOpt = this.repositoryHelper.getDeckRepository().getDeckByUsername(userOpt.get().getUsername());
                if (deckOpt.isPresent()) {
                    Deck deck = deckOpt.get();
                    response.setContent(deck.toString());
                    response.setStatus(StatusCodeEnum.SC_200);
                } else {
                    response.setStatus(StatusCodeEnum.SC_500);
                }
            } else if (userRequest.getMethod() == HttpMethod.POST) {

            } else {
                response.setStatus(StatusCodeEnum.SC_400);
            }
        } else {
            response.setStatus(StatusCodeEnum.SC_401);
        }
        return response;
    }
}
