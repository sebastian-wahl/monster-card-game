package game.controller;

import game.helper.RepositoryHelper;
import game.http.request.Request;
import game.http.response.Response;

public abstract class ControllerBase {

    // This Queue is used to transfer information from requests to the specific controllers
    protected Request userRequest;
    protected RepositoryHelper repositoryHelper;


    protected ControllerBase(Request request, RepositoryHelper repositoryHelper) {
        this.userRequest = request;
        this.repositoryHelper = repositoryHelper;
    }

    public abstract Response doWork();
}
