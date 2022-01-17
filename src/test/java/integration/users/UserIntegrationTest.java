package integration.users;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import integration.MonsterCardIntegrationTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.fail;

/**
 * /users + /sessions integration test
 */
public class UserIntegrationTest extends MonsterCardIntegrationTest {

    private static final List<String> USER_LOGIN_JSON_NAMES_LIST = List.of("Authorization", "ValidUntil");
    private static final List<String> USER_GET_USER_JSON_NAMES_LIST = List.of("Username", "Display Name", "Bio", "Image", "Elo", "Statistics");

    @Override
    @Test
    public void integrationTest() {
        HttpRequest request;
        HttpResponse<String> response;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseObj;
        JsonNode jsonNode;
        String requestBody;
        List<String> fieldNames;
        try {
            // create existing users
            createExistingUsers();

            // login kienboec get token
            requestBody = "{\"Username\":\"kienboec\", \"Password\":\"daniel\"}";
            request = getHttpPostRequest("sessions", List.of("Content-Type", "application/json"), requestBody);
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            printRequestAndResponseToConsole(request.toString(), request.headers().toString(), requestBody, response.body());
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).isNotEmpty();
            responseObj = mapper.readTree(response.body());
            fieldNames = this.namesIteratorToList(responseObj.fieldNames());
            assertThat(fieldNames.containsAll(USER_LOGIN_JSON_NAMES_LIST)).isTrue();
            jsonNode = responseObj.get("Authorization");
            String kienboecToken = jsonNode.asText();

            // login altenhof
            requestBody = "{\"Username\":\"altenhof\", \"Password\":\"markus\"}";
            request = getHttpPostRequest("sessions", List.of("Content-Type", "application/json"), requestBody);
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            printRequestAndResponseToConsole(request.toString(), request.headers().toString(), requestBody, response.body());
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).isNotEmpty();
            responseObj = mapper.readTree(response.body());
            fieldNames = this.namesIteratorToList(responseObj.fieldNames());
            assertThat(fieldNames.containsAll(USER_LOGIN_JSON_NAMES_LIST)).isTrue();
            jsonNode = responseObj.get("Authorization");
            String altenhofToken = jsonNode.asText();


            // get own user profile
            getOwnProfile(mapper, kienboecToken, altenhofToken);

            // error, get not own user
            getProfileError(kienboecToken, altenhofToken);


