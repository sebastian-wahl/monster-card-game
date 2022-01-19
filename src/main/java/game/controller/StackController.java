package game.controller;

import game.helper.RepositoryHelper;
import game.http.enums.StatusCodeEnum;
import game.http.request.Request;
import game.http.response.ConcreteResponse;
import game.http.response.Response;
import game.objects.Deck;
import game.objects.Stack;
import game.objects.User;

import java.sql.SQLException;
import java.util.Optional;

public class StackController extends ControllerBase {

    private static final String STACK_WITHOUT_DECK_PARAMETER = "not_in_deck";
    private static final String STACK_WITHOUT_DECK_PARAMETER_VALUE = "true";

    private static final String STACK_WITHOUT_IN_TRADE_CARDS_PARAMETER = "not_in_trade";
    private static final String STACK_WITHOUT_IN_TRADE_CARDS_PARAMETER_VALUE = "true";

    public StackController(Request request, RepositoryHelper repositoryHelper) {
        super(request, repositoryHelper);
    }

    @Override
    public Response doWorkIntern() throws SQLException {
        Response response = new ConcreteResponse();

        Optional<User> isValid = this.repositoryHelper.getUserRepository().checkTokenAndGetUser(this.userRequest.getAuthorizationToken());
        if (isValid.isPresent()) {
            // set content
            Optional<User> user = this.repositoryHelper.getStackRepository().getUserStack(isValid.get());
            if (user.isPresent()) {
                Stack adaptedStack = user.get().getStack();
                response.setStatus(StatusCodeEnum.SC_200);
                if (this.hasStackWithoutDeckUrlParameter()) {
                    Deck deck = this.repositoryHelper.getDeckRepository().getDeckByUsername(user.get().getUsername());
                    adaptedStack.removeCardsFromStack(deck.getCards());
                }
                if (this.hasStackWithoutTradeCardsUrlParameter()) {
                    adaptedStack.removeAllCardsThatAreInvolvedInTrade();
                }
                response.setContent(user.get().getStack().toString());
            } else {
                response.setStatus(StatusCodeEnum.SC_500);
            }
        } else {
            response.setStatus(StatusCodeEnum.SC_401);
            response.setContent(WRONG_SECURITY_TOKEN_ERROR_MESSAGE);
        }
        return response;
    }

    private boolean hasStackWithoutDeckUrlParameter() {
        return !this.userRequest.getUrl().getUrlParameters().isEmpty() &&
                this.userRequest.getUrl().getUrlParameters().get(STACK_WITHOUT_DECK_PARAMETER) != null &&
                this.userRequest.getUrl().getUrlParameters().get(STACK_WITHOUT_DECK_PARAMETER).equals(STACK_WITHOUT_DECK_PARAMETER_VALUE);
    }

    private boolean hasStackWithoutTradeCardsUrlParameter() {
        return !this.userRequest.getUrl().getUrlParameters().isEmpty() &&
                this.userRequest.getUrl().getUrlParameters().get(STACK_WITHOUT_IN_TRADE_CARDS_PARAMETER) != null &&
                this.userRequest.getUrl().getUrlParameters().get(STACK_WITHOUT_IN_TRADE_CARDS_PARAMETER).equals(STACK_WITHOUT_IN_TRADE_CARDS_PARAMETER_VALUE);
    }
}
