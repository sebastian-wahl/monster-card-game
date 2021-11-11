package card.game.main;

import card.game.controller.BattleController;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String[] args) {
        BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();
        blockingQueue.offer("User1");
        blockingQueue.offer("User2");
        BattleController battleController = new BattleController(blockingQueue);
        battleController.handleBattle();
    }
}
