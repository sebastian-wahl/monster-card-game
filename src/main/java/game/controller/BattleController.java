package game.controller;

import game.objects.CardBase;
import game.objects.Deck;
import game.objects.enums.FightOutcome;
import game.objects.monstercards.FireElf;
import game.objects.monstercards.GreyGoblin;
import game.objects.monstercards.Knight;
import game.objects.monstercards.Ork;
import game.objects.spellcards.DarkSpell;
import game.objects.spellcards.FireSpell;
import game.objects.spellcards.WaterSpell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class BattleController extends ControllerBase {
    public BattleController(BlockingQueue<String> battleQueue) {
        super(battleQueue);
    }

    public void handleBattle() {
        List<String> players = new ArrayList<>(Arrays.asList(battleQueue.poll(), battleQueue.poll()));
        List<CardBase> deck1 = new ArrayList<>();
        deck1.add(new FireElf());
        deck1.add(new GreyGoblin());
        deck1.add(new Ork());
        deck1.add(new FireSpell());

        List<CardBase> deck2 = new ArrayList<>();
        deck2.add(new Knight());
        deck2.add(new Ork());
        deck2.add(new DarkSpell());
        deck2.add(new WaterSpell());

        Deck playerOneDeck = new Deck(deck1);
        Deck playerTwoDeck = new Deck(deck2);

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

        battleQueue.clear();

        printLog(log);
        // ToDo send response with log

    }

    private void updateScore(String winningPlayer, String loosingPlayer) {

    }

    private void updateScoreTie(List<String> players) {

    }

    private void printLog(StringBuilder log) {
        System.out.println(log.toString());
    }
}