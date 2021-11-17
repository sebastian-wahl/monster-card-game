package game.main;

import game.controller.BattleController;
import game.objects.CardBase;
import game.objects.card.factory.CardFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String[] args) {
        List<Class<? extends CardBase>> test = CardFactory.CARD_CLASS_LIST;
        for (Class<? extends CardBase> a : test) {
            System.out.println(a);
        }
        BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();
        blockingQueue.offer("User1");
        blockingQueue.offer("User2");
        BattleController battleController = new BattleController(blockingQueue);
        battleController.handleBattle();
    }
}
