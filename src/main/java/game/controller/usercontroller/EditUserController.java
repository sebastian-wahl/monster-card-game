package game.controller.usercontroller;

import game.controller.ControllerBase;
import game.helper.RepositoryHelper;
import game.http.request.Request;
import game.http.response.ConcreteResponse;
import game.http.response.Response;

public class EditUserController extends ControllerBase {
    public EditUserController(Request request, RepositoryHelper repositoryHelper) {
        super(request, repositoryHelper);
    }

    @Override
    public Response doWork() {
        Response response = new ConcreteResponse();

        return response;
    }
}
