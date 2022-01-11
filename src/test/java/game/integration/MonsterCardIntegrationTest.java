package game.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import game.server.Server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;


public abstract class MonsterCardIntegrationTest {
    protected static final String SERVER_BASE_URL = "http://localhost:" + Server.PORT + "/";

    protected static final String AUTORISATION_HEADER_KEY = "Authorization";

    protected HttpClient client = HttpClient.newHttpClient();

    public abstract void integrationTest();

    protected String getSecurityTokenForUserAndPassword(String username, String password) throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();
        HttpRequest request = getHttpPostRequest("sessions", Collections.emptyList(), "{\"Username\":\"" + username + "\", \"Password\":\"" + password + "\"}");
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readTree(response.body()).get(AUTORISATION_HEADER_KEY).asText();
    }

    public static HttpRequest getHttpGetRequest(String path, List<String> headers) {
        if (headers.isEmpty()) {
            return HttpRequest.newBuilder()
                    .uri(URI.create(SERVER_BASE_URL + path))
                    .build();
        }
        return HttpRequest.newBuilder()
                .uri(URI.create(SERVER_BASE_URL + path))
                .headers(headers.toArray(String[]::new))
                .build();
    }

    public static HttpRequest getHttpGetRequestSecurityToken(String path, String securityToken) {
        return HttpRequest.newBuilder(
                        URI.create(SERVER_BASE_URL + path))
                .header(AUTORISATION_HEADER_KEY, securityToken)
                .build();
    }

    public static HttpRequest getHttpPostRequest(String path, List<String> headers, String requestBody) {
        if (headers.isEmpty()) {
            return HttpRequest.newBuilder()
                    .uri(URI.create(SERVER_BASE_URL + path))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
        }
        return HttpRequest.newBuilder()
                .uri(URI.create(SERVER_BASE_URL + path))
                .headers(headers.toArray(String[]::new))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
    }

    public static HttpRequest getHttpPutRequest(String path, List<String> headers, String requestBody) {
        if (headers.isEmpty()) {
            return HttpRequest.newBuilder()
                    .uri(URI.create(SERVER_BASE_URL + path))
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
        }
        return HttpRequest.newBuilder()
                .uri(URI.create(SERVER_BASE_URL + path))
                .headers(headers.toArray(String[]::new))
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
    }
}
