package game.controller;

import game.helper.RepositoryHelper;
import game.http.HttpMethod;
import game.http.enums.StatusCodeEnum;
import game.http.models.DeckModel;
import game.http.request.Request;
import game.http.response.ConcreteResponse;
import game.http.response.Response;
import game.objects.Deck;
import game.objects.User;

import java.sql.SQLException;
import java.util.Optional;

public class DeckController extends ControllerBase {

    private static final String TOO_MANY_CARDS_FOR_DECK_ERROR_MESSAGE = "Please only choose 4 cards for your deck.";

    public DeckController(Request request, RepositoryHelper repositoryHelper) {
        super(request, repositoryHelper);
    }

    @Override
    public Response doWorkIntern() throws SQLException {
        Response response = new ConcreteResponse();
        Optional<User> userOpt = this.repositoryHelper.getUserRepository().checkTokenAndGetUser(userRequest.getAuthorizationToken());
        if (userOpt.isPresent()) {
            if (userRequest.getMethod() == HttpMethod.GET) {
                Deck deck = this.repositoryHelper.getDeckRepository().getDeckByUsername(userOpt.get().getUsername());
                response.setContent(deck.toString());
                response.setStatus(StatusCodeEnum.SC_200);
            } else if (userRequest.getMethod() == HttpMethod.PUT) {
                if (userRequest.getModel() instanceof DeckModel) {
                    userOpt = this.repositoryHelper.getDeckRepository().setUserDeck(userOpt.get(), (DeckModel) userRequest.getModel());
                    if (userOpt.isPresent()) {
                        response.setStatus(StatusCodeEnum.SC_200);
                        response.setContent(userOpt.get().getDeck().toString());
                    } else {
                        response.setStatus(StatusCodeEnum.SC_400);
                        response.setContent(TOO_MANY_CARDS_FOR_DECK_ERROR_MESSAGE);
                    }
                } else {
                    response.setStatus(StatusCodeEnum.SC_400);
                    response.setContent(WRONG_BODY_MESSAGE);
                }
            } else {
                response.setStatus(StatusCodeEnum.SC_400);
            }
        } else {
            response.setStatus(StatusCodeEnum.SC_401);
            response.setContent(WRONG_SECURITY_TOKEN_ERROR_MESSAGE);
        }
        return response;
    }
}
