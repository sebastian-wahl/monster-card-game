package game.integration.users;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import game.integration.MonsterCardIntegrationTest;
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

    @Override
    @Test
    public void integrationTest() {
        HttpRequest request;
        HttpResponse<String> response;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseObj;
        JsonNode jsonNode;
        try {
            // create existing users
            createExistingUsers();

            // login kienboec get token
            request = getHttpPostRequest("sessions", List.of("Content-Type", "application/json"), "{\"Username\":\"kienboec\", \"Password\":\"daniel\"}");
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).isNotEmpty();
            responseObj = mapper.readTree(response.body());
            jsonNode = responseObj.get("Authorization");
            String kienboecToken = jsonNode.asText();

            // login altenhof
            request = getHttpPostRequest("sessions", List.of("Content-Type", "application/json"), "{\"Username\":\"altenhof\", \"Password\":\"markus\"}");
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).isNotEmpty();
            responseObj = mapper.readTree(response.body());
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
        request = getHttpPutRequest("/users/kienboec", List.of("Content-Type", "application/json", AUTORISATION_HEADER_KEY, kienboecToken), "{\"Name\": \"Test\",  \"Bio\": \"1234\", \"Image\": \"!\"}");
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotEmpty();
        responseObj = mapper.readTree(response.body());
        jsonNode = responseObj.get("User");
        assertThat(jsonNode.get("Username").asText()).isEqualTo("kienboec");
        assertThat(jsonNode.get("Display Name").asText()).isEqualTo("Test");
        assertThat(jsonNode.get("Bio").asText()).isEqualTo("1234");
        assertThat(jsonNode.get("Image").asText()).isEqualTo("!");
        request = getHttpPutRequest("/users/kienboec", List.of("Content-Type", "application/json", AUTORISATION_HEADER_KEY, kienboecToken), "{\"Name\": \"Kienboeck\",  \"Bio\": \"Hello!\", \"Image\": \";)\"}");
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotEmpty();
        responseObj = mapper.readTree(response.body());
        jsonNode = responseObj.get("User");
        assertThat(jsonNode.get("Username").asText()).isEqualTo("kienboec");
        assertThat(jsonNode.get("Display Name").asText()).isEqualTo("Kienboeck");
        assertThat(jsonNode.get("Bio").asText()).isEqualTo("Hello!");
        assertThat(jsonNode.get("Image").asText()).isEqualTo(";)");

        request = getHttpPutRequest("/users/altenhof", List.of("Content-Type", "application/json", AUTORISATION_HEADER_KEY, altenhofToken), "{\"Name\": \"Privat\",  \"Bio\": \"Not welcome!\", \"Image\": \">:/\"}");
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotEmpty();
        responseObj = mapper.readTree(response.body());
        jsonNode = responseObj.get("User");
        assertThat(jsonNode.get("Username").asText()).isEqualTo("altenhof");
        assertThat(jsonNode.get("Display Name").asText()).isEqualTo("Privat");
        assertThat(jsonNode.get("Bio").asText()).isEqualTo("Not welcome!");
        assertThat(jsonNode.get("Image").asText()).isEqualTo(">:/");
        request = getHttpPutRequest("/users/altenhof", List.of("Content-Type", "application/json", AUTORISATION_HEADER_KEY, altenhofToken), "{\"Name\": \"Altenhof\",  \"Bio\": \"Welcome!\", \"Image\": \":)\"}");
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
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
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.body()).isEqualTo("You can only edit your own profile.");

        request = getHttpGetRequestSecurityToken("/users/altenhof", kienboecToken);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.body()).isEqualTo("You can only edit your own profile.");

        request = getHttpGetRequestSecurityToken("/users/someGuy", kienboecToken);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.body()).isEqualTo("You can only edit your own profile.");
    }

    private void getOwnProfile(ObjectMapper mapper, String kienboecToken, String altenhofToken) throws IOException, InterruptedException {
        JsonNode responseObj;
        JsonNode jsonNode;
        HttpResponse<String> response;
        HttpRequest request;
        request = getHttpGetRequestSecurityToken("/users/kienboec", kienboecToken);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotEmpty();
        responseObj = mapper.readTree(response.body());
        jsonNode = responseObj.get("User").get("Username");
        assertThat(jsonNode.asText()).isEqualTo("kienboec");

        request = getHttpGetRequestSecurityToken("/users/altenhof", altenhofToken);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotEmpty();
        responseObj = mapper.readTree(response.body());
        jsonNode = responseObj.get("User").get("Username");
        assertThat(jsonNode.asText()).isEqualTo("altenhof");
    }

    private void createExistingUsers() throws IOException, InterruptedException {
        HttpRequest request;
        HttpResponse<String> response;// create user kienboec - already exists
        request = getHttpPostRequest("users", List.of("Content-Type", "application/json"), "{\"Username\":\"kienboec\", \"Password\":\"daniel\"}");
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.body()).isEqualTo("Bad request");

        // create user altenhof - already exists
        request = getHttpPostRequest("users", List.of("Content-Type", "application/json"), "{\"Username\":\"altenhof\", \"Password\":\"markus\"}");
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.body()).isEqualTo("Bad request");
    }


}
