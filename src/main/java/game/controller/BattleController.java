package game.controller;

import game.http.request.Request;
import game.http.response.ConcreteResponse;
import game.http.response.Response;
import game.objects.CardBase;
import game.objects.Deck;
import game.objects.enums.FightOutcome;
import game.objects.exceptions.DeckNotFoundException;
import game.repository.DeckRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

import static game.http.HttpReady.USERNAME_KEY;
import static game.http.enums.StatusCodeEnum.SC_400;
import static game.http.enums.StatusCodeEnum.SC_500;

public class BattleController extends ControllerBase {
    private final DeckRepository deckRepository;

    public BattleController(BlockingQueue<String> battleQueue, Request request) {
        this(battleQueue, request, new DeckRepository());
    }

    public BattleController(BlockingQueue<String> battleQueue, Request request, DeckRepository deckRepository) {
        super(battleQueue, request);
        this.deckRepository = deckRepository;

        try {
            this.handleBattle();
        } catch (DeckNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Response doWork() {
        Response response = new ConcreteResponse();
        try {
            this.queue.add(this.userRequest.getContent().get(USERNAME_KEY));
            while (this.queue.size() < 2) {
                Thread.sleep(1000);
            }
            this.handleBattle();
        } catch (DeckNotFoundException e) {
            response.setStatus(SC_400);
            response.setContent(e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            response.setStatus(SC_500);
        }
        return response;
    }

    public String handleBattle() throws DeckNotFoundException {
        List<String> players = new ArrayList<>(Arrays.asList(queue.poll(), queue.poll()));

        Deck playerOneDeck;
        Optional<Deck> playerOneOpt = deckRepository.getDeckByUsername(players.get(0));
        if (playerOneOpt.isPresent()) {
            playerOneDeck = playerOneOpt.get();
        } else {
            throw new DeckNotFoundException(players.get(0));
        }

        Deck playerTwoDeck;
        Optional<Deck> playerTwoOpt = deckRepository.getDeckByUsername(players.get(1));
        if (playerTwoOpt.isPresent()) {
            playerTwoDeck = playerTwoOpt.get();
        } else {
            throw new DeckNotFoundException(players.get(1));
        }

        StringBuilder log = new StringBuilder();
        log.append("Starting battle between ").append(players.get(0)).append(" and ").append(players.get(1)).append(":");

        int counter = 0;
        CardBase attackerCard;
        CardBase defenderCard;
        while (!playerOneDeck.isDeckEmpty() && !playerTwoDeck.isDeckEmpty() && counter < 100) {
            FightOutcome fightOutcome;

            attackerCard = playerOneDeck.getRandomCard();
            defenderCard = playerTwoDeck.getRandomCard();

            fightOutcome = attackerCard.attack(defenderCard);
            log.append("Round ").append(counter + 1).append("\n");
            log.append(players.get(0)).append(": ").append(attackerCard.getName()).append(" (Damage: ").append(attackerCard.getDamage()).append(", Type: ").append(attackerCard.getElementType().toString()).append(")")
                    .append(" VS ")
                    .append(players.get(1)).append(": ").append(defenderCard.getName()).append(" (Damage: ").append(defenderCard.getDamage()).append(", Type: ").append(defenderCard.getElementType().toString()).append(")");
            if (fightOutcome == FightOutcome.ATTACKER) {
                log.append("\nOutcome -> Winner: ").append(players.get(0));
                playerOneDeck.addCard(defenderCard);
                playerTwoDeck.removeCard(defenderCard);
            } else if (fightOutcome == FightOutcome.DEFENDER) {
                log.append("\nOutcome -> Winner: ").append(players.get(1));
                playerTwoDeck.addCard(attackerCard);
                playerOneDeck.removeCard(attackerCard);
            } else {
                // Tie
                log.append("\nOutcome -> Round ended in a draw!");
            }

            log.append("\n");
            counter++;
        }
        if (playerOneDeck.getDeckSize() > playerTwoDeck.getDeckSize()) {
            log.append(players.get(0)).append(" won the battle");
            updateScore(players.get(0), players.get(1));
        } else if (playerOneDeck.getDeckSize() < playerTwoDeck.getDeckSize()) {
            log.append(players.get(1)).append(" won the battle");
            updateScore(players.get(0), players.get(1));
        } else {
            log.append("Game ended in a Tie");
            updateScoreTie(players);
        }

        queue.clear();

        // printLog(log);
        // ToDo send response with log
        return log.toString();
    }

    private void updateScore(String winningPlayer, String loosingPlayer) {

    }

    private void updateScoreTie(List<String> players) {

    }

    private void printLog(StringBuilder log) {
        System.out.println(log.toString());
    }
}
