package game.controller;

import game.helper.RepositoryHelper;
import game.http.HttpMethod;
import game.http.enums.StatusCodeEnum;
import game.http.models.TradeModel;
import game.http.request.Request;
import game.http.response.ConcreteResponse;
import game.http.response.Response;
import game.objects.CardBase;
import game.objects.Trade;
import game.objects.User;
import game.objects.enums.CardsEnum;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TradeController extends ControllerBase {

    private static final String OWN_TRADE_PARAM = "own_deals";
    private static final String OWN_TRADE_PARAM_VALUE = "true";

    private static final String OTHER_TRADE_PARAM = "other_deals";
    private static final String OTHER_TRADE_PARAM_VALUE = "true";

    private static final String SPECIFIC_USER_TRADE_PARAM = "user_deal";

    public TradeController(Request request, RepositoryHelper repositoryHelper) {
        super(request, repositoryHelper);
    }

    @Override
    public Response doWorkIntern() throws SQLException {
        Response response = new ConcreteResponse();

        Optional<User> userOpt = this.repositoryHelper.getUserRepository().checkTokenAndGetUser(userRequest.getAuthorizationToken());
        if (userOpt.isPresent()) {
            if (userRequest.getMethod() == HttpMethod.GET) {
                List<Trade> trades;
                if (this.hasOwnTradingDealsParam()) {
                    // get own trading deals
                    trades = this.repositoryHelper.getTradeRepository().getAllTradesForUser(userOpt.get());
                } else if (this.hasOtherTradingDealsParam()) {
                    // get deals from all others
                    trades = this.repositoryHelper.getTradeRepository().getOtherTradesExceptUserTrades(userOpt.get());
                } else if (this.hasSpecificUserTradingDealsParam()) {
                    // get deals from specific user
                    String usernameForDeals = this.userRequest.getUrl().getUrlParameters().get(SPECIFIC_USER_TRADE_PARAM);
                    trades = this.repositoryHelper.getTradeRepository().getAllTradesForUser(User.builder().username(usernameForDeals).build());
                } else {
                    // get all trading deals
                    trades = this.repositoryHelper.getTradeRepository().getAllTrades();
                }
                response.setStatus(StatusCodeEnum.SC_200);
                setResponseContent(response, trades);
            } else if (userRequest.getMethod() == HttpMethod.POST) {
                // add trade
                addTrade(response, userOpt.get());
            } else if (userRequest.getMethod() == HttpMethod.DELETE) {
                // delete trading deal
                this.userRequest.getUrl().getUrlSegments();
                if (this.userRequest.getUrl().getUrlSegments().size() == 2) {
                    String tradeId = this.userRequest.getUrl().getUrlSegments().get(1);
                    this.repositoryHelper.getTradeRepository().removeTrade(Trade.builder().id(UUID.fromString(tradeId)).tradeUser(userOpt.get()).build());
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

    private void addTrade(Response response, User user) throws SQLException {
        if (this.userRequest.getModel() instanceof TradeModel) {
            TradeModel tradeModel = (TradeModel) this.userRequest.getModel();
            // check if card
            CardBase tradeCard = this.repositoryHelper.getCardRepository().markCardForTrading(tradeModel.getCardId(), true);
            if (!this.repositoryHelper.getDeckRepository().isCardInUserDeck(user.getUsername(), tradeCard.getId().toString())) {
                CardsEnum desiredCard;
                try {
                    desiredCard = CardsEnum.valueOf(tradeModel.getDesiredCardName());
                } catch (IllegalArgumentException ex) {
                    desiredCard = null;
                }
                Trade toCreate = Trade.builder()
                        .id(UUID.randomUUID())
                        .tradeUser(user)
                        .tradFinished(false)
                        .tradeCard(tradeCard)
                        .desiredCoins(tradeModel.getDesiredCoins())
                        .desiredCard(desiredCard)
                        .build();

                if (this.repositoryHelper.getTradeRepository().addTrade(toCreate)) {
                    response.setStatus(StatusCodeEnum.SC_200);
                    response.setContent(toCreate.toString());
                }
            }
        }
    }

    private void setResponseContent(Response response, List<Trade> trades) {
        if (trades.isEmpty()) {
            response.setContent("{\"Trades\": \"No open trades available.\"}");
        } else {
            StringBuilder out = new StringBuilder();
            out.append("{\"Trades\": [");
            int i = 0;
            for (Trade trade : trades) {
                out.append(trade.toString());
                if (i < trades.size() - 1) {
                    out.append(", ");
                }
                i++;
            }
            out.append("]}");
            response.setContent(out.toString());
        }
    }

    private boolean hasOwnTradingDealsParam() {
        return !this.userRequest.getUrl().getUrlParameters().isEmpty() &&
                this.userRequest.getUrl().getUrlParameters().get(OWN_TRADE_PARAM) != null &&
                this.userRequest.getUrl().getUrlParameters().get(OWN_TRADE_PARAM).equals(OWN_TRADE_PARAM_VALUE);
    }

    private boolean hasOtherTradingDealsParam() {
        return !this.userRequest.getUrl().getUrlParameters().isEmpty() &&
                this.userRequest.getUrl().getUrlParameters().get(OTHER_TRADE_PARAM) != null &&
                this.userRequest.getUrl().getUrlParameters().get(OTHER_TRADE_PARAM).equals(OTHER_TRADE_PARAM_VALUE);
    }

    private boolean hasSpecificUserTradingDealsParam() {
        return !this.userRequest.getUrl().getUrlParameters().isEmpty() &&
                this.userRequest.getUrl().getUrlParameters().get(SPECIFIC_USER_TRADE_PARAM) != null;
    }
}
