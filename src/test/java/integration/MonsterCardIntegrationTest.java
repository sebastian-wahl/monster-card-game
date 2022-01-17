package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import game.server.Server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public abstract class MonsterCardIntegrationTest {
    protected static final String SERVER_BASE_URL = "http://localhost:" + Server.PORT + "/";

    protected static final String AUTORISATION_HEADER_KEY = "Authorization";

    protected HttpClient client = HttpClient.newHttpClient();

    protected void printRequestToConsole(String request, String headers, String body) {
        System.out.println(request + ", Headers: " + headers + ", Body: " + body + "\n");
    }

    protected void printRequestAndResponseToConsole(String request, String headers, String body, String responseBody) {
        System.out.println("Request: " + request + ", Headers: " + headers + ", Body: " + body + "\nResponse: " + responseBody + "\n");
    }

    protected void printToConsole(String text) {
        System.out.println(text + "\n");
    }

    public abstract void integrationTest();

    protected String getSecurityTokenForUserAndPassword(String username, String password) throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();
        String requestBody = "{\"Username\":\"" + username + "\", \"Password\":\"" + password + "\"}";
        HttpRequest request = getHttpPostRequest("sessions", Collections.emptyList(), requestBody);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), requestBody, response.body());
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

    public static HttpRequest getHttpDeleteRequest(String path, List<String> headers) {
        if (headers.isEmpty()) {
            return HttpRequest.newBuilder()
                    .uri(URI.create(SERVER_BASE_URL + path))
                    .DELETE()
                    .build();
        }
        return HttpRequest.newBuilder()
                .uri(URI.create(SERVER_BASE_URL + path))
                .headers(headers.toArray(String[]::new))
                .DELETE()
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

    protected List<String> namesIteratorToList(Iterator<String> it) {
        List<String> out = new ArrayList<>();
        while (it.hasNext()) {
            out.add(it.next());
        }

        return out;
    }
}
