package game.controller;

import game.helper.RepositoryHelper;
import game.http.HttpMethod;
import game.http.enums.StatusCodeEnum;
import game.http.models.DeckModel;
import game.http.request.Request;
import game.http.response.ConcreteResponse;
import game.http.response.Response;
import game.objects.CardBase;
import game.objects.Deck;
import game.objects.User;

import java.sql.SQLException;
import java.util.Optional;

public class DeckController extends ControllerBase {

    private static final String TOO_MANY_CARDS_FOR_DECK_ERROR_MESSAGE = "Please only choose 4 cards for your deck.";

    private static final String FORMAT_PARAMETER = "format";
    private static final String FORMAT_PARAMETER_VALUE = "plain";
    private static final String CARD_NOT_IN_STACK_MESSAGE = "It looks like some Cards are involved in a trade or not present in your Card Stack.";

    public DeckController(Request request, RepositoryHelper repositoryHelper) {
        super(request, repositoryHelper);
    }

    @Override
    public Response doWorkIntern() throws SQLException {
        Response response = new ConcreteResponse();
        Optional<User> userOpt = this.repositoryHelper.getUserRepository().checkTokenAndGetUser(userRequest.getAuthorizationToken());
        if (userOpt.isPresent()) {
            if (userRequest.getMethod() == HttpMethod.GET) {
                getDeck(response, userOpt);
            } else if (userRequest.getMethod() == HttpMethod.PUT) {
                setDeck(response, userOpt);
            } else {
                response.setStatus(StatusCodeEnum.SC_400);
            }
        } else {
            response.setStatus(StatusCodeEnum.SC_401);
            response.setContent(WRONG_SECURITY_TOKEN_ERROR_MESSAGE);
        }
        return response;
    }

    private void setDeck(Response response, Optional<User> userOpt) throws SQLException {
        if (userRequest.getModel() instanceof DeckModel) {
            DeckModel deckModel = (DeckModel) userRequest.getModel();
            // check if all cards are in user stack and in no trade involved
            boolean allNotInTrade = this.repositoryHelper.getCardRepository().getCardsById(deckModel.getDeckIds())
                    .stream().map(CardBase::isInTradeInvolved).allMatch(aBoolean -> aBoolean == false);
            if (allNotInTrade && this.repositoryHelper.getStackRepository().areCardsWithIdOwnedByUser(userOpt.get(), deckModel.getDeckIds())) {
                userOpt = this.repositoryHelper.getDeckRepository().setUserDeck(userOpt.get(), deckModel);
                if (userOpt.isPresent()) {
                    response.setStatus(StatusCodeEnum.SC_200);
                    response.setContent(userOpt.get().getDeck().toString());
                } else {
                    response.setStatus(StatusCodeEnum.SC_400);
                    response.setContent(TOO_MANY_CARDS_FOR_DECK_ERROR_MESSAGE);
                }
            } else {
                response.setStatus(StatusCodeEnum.SC_400);
                response.setContent(CARD_NOT_IN_STACK_MESSAGE);
            }
        } else {
            response.setStatus(StatusCodeEnum.SC_400);
            response.setContent(WRONG_BODY_MESSAGE);
        }
    }

    private void getDeck(Response response, Optional<User> userOpt) throws SQLException {
        Deck deck = this.repositoryHelper.getDeckRepository().getDeckByUsername(userOpt.get().getUsername());
        if (this.hasFormatPlaneUrlParameter()) {
            response.setContent(deck.toPlainString());
        } else {
            response.setContent(deck.toString());
        }
        response.setStatus(StatusCodeEnum.SC_200);
    }

    private boolean hasFormatPlaneUrlParameter() {
        return !this.userRequest.getUrl().getUrlParameters().isEmpty() &&
                this.userRequest.getUrl().getUrlParameters().get(FORMAT_PARAMETER) != null &&
                this.userRequest.getUrl().getUrlParameters().get(FORMAT_PARAMETER).equals(FORMAT_PARAMETER_VALUE);
    }
}
