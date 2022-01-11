package game.integration.game;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import game.integration.MonsterCardIntegrationTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.fail;

/**
 * Test /packages + /cards + /deck + /stats + /score + /battles
 */
public class GameIntegrationTest extends MonsterCardIntegrationTest {

    @Override
    @Test
    public void integrationTest() {
        HttpRequest request;
        HttpResponse<String> response;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseObj;
        JsonNode jsonNode;
        try {
            String kienboecToken = getSecurityTokenForUserAndPassword("kienboec", "daniel");
            String altenhofToken = getSecurityTokenForUserAndPassword("altenhof", "markus");

            createAdminPackages(mapper);

            buyAdminPackages(mapper, kienboecToken, altenhofToken);

            List<String> kienboecDeckIds = new ArrayList<>();
            List<String> altenhofDeckIds = new ArrayList<>();

            getStackAndAddFirst4ForPotentialDeckCards(mapper, kienboecToken, altenhofToken, kienboecDeckIds, altenhofDeckIds);


        } catch (IOException | InterruptedException e) {
            fail("Exception occurred during test runtime.");
            e.printStackTrace();
        }
    }

    private void getStackAndAddFirst4ForPotentialDeckCards(ObjectMapper mapper, String kienboecToken, String altenhofToken, List<String> kienboecDeckIds, List<String> altenhofDeckIds) throws IOException, InterruptedException {
        JsonNode jsonNode;
        HttpResponse<String> response;
        JsonNode responseObj;
        HttpRequest request;
        request = getHttpGetRequestSecurityToken("cards", kienboecToken);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotEmpty();
        responseObj = mapper.readTree(response.body());
        jsonNode = responseObj.get("Stack");
        assertThat(jsonNode.size()).isGreaterThanOrEqualTo(5);
        int i = 0;
        for (JsonNode objNode : jsonNode) {
            kienboecDeckIds.add(objNode.get("Card").get("id").asText());
            i++;
            if (i == 4) break;
        }

        request = getHttpGetRequestSecurityToken("cards", altenhofToken);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotEmpty();
        responseObj = mapper.readTree(response.body());
        jsonNode = responseObj.get("Stack");
        assertThat(jsonNode.size()).isGreaterThanOrEqualTo(5);
        i = 0;
        for (JsonNode objNode : jsonNode) {
            altenhofDeckIds.add(objNode.get("Card").get("id").asText());
            i++;
            if (i == 4) break;
        }
    }

    private void buyAdminPackages(ObjectMapper mapper, String kienboecToken, String altenhofToken) throws IOException, InterruptedException {
        HttpRequest request;
        HttpResponse<String> response;
        JsonNode responseObj;
        JsonNode jsonNode;
        request = getHttpPostRequest("packages?buy_admin_package=true", List.of("Content-Type", "application/json", "Authorization", kienboecToken), "");
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotEmpty();
        responseObj = mapper.readTree(response.body());
        jsonNode = responseObj.get("Package");
        assertThat(jsonNode.isArray()).isTrue();
        assertThat(jsonNode.size()).isEqualTo(5);

        request = getHttpPostRequest("packages?buy_admin_package=true", List.of("Content-Type", "application/json", "Authorization", altenhofToken), "");
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotEmpty();
        responseObj = mapper.readTree(response.body());
        jsonNode = responseObj.get("Package");
        assertThat(jsonNode.isArray()).isTrue();
        assertThat(jsonNode.size()).isEqualTo(5);
    }

    private void createAdminPackages(ObjectMapper mapper) throws IOException, InterruptedException {
        HttpRequest request;
        HttpResponse<String> response;
        JsonNode responseObj;
        JsonNode jsonNode;
        request = getHttpPostRequest("packages?add_admin_package=true", List.of("Content-Type", "application/json", "Authorization", "admin-token-1234"),
                "[{\"Id\":\"845f0dc7-37d0-426e-994e-43fc3ac83c07\", \"Name\":\"Grey Goblin\", \"Damage\": 10.0}, " +
                        "{\"Id\":\"99f8f8dc-e25e-4a95-aa2c-782823f36e2z\", \"Name\":\"Dragon\", \"Damage\": 50.0}, " +
                        "{\"Id\":\"e85e3976-7c86-4d06-9a80-641c2019a79d\", \"Name\":\"Water Spell\", \"Damage\": 20.0}, " +
                        "{\"Id\":\"1cb6ab86-bdb2-47e5-b6e4-68c5ab389333\", \"Name\":\"Ork\", \"Damage\": 45.0}, " +
                        "{\"Id\":\"dfdd758f-649c-40f9-ba3a-8657f4b3439d\", \"Name\":\"Fire Spell\", \"Damage\": 25.0}]");

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotEmpty();
        responseObj = mapper.readTree(response.body());
        jsonNode = responseObj.get("Package");
        assertThat(jsonNode.isArray()).isTrue();
        assertThat(jsonNode.size()).isEqualTo(5);

        request = getHttpPostRequest("packages?add_admin_package=true", List.of("Content-Type", "application/json", "Authorization", "admin-token-1234"),
                "[{\"Id\":\"845f0dc7-37d0-426e-994e-43fc3ac83c07\", \"Name\":\"Grey Goblin\", \"Damage\": 10.0}, " +
                        "{\"Id\":\"99f8f8dc-e25e-4a95-aa2c-782823f36e2z\", \"Name\":\"Dragon\", \"Damage\": 50.0}, " +
                        "{\"Id\":\"e85e3976-7c86-4d06-9a80-641c2019a79d\", \"Name\":\"Water Spell\", \"Damage\": 20.0}, " +
                        "{\"Id\":\"1cb6ab86-bdb2-47e5-b6e4-68c5ab389333\", \"Name\":\"Ork\", \"Damage\": 45.0}, " +
                        "{\"Id\":\"dfdd758f-649c-40f9-ba3a-8657f4b3439d\", \"Name\":\"Fire Spell\", \"Damage\": 25.0}]");

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotEmpty();
        responseObj = mapper.readTree(response.body());
        jsonNode = responseObj.get("Package");
        assertThat(jsonNode.isArray()).isTrue();
        assertThat(jsonNode.size()).isEqualTo(5);
    }
}
