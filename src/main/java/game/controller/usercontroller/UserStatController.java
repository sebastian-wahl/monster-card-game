package game.controller.usercontroller;

import game.controller.ControllerBase;
import game.helper.RepositoryHelper;
import game.http.enums.StatusCodeEnum;
import game.http.request.Request;
import game.http.response.ConcreteResponse;
import game.http.response.Response;
import game.objects.User;

import java.sql.SQLException;
import java.util.Optional;

public class UserStatController extends ControllerBase {

    public UserStatController(Request request, RepositoryHelper repositoryHelper) {
        super(request, repositoryHelper);
    }

    @Override
    public Response doWorkIntern() throws SQLException {
        Response response = new ConcreteResponse();
        Optional<User> userOpt = this.repositoryHelper.getUserRepository().checkTokenAndGetUser(userRequest.getAuthorizationToken());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            response.setContent(this.buildStats(user));
            response.setStatus(StatusCodeEnum.SC_200);
        } else {
            response.setStatus(StatusCodeEnum.SC_401);
            response.setContent(WRONG_SECURITY_TOKEN_ERROR_MESSAGE);
        }
        return response;
    }

    private String buildStats(User user) {
        return "{\"" + user.getUsername() + "\": " +
                user.getStatisticString() +
                "}";
    }
}
