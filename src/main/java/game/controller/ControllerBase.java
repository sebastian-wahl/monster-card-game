package game.controller;

import game.http.request.Request;
import game.http.response.Response;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;

public abstract class ControllerBase {

    // This Queue is used to transfer information from requests to the specific controllers
    protected Queue<String> queue;
    protected Request userRequest;


    protected ControllerBase(BlockingQueue<String> queue, Request request) {
        this.queue = queue;
        this.userRequest = request;
    }

    public abstract Response doWork();
}
