package game.controller;

import game.http.enums.StatusCodeEnum;
import game.http.request.Request;
import game.http.response.ConcreteResponse;
import game.http.response.Response;
import game.objects.User;
import game.repository.RepositoryHelper;

import java.util.Optional;

public class StackController extends ControllerBase {

    public StackController(Request request, RepositoryHelper repositoryHelper) {
        super(request, repositoryHelper);
    }

    @Override
    public Response doWork() {
        Response response = new ConcreteResponse();

        Optional<User> isValid = this.repositoryHelper.getUserRepository().checkTokenAndGetUser(this.userRequest.getAuthorizationToken());
        if (isValid.isPresent()) {
            // set content
            Optional<User> user = this.repositoryHelper.getStackRepository().getUserStack(isValid.get());
            if (user.isPresent()) {
                response.setStatus(StatusCodeEnum.SC_200);
                response.setContent(user.get().getStack().toString());
            } else {
                response.setStatus(StatusCodeEnum.SC_500);
            }
        } else {
            response.setStatus(StatusCodeEnum.SC_401);
        }
        return response;
    }
}
