package game.controller;

import game.http.request.Request;
import game.http.response.Response;
import game.repository.RepositoryHelper;

public class DeckController extends ControllerBase {

    public DeckController(Request request, RepositoryHelper repositoryHelper) {
        super(request, repositoryHelper);
    }

    @Override
    public Response doWork() {
        return null;
    }
}
