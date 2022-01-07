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
        Connection connection = null;
        try {
            connection = DatabaseConnectionProvider.getConnection();
            connection.setAutoCommit(false);
            this.repositoryHelper.setConnection(connection);
            // ---
            response = this.doWorkIntern();
            // ---
            connection.commit();
            // close connection
            connection.close();
        } catch (Exception ex) {
            // rollback
            if (connection != null) {
                try {
                    System.out.println("Rollback!");
                    connection.rollback();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            ex.printStackTrace();
        }
        return response;
    }

    protected abstract Response doWorkIntern() throws SQLException;
}
