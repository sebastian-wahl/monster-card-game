package card.game.controller;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;

public abstract class ControllerBase {

    protected Queue<String> battleQueue;

    public ControllerBase(BlockingQueue<String> battleQueue) {
        this.battleQueue = battleQueue;

    }
}
