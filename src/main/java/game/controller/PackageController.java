package game.controller;

import game.http.enums.StatusCodeEnum;
import game.http.request.Request;
import game.http.response.ConcreteResponse;
import game.http.response.Response;
import game.objects.Package;
import game.objects.User;
import game.repository.RepositoryHelper;

import java.util.Optional;

public class PackageController extends ControllerBase {

    public PackageController(Request request, RepositoryHelper repositoryHelper) {
        super(request, repositoryHelper);
    }

    @Override
    public Response doWork() {
        Response response = new ConcreteResponse();
        Optional<User> userOpt = this.repositoryHelper.getUserRepository().checkTokenAndGetUser(userRequest.getAuthorizationToken());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (userRequest.getContentLength() == 0) {
                // buy package
                if (user.getCoins() - Package.PACKAGE_COST >= 0) {

                    Package pack = new Package();
                    user.setCoins(user.getCoins() - Package.PACKAGE_COST);

                    userOpt = this.repositoryHelper.getStackRepository().addCardsToUserStack(user, pack);
                    if (userOpt.isPresent()) {
                        response.setStatus(StatusCodeEnum.SC_200);
                        response.setContent(pack.toString());
                    } else {
                        response.setStatus(StatusCodeEnum.SC_500);
                    }

                } else {
                    response.setContent("Not enough coins for this action.");
                    response.setStatus(StatusCodeEnum.SC_400);
                }
            } else {
                // "add" package
                // ToDo
            }
        } else {
            response.setStatus(StatusCodeEnum.SC_401);
        }
        return response;
    }
}