            // change user profile
            changeOwnProfile(mapper, kienboecToken, altenhofToken);

        } catch (InterruptedException | IOException e) {
            fail("Exception occurred during test runtime.");
            e.printStackTrace();

        }
    }

    private void changeOwnProfile(ObjectMapper mapper, String kienboecToken, String altenhofToken) throws IOException, InterruptedException {
        JsonNode responseObj;
        JsonNode jsonNode;
        HttpResponse<String> response;
        HttpRequest request;
        String requestBody;
        requestBody = "{\"Name\": \"Test\",  \"Bio\": \"1234\", \"Image\": \"!\"}";
        request = getHttpPutRequest("/users/kienboec", List.of("Content-Type", "application/json", AUTORISATION_HEADER_KEY, kienboecToken), requestBody);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), requestBody, response.body());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotEmpty();
        responseObj = mapper.readTree(response.body());
        jsonNode = responseObj.get("User");
        assertThat(jsonNode.get("Username").asText()).isEqualTo("kienboec");
        assertThat(jsonNode.get("Display Name").asText()).isEqualTo("Test");
        assertThat(jsonNode.get("Bio").asText()).isEqualTo("1234");
        assertThat(jsonNode.get("Image").asText()).isEqualTo("!");

        requestBody = "{\"Name\": \"Kienboeck\",  \"Bio\": \"Hello!\", \"Image\": \";)\"}";
        request = getHttpPutRequest("/users/kienboec", List.of("Content-Type", "application/json", AUTORISATION_HEADER_KEY, kienboecToken), requestBody);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), requestBody, response.body());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotEmpty();
        responseObj = mapper.readTree(response.body());
        jsonNode = responseObj.get("User");
        assertThat(jsonNode.get("Username").asText()).isEqualTo("kienboec");
        assertThat(jsonNode.get("Display Name").asText()).isEqualTo("Kienboeck");
        assertThat(jsonNode.get("Bio").asText()).isEqualTo("Hello!");
        assertThat(jsonNode.get("Image").asText()).isEqualTo(";)");


        requestBody = "{\"Name\": \"Privat\",  \"Bio\": \"Not welcome!\", \"Image\": \">:/\"}";
        request = getHttpPutRequest("/users/altenhof", List.of("Content-Type", "application/json", AUTORISATION_HEADER_KEY, altenhofToken), requestBody);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), requestBody, response.body());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotEmpty();
        responseObj = mapper.readTree(response.body());
        jsonNode = responseObj.get("User");
        assertThat(jsonNode.get("Username").asText()).isEqualTo("altenhof");
        assertThat(jsonNode.get("Display Name").asText()).isEqualTo("Privat");
        assertThat(jsonNode.get("Bio").asText()).isEqualTo("Not welcome!");
        assertThat(jsonNode.get("Image").asText()).isEqualTo(">:/");

        requestBody = "{\"Name\": \"Altenhof\",  \"Bio\": \"Welcome!\", \"Image\": \":)\"}";
        request = getHttpPutRequest("/users/altenhof", List.of("Content-Type", "application/json", AUTORISATION_HEADER_KEY, altenhofToken), requestBody);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), requestBody, response.body());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotEmpty();
        responseObj = mapper.readTree(response.body());
        jsonNode = responseObj.get("User");
        assertThat(jsonNode.get("Username").asText()).isEqualTo("altenhof");
        assertThat(jsonNode.get("Display Name").asText()).isEqualTo("Altenhof");
        assertThat(jsonNode.get("Bio").asText()).isEqualTo("Welcome!");
        assertThat(jsonNode.get("Image").asText()).isEqualTo(":)");
    }

    private void getProfileError(String kienboecToken, String altenhofToken) throws IOException, InterruptedException {
        HttpResponse<String> response;
        HttpRequest request;
        request = getHttpGetRequestSecurityToken("/users/kienboec", altenhofToken);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), "", response.body());
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.body()).isEqualTo("You can only edit your own profile.");


        request = getHttpGetRequestSecurityToken("/users/altenhof", kienboecToken);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), "", response.body());
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.body()).isEqualTo("You can only edit your own profile.");


        request = getHttpGetRequestSecurityToken("/users/someGuy", kienboecToken);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), "", response.body());
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.body()).isEqualTo("You can only edit your own profile.");
    }

    private void getOwnProfile(ObjectMapper mapper, String kienboecToken, String altenhofToken) throws IOException, InterruptedException {
        JsonNode responseObj;
        JsonNode jsonNode;
        HttpResponse<String> response;
        HttpRequest request;
        List<String> fieldNames;
        request = getHttpGetRequestSecurityToken("/users/kienboec", kienboecToken);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), "", response.body());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotEmpty();
        responseObj = mapper.readTree(response.body());
        fieldNames = this.namesIteratorToList(responseObj.get("User").fieldNames());
        assertThat(fieldNames.containsAll(USER_GET_USER_JSON_NAMES_LIST)).isTrue();
        jsonNode = responseObj.get("User").get("Username");
        assertThat(jsonNode.asText()).isEqualTo("kienboec");

        request = getHttpGetRequestSecurityToken("/users/altenhof", altenhofToken);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), "", response.body());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotEmpty();
        responseObj = mapper.readTree(response.body());
        fieldNames = this.namesIteratorToList(responseObj.get("User").fieldNames());
        assertThat(fieldNames.containsAll(USER_GET_USER_JSON_NAMES_LIST)).isTrue();
        jsonNode = responseObj.get("User").get("Username");
        assertThat(jsonNode.asText()).isEqualTo("altenhof");
    }

    private void createExistingUsers() throws IOException, InterruptedException {
        HttpRequest request;
        HttpResponse<String> response;// create user kienboec - already exists
        String requestBody;
        requestBody = "{\"Username\":\"kienboec\", \"Password\":\"daniel\"}";
        request = getHttpPostRequest("users", List.of("Content-Type", "application/json"), requestBody);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), requestBody, response.body());
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.body()).isEqualTo("Bad request");

        // create user altenhof - already exists
        requestBody = "{\"Username\":\"altenhof\", \"Password\":\"markus\"}";
        request = getHttpPostRequest("users", List.of("Content-Type", "application/json"), requestBody);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), requestBody, response.body());
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.body()).isEqualTo("Bad request");
    }


}
