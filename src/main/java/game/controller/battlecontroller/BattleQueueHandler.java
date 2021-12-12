package game.controller.battlecontroller;

import game.http.enums.StatusCodeEnum;
import game.http.response.ConcreteResponse;
import game.http.response.Response;
import game.objects.CardBase;
import game.objects.Deck;
import game.objects.enums.FightOutcome;
import game.repository.RepositoryHelper;
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

    private void handleBattle(List<String> usernames) {
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

        StringBuilder log = new StringBuilder();
        log.append("Starting battle between ").append(usernames.get(0)).append(" and ").append(usernames.get(1)).append(":");

        int counter = 0;
        CardBase attackerCard;
        CardBase defenderCard;
        while (!playerOneDeck.isDeckEmpty() && !playerTwoDeck.isDeckEmpty() && counter < 100) {
            FightOutcome fightOutcome;

            attackerCard = playerOneDeck.getRandomCard();
            defenderCard = playerTwoDeck.getRandomCard();

            fightOutcome = attackerCard.attack(defenderCard);
            log.append("Round ").append(counter + 1).append("\n");
            log.append(usernames.get(0)).append(": ").append(attackerCard.getName()).append(" (Damage: ").append(attackerCard.getDamage()).append(", Type: ").append(attackerCard.getElementType().toString()).append(")")
                    .append(" VS ")
                    .append(usernames.get(1)).append(": ").append(defenderCard.getName()).append(" (Damage: ").append(defenderCard.getDamage()).append(", Type: ").append(defenderCard.getElementType().toString()).append(")");
            if (fightOutcome == FightOutcome.ATTACKER) {
                log.append("\nOutcome -> Winner: ").append(usernames.get(0));
                playerOneDeck.addCard(defenderCard);
                playerTwoDeck.removeCard(defenderCard);
            } else if (fightOutcome == FightOutcome.DEFENDER) {
                log.append("\nOutcome -> Winner: ").append(usernames.get(1));
                playerTwoDeck.addCard(attackerCard);
                playerOneDeck.removeCard(attackerCard);
            } else {
                // Tie
                log.append("\nOutcome -> Round ended in a draw!");
            }

            log.append("\n");
            counter++;
        }

        String responseContent;
        if (playerOneDeck.getDeckSize() > playerTwoDeck.getDeckSize()) {
            log.append(usernames.get(0)).append(" won the battle");
            updateScore(usernames.get(0), usernames.get(1));
            responseContent = "{\"Winner\": \"" + usernames.get(0) + "\", \"fullReport\":\"" + log.toString() + "\"}";
        } else if (playerOneDeck.getDeckSize() < playerTwoDeck.getDeckSize()) {
            log.append(usernames.get(1)).append(" won the battle");
            updateScore(usernames.get(0), usernames.get(1));
            responseContent = "{\"Winner\": \"" + usernames.get(1) + "\", \"fullReport\":\"" + log.toString() + "\"}";
        } else {
            log.append("Game ended in a Tie");
            updateScoreTie(usernames);
            responseContent = "{\"Winner\": \"Game ended in a Tie\", \"fullReport\":\"" + log.toString() + "\"}";
        }

        Response response = new ConcreteResponse();
        response.setContent(responseContent);
        response.setStatus(StatusCodeEnum.SC_200);

        for (String user : usernames) {
            this.completableFutureMap.get(user).complete(response);
        }
    }

    private void updateScore(String winningPlayer, String loosingPlayer) {
        if (this.repositoryHelper.getUserRepository().updateElo(winningPlayer, loosingPlayer)) {
            System.out.println("Score updated");
        } else {
            System.out.println("Error when updating");
        }
    }

    private void updateScoreTie(List<String> players) {

    }
}
