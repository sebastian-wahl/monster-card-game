package game.controller.battlecontroller;

import game.helper.RepositoryHelper;
import game.helper.battle.BattleReportHelper;
import game.helper.battle.Round;
import game.http.enums.StatusCodeEnum;
import game.http.response.ConcreteResponse;
import game.http.response.Response;
import game.objects.CardBase;
import game.objects.Deck;
import game.objects.User;
import game.objects.enums.FightOutcome;
import lombok.Synchronized;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class BattleQueueHandler {

    private final RepositoryHelper repositoryHelper;
    private final Map<String, CompletableFuture<Response>> completableFutureMap;

    public BattleQueueHandler(RepositoryHelper repositoryHelper) {
        this.repositoryHelper = repositoryHelper;
        completableFutureMap = new HashMap<>();
    }

    @Synchronized
    public CompletableFuture<Response> addUserToBattleQueueAndHandleBattle(String user) {
        CompletableFuture<Response> output = new CompletableFuture<>();
        if (this.completableFutureMap.containsKey(user)) {
            // user already in queue
            Response response = new ConcreteResponse();
            response.setContent("User is already queued in another connection.");
            response.setStatus(StatusCodeEnum.SC_400);
            output.complete(response);
            return output;
        }

        this.completableFutureMap.put(user, output);
        if (this.completableFutureMap.size() == 2) {
            this.handleBattle(new ArrayList<>(this.completableFutureMap.keySet()));
            this.completableFutureMap.clear();
        }
        return output;
    }

    protected void handleBattle(List<String> usernames) {
        Deck playerOneDeck;
        Optional<Deck> playerOneOpt = this.repositoryHelper.getDeckRepository().getDeckByUsername(usernames.get(0));
        if (playerOneOpt.isPresent()) {
            playerOneDeck = playerOneOpt.get();
        } else {
            Response response = new ConcreteResponse();
            response.setContent("User: \"" + usernames.get(0) + "\" hasn't selected a deck yet! A battle cannot start if a user has not selected a deck!");
            response.setStatus(StatusCodeEnum.SC_400);
            for (String user : usernames) {
                this.completableFutureMap.get(user).complete(response);
            }
            return;
        }

        Deck playerTwoDeck;
        Optional<Deck> playerTwoOpt = this.repositoryHelper.getDeckRepository().getDeckByUsername(usernames.get(1));
        if (playerTwoOpt.isPresent()) {
            playerTwoDeck = playerTwoOpt.get();
        } else {
            Response response = new ConcreteResponse();
            response.setContent("User: \"" + usernames.get(1) + "\" hasn't selected a deck yet! A battle cannot start if a user has not selected a deck!");
            response.setStatus(StatusCodeEnum.SC_400);
            for (String user : usernames) {
                this.completableFutureMap.get(user).complete(response);
            }
            return;
        }
        BattleReportHelper rh = new BattleReportHelper();

        int counter = 0;
        CardBase attackerCard;
        CardBase defenderCard;
        while (!playerOneDeck.isDeckEmpty() && !playerTwoDeck.isDeckEmpty() && counter < 100) {
            FightOutcome fightOutcome;

            attackerCard = playerOneDeck.getRandomCard();
            defenderCard = playerTwoDeck.getRandomCard();
            // fight
            fightOutcome = attackerCard.attack(defenderCard);

            // add round to logger
            rh.addRound(Round.builder().roundNumber(counter + 1).monster1(attackerCard).monster2(defenderCard)
                    .fightOutcome(fightOutcome).user1(usernames.get(0)).user2(usernames.get(1)).build());

            counter++;
        }

        if (playerOneDeck.getDeckSize() > playerTwoDeck.getDeckSize()) {
            rh.setFightOutcome(FightOutcome.ATTACKER);
            updateScore(usernames.get(0), usernames.get(1));
        } else if (playerOneDeck.getDeckSize() < playerTwoDeck.getDeckSize()) {
            updateScore(usernames.get(1), usernames.get(0));
            rh.setFightOutcome(FightOutcome.DEFENDER);
        } else {
            updateScoreTie(usernames);
            rh.setFightOutcome(FightOutcome.TIE);
        }

        // Get updated users
        Optional<User> userOpt1 = this.repositoryHelper.getUserRepository().getUser(usernames.get(0));
        Optional<User> userOpt2 = this.repositoryHelper.getUserRepository().getUser(usernames.get(1));
        if (userOpt1.isPresent() && userOpt2.isPresent()) {
            rh.setUser1(userOpt1.get());
            rh.setUser2(userOpt2.get());
            Response response = new ConcreteResponse();
            response.setContent(rh.toString());
            response.setStatus(StatusCodeEnum.SC_200);

            for (String user : usernames) {
                this.completableFutureMap.get(user).complete(response);
            }
        } else {
            for (String user : usernames) {
                this.completableFutureMap.get(user).complete(new ConcreteResponse());
            }
        }
    }

    private void updateScore(String winningPlayer, String losingPlayer) {
        if (this.repositoryHelper.getUserRepository().updateEloAndGamesPlayed(winningPlayer, losingPlayer)) {
            System.out.println("Score updated");
        } else {
            System.out.println("Error when updating");
        }
    }

    private void updateScoreTie(List<String> players) {
        if (this.repositoryHelper.getUserRepository().updateTieAndGamesPlayed(players)) {
            System.out.println("Tie: Score updated");
        } else {
            System.out.println("Tie: Error when updating");
        }
    }
}
