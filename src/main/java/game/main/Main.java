package game.main;

import game.controller.BattleController;
import game.objects.CardBase;
import game.objects.Package;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String[] args) {

        for (int i = 0; i < 3; i++) {
            Package a = new Package();
            for (CardBase card : a.cards) {
                System.out.println(card.toString());
            }
            System.out.println("Next package! \n");
        }


        BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();
        blockingQueue.offer("User1");
        blockingQueue.offer("User2");
        BattleController battleController = new BattleController(blockingQueue);
        battleController.handleBattle();
    }
}
