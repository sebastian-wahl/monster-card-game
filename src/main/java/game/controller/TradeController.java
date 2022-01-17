package game.controller;

import game.helper.RepositoryHelper;
import game.http.HttpMethod;
import game.http.enums.StatusCodeEnum;
import game.http.models.AddTradeModel;
import game.http.models.FinishTradeModel;
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

    private static final String OPEN_ONLY_TRADE_PARAM = "open_only";
    private static final String OPEN_ONLY_TRADE_PARAM_VALUE = "true";

    private static final String SPECIFIC_USER_TRADE_PARAM = "user_deal";
    private static final String SELF_TRADE_ERROR_MESSAGE = "It is not possible to trade with yourself!";
    private static final String NO_TRADE_FOUND_ERROR_MESSAGE = "No trade found under the given id:";
    private static final String CARD_NOT_SUITABLE_ERROR_MESSAGE = "The selected card is not suited for this trade. Either the card is involved in another trade or is selected as deck card or does not match the desired card from this trade.";
    private static final String NOT_ENOUGH_COINS_ERROR_MESSAGE = "You do not have enough coins for this trade. Required: ";
    private static final String CARD_ALREADY_IN_USE_MESSAGE = "The card that was selected for that trade is already in use for another trade.";
    private static final String TRADE_REMOVED_SUCCESSFULLY = "Trade removed successfully";
    private static final String ONLY_OWN_TRADES_MESSAGE = "You can only remove your own trades.";

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
                boolean openOnly = this.hasOpenOnlyTradingDealsParam();
                if (this.hasOwnTradingDealsParam()) {
                    // get own trading deals
                    trades = this.repositoryHelper.getTradeRepository().getAllTradesForUser(userOpt.get(), openOnly);
                } else if (this.hasOtherTradingDealsParam()) {
                    // get deals from all others
                    trades = this.repositoryHelper.getTradeRepository().getOtherTradesExceptUserTrades(userOpt.get(), openOnly);
                } else if (this.hasSpecificUserTradingDealsParam()) {
                    // get deals from specific user
                    String usernameForDeals = this.userRequest.getUrl().getUrlParameters().get(SPECIFIC_USER_TRADE_PARAM);
                    trades = this.repositoryHelper.getTradeRepository().getAllTradesForUser(User.builder().username(usernameForDeals).build(), openOnly);
                } else {
                    // get all trading deals
                    trades = this.repositoryHelper.getTradeRepository().getAllTrades(openOnly);
                }
                response.setStatus(StatusCodeEnum.SC_200);
                setResponseContent(response, trades);
            } else if (userRequest.getMethod() == HttpMethod.POST) {
                // add trade or finish trade
                switch (this.userRequest.getUrl().getUrlSegments().size()) {
                    case 1 -> addTrade(response, userOpt.get());
                    case 2 -> finishTrade(response, userOpt.get());
                }
            } else if (userRequest.getMethod() == HttpMethod.DELETE) {
                // delete trading deal
                if (this.userRequest.getUrl().getUrlSegments().size() == 2) {
                    String tradeId = this.userRequest.getUrl().getUrlSegments().get(1);
                    Optional<Trade> tradeOpt = this.repositoryHelper.getTradeRepository().getTradeById(tradeId);
                    if (tradeOpt.isPresent() && tradeOpt.get().getTradeUser().getUsername().equals(userOpt.get().getUsername())) {
                        this.repositoryHelper.getTradeRepository().removeTrade(Trade.builder().id(UUID.fromString(tradeId)).tradeUser(userOpt.get()).build());
                        this.repositoryHelper.getCardRepository().markCardForTrading(tradeOpt.get().getTradeCard().getId().toString(), false);
                        response.setStatus(StatusCodeEnum.SC_200);
                        response.setContent(TRADE_REMOVED_SUCCESSFULLY);
                    } else {
                        response.setStatus(StatusCodeEnum.SC_400);
                        response.setContent(ONLY_OWN_TRADES_MESSAGE);
                    }
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

    private void finishTrade(Response response, User user) throws SQLException {
        if (this.userRequest.getModel() instanceof FinishTradeModel) {
            FinishTradeModel finishTradeModel = (FinishTradeModel) this.userRequest.getModel();
            String tradeCardId = finishTradeModel.getTradeId();
            String tradeId = this.userRequest.getUrl().getUrlSegments().get(1);
            Optional<Trade> toFinishTradeOpt = this.repositoryHelper.getTradeRepository().getTradeById(tradeId);
            if (toFinishTradeOpt.isPresent()) {
                Trade toFinishTrade = toFinishTradeOpt.get();
                if (toFinishTrade.getTradeUser().getUsername().equals(user.getUsername())) {
                    // same user
                    response.setStatus(StatusCodeEnum.SC_400);
                    response.setContent(SELF_TRADE_ERROR_MESSAGE);
                } else {
                    if (user.getCoins() - toFinishTrade.getDesiredCoins() >= 0) {
                        Optional<CardBase> toTradeCardOpt = this.repositoryHelper.getCardRepository().getCardById(tradeCardId);
                        if (toTradeCardOpt.isPresent() &&
                                !toTradeCardOpt.get().isInTradeInvolved() &&
                                toTradeCardOpt.get().getName().equals(toFinishTrade.getDesiredCard().getName()) &&
                                !this.repositoryHelper.getDeckRepository().isCardInUserDeck(user.getUsername(), tradeCardId)) {

                            Optional<Trade> tradeOpt = this.repositoryHelper.getTradeRepository().finishTrade(toFinishTrade, user, tradeCardId);
                            if (tradeOpt.isPresent()) {
                                Trade finishedTrade = tradeOpt.get();
                                // reset card
                                this.repositoryHelper.getCardRepository().markCardForTrading(finishedTrade.getTradeCard().getId().toString(), false);
                                // remove cards from trade from user stack
                                this.repositoryHelper.getStackRepository().removeCardsFromUserStack(finishedTrade.getTradeUser(), List.of(finishedTrade.getTradeCard()));
                                this.repositoryHelper.getStackRepository().removeCardsFromUserStack(finishedTrade.getTradedToUser(), List.of(finishedTrade.getTradedForCard()));
                                // add new traded cards to user stack
                                this.repositoryHelper.getStackRepository().addCardsToUserStack(finishedTrade.getTradeUser(), List.of(finishedTrade.getTradedForCard()));
                                this.repositoryHelper.getStackRepository().addCardsToUserStack(finishedTrade.getTradedToUser(), List.of(finishedTrade.getTradeCard()));

                                // get users
                                getUserForTradeFromDB(finishedTrade);
                                response.setStatus(StatusCodeEnum.SC_200);
                                response.setContent(finishedTrade.toString());
                            }
                        } else {
                            response.setStatus(StatusCodeEnum.SC_400);
                            response.setContent(CARD_NOT_SUITABLE_ERROR_MESSAGE);
                        }
                    } else {
                        response.setStatus(StatusCodeEnum.SC_400);
                        response.setContent(NOT_ENOUGH_COINS_ERROR_MESSAGE + toFinishTrade.getDesiredCoins());
                    }
                }
            } else {
                response.setStatus(StatusCodeEnum.SC_400);
                response.setContent(NO_TRADE_FOUND_ERROR_MESSAGE + " \"" + tradeId + "\".");
            }
        }
    }

    private void addTrade(Response response, User user) throws SQLException {
        if (this.userRequest.getModel() instanceof AddTradeModel) {
            AddTradeModel addTradeModel = (AddTradeModel) this.userRequest.getModel();
            // check if card is already marked
            Optional<CardBase> tradeCardOpt = this.repositoryHelper.getCardRepository().getCardById(addTradeModel.getCardId());
            if (tradeCardOpt.isPresent() && !tradeCardOpt.get().isInTradeInvolved()) {
                CardBase tradeCard = this.repositoryHelper.getCardRepository().markCardForTrading(addTradeModel.getCardId(), true);
                if (!this.repositoryHelper.getDeckRepository().isCardInUserDeck(user.getUsername(), tradeCard.getId().toString())) {
                    CardsEnum desiredCard;
                    try {
                        desiredCard = CardsEnum.valueOf(addTradeModel.getDesiredCardName().toUpperCase());
                    } catch (IllegalArgumentException ex) {
                        desiredCard = null;
                    }
                    Trade toCreate = Trade.builder()
                            .id(UUID.randomUUID())
                            .tradeUser(user)
                            .tradFinished(false)
                            .tradeCard(tradeCard)
                            .desiredCoins(addTradeModel.getDesiredCoins())
                            .desiredCard(desiredCard)
                            .build();

                    if (this.repositoryHelper.getTradeRepository().addTrade(toCreate)) {
                        response.setStatus(StatusCodeEnum.SC_200);
                        response.setContent(toCreate.toString());
                    }
                }
            } else {
                response.setStatus(StatusCodeEnum.SC_400);
                response.setContent(CARD_ALREADY_IN_USE_MESSAGE);
            }
        }
    }

    private void setResponseContent(Response response, List<Trade> trades) throws SQLException {
        if (trades.isEmpty()) {
            response.setContent("{\"Trades\": \"No trades available.\"}");
        } else {
            StringBuilder out = new StringBuilder();
            out.append("{\"Trades\": [");
            int i = 0;
            for (Trade trade : trades) {
                // collect users from db
                getUserForTradeFromDB(trade);

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

    private void getUserForTradeFromDB(Trade trade) throws SQLException {
        Optional<User> tradeUserOpt = this.repositoryHelper.getUserRepository().getUser(trade.getTradeUser().getUsername());
        tradeUserOpt.ifPresent(trade::setTradeUser);
        if (trade.getTradedToUser() != null) {
            tradeUserOpt = this.repositoryHelper.getUserRepository().getUser(trade.getTradedToUser().getUsername());
            tradeUserOpt.ifPresent(trade::setTradedToUser);
        }
    }

    private boolean hasOpenOnlyTradingDealsParam() {
        return !this.userRequest.getUrl().getUrlParameters().isEmpty() &&
                this.userRequest.getUrl().getUrlParameters().get(OPEN_ONLY_TRADE_PARAM) != null &&
                this.userRequest.getUrl().getUrlParameters().get(OPEN_ONLY_TRADE_PARAM).equals(OPEN_ONLY_TRADE_PARAM_VALUE);
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
