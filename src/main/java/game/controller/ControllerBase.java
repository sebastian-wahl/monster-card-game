package game.controller;

import game.db.DatabaseConnectionProvider;
import game.helper.RepositoryHelper;
import game.http.request.Request;
import game.http.response.ConcreteResponse;
import game.http.response.Response;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class ControllerBase {

    protected static final String WRONG_SECURITY_TOKEN_ERROR_MESSAGE = "Authentication token is invalid. Please login again.";
    protected static final String WRONG_BODY_MESSAGE = "Wrong HTTP body for this request.";

    // This Queue is used to transfer information from requests to the specific controllers
    protected Request userRequest;
    protected RepositoryHelper repositoryHelper;


    protected ControllerBase(Request request, RepositoryHelper repositoryHelper) {
        this.userRequest = request;
        this.repositoryHelper = repositoryHelper;
    }

    public Response doWork() {
        Response response = new ConcreteResponse();
        try (Connection connection = DatabaseConnectionProvider.getConnection()) {
            this.repositoryHelper.getUserRepository().beginTransaction();
            // ---
            response = this.doWorkIntern();
            // ---
            this.repositoryHelper.getUserRepository().endTransaction();
        } catch (Exception ex) {
            // rollback
            ex.printStackTrace();
            System.out.println("Rollback!");
            try {
                this.repositoryHelper.getUserRepository().doRollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    protected abstract Response doWorkIntern() throws SQLException;
}
