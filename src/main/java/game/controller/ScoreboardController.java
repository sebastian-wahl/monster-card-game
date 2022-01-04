package game.controller;

import game.helper.RepositoryHelper;
import game.http.enums.StatusCodeEnum;
import game.http.request.Request;
import game.http.response.ConcreteResponse;
import game.http.response.Response;
import game.objects.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ScoreboardController extends ControllerBase {

    private static final String FORMAT_PARAMETER = "format";
    private static final String FORMAT_PARAMETER_VALUE = "simple";

    public ScoreboardController(Request request, RepositoryHelper repositoryHelper) {
        super(request, repositoryHelper);
    }

    @Override
    public Response doWork() {
        Response response = new ConcreteResponse();
        Optional<User> userOpt = this.repositoryHelper.getUserRepository().checkTokenAndGetUser(userRequest.getAuthorizationToken());
        if (userOpt.isPresent()) {
            List<User> sortedUserList = this.repositoryHelper.getUserRepository().getAllUsers().stream().sorted(User::compareEloToHighToLow).collect(Collectors.toList());
            if (!sortedUserList.isEmpty()) {
                // Build scoreboard
                if (!this.userRequest.getUrl().getUrlParameters().isEmpty() && this.userRequest.getUrl().getUrlParameters().get(FORMAT_PARAMETER).equals(FORMAT_PARAMETER_VALUE)) {
                    response.setContent(this.buildsSimpleScoreboard(sortedUserList));

                } else {
                    response.setContent(this.buildScoreboard(sortedUserList));
                }
                response.setStatus(StatusCodeEnum.SC_200);
            }
        } else {
            response.setStatus(StatusCodeEnum.SC_401);
            response.setContent(WRONG_SECURITY_TOKEN_ERROR_MESSAGE);
        }
        return response;
    }

    private String buildsSimpleScoreboard(List<User> sortedUserList) {
        StringBuilder sb = new StringBuilder();

        sb.append("{\"Scoreboard\": ");
        if (!sortedUserList.isEmpty()) {
            sb.append("{");
            for (int i = 1; i <= sortedUserList.size(); i++) {
                User user = sortedUserList.get(i - 1);
                sb.append("\"").append(i).append(".\": ");
                sb.append("\"").append(user.getUsername());
                if (user.getDisplayName() != null) {
                    sb.append("/").append(user.getDisplayName());
                }
                sb.append("\"");
                if (i < sortedUserList.size()) {
                    sb.append(", ");
                }
            }
            sb.append("}");
        } else {
            sb.append("No Users scored yet.");
        }
        sb.append("}");
        return sb.toString();
    }

    private String buildScoreboard(List<User> sortedUserList) {
        StringBuilder sb = new StringBuilder();

        sb.append("{\"Scoreboard\": ");
        if (!sortedUserList.isEmpty()) {
            sb.append("{");
            for (int i = 1; i <= sortedUserList.size(); i++) {
                sb.append("\"").append(i).append(".\": ");
                sb.append(sortedUserList.get(i - 1).toString());
                if (i < sortedUserList.size()) {
                    sb.append(", ");
                }
            }
            sb.append("}");
        } else {
            sb.append("No Users scored yet.");
        }
        sb.append("}");
        return sb.toString();
    }
}
