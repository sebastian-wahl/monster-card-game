package integration.game;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import integration.MonsterCardIntegrationTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.fail;

/**
 * Test /packages + /cards + /deck + /stats + /score + /battles
 */
public class GameIntegrationTest extends MonsterCardIntegrationTest {

    private static final List<String> GAME_USER_STATS_JSON_NAMES_LIST = List.of("Elo", "Games played", "Total Wins", "Total Losses", "Win/Lose Ratio", "Total Ties");
    private static final List<String> GAME_USER_SCORE_STATS_JSON_NAMES_LIST = List.of("Games played", "Total Wins", "Total Losses", "Win/Lose Ratio", "Total Ties");
    private static final List<String> GAME_ADD_OR_BUY_ADMIN_PACKAGE_JSON_NAMES_LIST = List.of("Package");
    private static final List<String> GAME_GET_STACK_JSON_NAMES_LIST = List.of("Stack");
    private static final List<String> GAME_GET_OR_SET_DECK_STACK_JSON_NAMES_LIST = List.of("Deck");
    private static final List<String> GAME_BATTLE_JSON_NAMES_LIST = List.of("User1", "User2", "Fight outcome", "Rounds played", "Detailed Report");


    @Override
    @Test
    public void integrationTest() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String kienboecToken = getSecurityTokenForUserAndPassword("kienboec", "daniel");
            String altenhofToken = getSecurityTokenForUserAndPassword("altenhof", "markus");

            // packages
            createAdminPackages(mapper);

            buyAdminPackages(mapper, kienboecToken, altenhofToken);

            List<String> kienboecDeckIds = new ArrayList<>();
            List<String> altenhofDeckIds = new ArrayList<>();

            // cards - get stack, add first 4 cards to list so those cards can be added to the users deck
            getStackAndAddFirst4ForPotentialDeckCards(mapper, kienboecToken, altenhofToken, kienboecDeckIds, altenhofDeckIds);

            // deck
            testGetAndSetDeckKienboec(mapper, kienboecToken, kienboecDeckIds);

            testGetAndSetDeckAltenhof(mapper, altenhofToken, altenhofDeckIds);

            testGetDeckFormatPlain(mapper, altenhofToken);

            // stats
            testGetStatsAltenhof(mapper, altenhofToken);

            // scoreboard
            testGetScoreboard(mapper, altenhofToken);

            // battle
            testGame(mapper, kienboecToken, altenhofToken);

        } catch (IOException | InterruptedException e) {
            fail("Exception occurred during test runtime.");
            e.printStackTrace();
        }
    }

    private Runnable getBattleRunnable(String token, CompletableFuture<HttpResponse<String>> responseFuture) {
        return () -> {
            try {
                HttpRequest request;
                HttpResponse<String> response;
                request = getHttpGetRequestSecurityToken("battles", token);
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                printRequestAndResponseToConsole(request.toString(), request.headers().toString(), "", response.body());
                responseFuture.complete(response);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        };
    }

    private void testGame(ObjectMapper mapper, String kienboecToken, String altenhofToken) throws IOException, InterruptedException {
        HttpResponse<String> response1, response2, response3;
        JsonNode responseObj;
        JsonNode jsonNode;
        List<String> fieldNames;
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(3);
            // altenhof first entry
            CompletableFuture<HttpResponse<String>> altenhof1ResponseWrapper = new CompletableFuture<>();
            executorService.submit(getBattleRunnable(altenhofToken, altenhof1ResponseWrapper));

            Thread.sleep(250);
            // altenhof second entry -> second time in queue, results in error
            CompletableFuture<HttpResponse<String>> altenhof2ResponseWrapper = new CompletableFuture<>();
            executorService.submit(getBattleRunnable(altenhofToken, altenhof2ResponseWrapper));

            Thread.sleep(250);
            CompletableFuture<HttpResponse<String>> kienboec1ResponseWrapper = new CompletableFuture<>();
            executorService.submit(getBattleRunnable(kienboecToken, kienboec1ResponseWrapper));

            response1 = altenhof1ResponseWrapper.get();
            assertThat(response1.statusCode()).isEqualTo(200);
            assertThat(response1.body()).isNotEmpty();
            responseObj = mapper.readTree(response1.body());
            jsonNode = responseObj.get("Battle");
            assertThat(jsonNode.size()).isEqualTo(5);
            fieldNames = this.namesIteratorToList(jsonNode.fieldNames());
            assertThat(fieldNames.containsAll(GAME_BATTLE_JSON_NAMES_LIST)).isTrue();

            response2 = altenhof2ResponseWrapper.get();
            assertThat(response2.statusCode()).isEqualTo(400);
            assertThat(response2.body()).isEqualTo("User is already queued in another connection.");

            response3 = kienboec1ResponseWrapper.get();
            assertThat(response3.statusCode()).isEqualTo(200);
            assertThat(response3.body()).isNotEmpty();
            responseObj = mapper.readTree(response3.body());
            jsonNode = responseObj.get("Battle");
            assertThat(jsonNode.size()).isEqualTo(5);
            fieldNames = this.namesIteratorToList(jsonNode.fieldNames());
            assertThat(fieldNames.containsAll(GAME_BATTLE_JSON_NAMES_LIST)).isTrue();

            executorService.awaitTermination(1, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void testGetScoreboard(ObjectMapper mapper, String altenhofToken) throws IOException, InterruptedException {
        JsonNode responseObj;
        JsonNode jsonNode;
        HttpRequest request;
        HttpResponse<String> response;
        request = getHttpGetRequestSecurityToken("score", altenhofToken);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), "", response.body());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotEmpty();
        responseObj = mapper.readTree(response.body());
        jsonNode = responseObj.get("Scoreboard");
        jsonNode = jsonNode.get("1.").get("User").get("Statistics");
        assertThat(jsonNode.size()).isEqualTo(5);
        List<String> fieldNames = this.namesIteratorToList(jsonNode.fieldNames());
        assertThat(fieldNames.containsAll(GAME_USER_SCORE_STATS_JSON_NAMES_LIST)).isTrue();
    }

    private void testGetStatsAltenhof(ObjectMapper mapper, String altenhofToken) throws IOException, InterruptedException {
        JsonNode responseObj;
        JsonNode jsonNode;
        HttpRequest request;
        HttpResponse<String> response;
        List<String> fieldNames;
        request = getHttpGetRequestSecurityToken("stats", altenhofToken);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), "", response.body());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotEmpty();
        responseObj = mapper.readTree(response.body());
        jsonNode = responseObj.get("altenhof");
        assertThat(jsonNode.size()).isEqualTo(1);
        jsonNode = jsonNode.get("Statistics");
        assertThat(jsonNode.size()).isEqualTo(6);
        fieldNames = this.namesIteratorToList(jsonNode.fieldNames());
        assertThat(fieldNames.containsAll(GAME_USER_STATS_JSON_NAMES_LIST)).isTrue();
    }

    private void testGetDeckFormatPlain(ObjectMapper mapper, String altenhofToken) throws IOException, InterruptedException {
        HttpRequest request;
        HttpResponse<String> response;
        JsonNode responseObj;
        JsonNode jsonNode;
        List<String> fieldNames;
        request = getHttpGetRequestSecurityToken("deck?format=plain", altenhofToken);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), "", response.body());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotEmpty();
        responseObj = mapper.readTree(response.body());
        fieldNames = this.namesIteratorToList(responseObj.fieldNames());
        assertThat(fieldNames.containsAll(GAME_GET_OR_SET_DECK_STACK_JSON_NAMES_LIST)).isTrue();
        jsonNode = responseObj.get("Deck");
        assertThat(jsonNode.size()).isEqualTo(4);
    }

    private void testGetAndSetDeckAltenhof(ObjectMapper mapper, String altenhofToken, List<String> altenhofDeckIds) throws IOException, InterruptedException {
        testGetAndSetDeck(mapper, altenhofToken, altenhofDeckIds);
    }

    private void testGetAndSetDeckKienboec(ObjectMapper mapper, String kienboecToken, List<String> kienboecDeckIds) throws IOException, InterruptedException {
        testGetAndSetDeck(mapper, kienboecToken, kienboecDeckIds);
    }

    private void testGetAndSetDeck(ObjectMapper mapper, String token, List<String> toSetDeckIds) throws IOException, InterruptedException {
        HttpRequest request;
        HttpResponse<String> response;
        JsonNode responseObj;
        JsonNode jsonNode;
        String requestBody;
        List<String> fieldNames;

        request = getHttpGetRequestSecurityToken("deck", token);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), "", response.body());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotEmpty();
        responseObj = mapper.readTree(response.body());
        fieldNames = this.namesIteratorToList(responseObj.fieldNames());
        assertThat(fieldNames.containsAll(GAME_GET_OR_SET_DECK_STACK_JSON_NAMES_LIST)).isTrue();
        jsonNode = responseObj.get("Deck");
        assertThat(jsonNode.size()).isEqualTo(4);

        StringBuilder deckIds = new StringBuilder("[");
        for (int i = 0; i < toSetDeckIds.size(); i++) {
            deckIds.append("\"").append(toSetDeckIds.get(i)).append("\"");
            if (i < toSetDeckIds.size() - 1) deckIds.append(", ");
        }
        deckIds.append("]");
        requestBody = deckIds.toString();
        request = getHttpPutRequest("deck", List.of("Content-Type", "application/json", AUTORISATION_HEADER_KEY, token), requestBody);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), requestBody, response.body());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotEmpty();
        responseObj = mapper.readTree(response.body());
        fieldNames = this.namesIteratorToList(responseObj.fieldNames());
        assertThat(fieldNames.containsAll(GAME_GET_OR_SET_DECK_STACK_JSON_NAMES_LIST)).isTrue();
        jsonNode = responseObj.get("Deck");
        assertThat(jsonNode.size()).isEqualTo(4);

        request = getHttpGetRequestSecurityToken("deck", token);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), "", response.body());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotEmpty();
        responseObj = mapper.readTree(response.body());
        fieldNames = this.namesIteratorToList(responseObj.fieldNames());
        assertThat(fieldNames.containsAll(GAME_GET_OR_SET_DECK_STACK_JSON_NAMES_LIST)).isTrue();
        jsonNode = responseObj.get("Deck");
        assertThat(jsonNode.size()).isEqualTo(4);
        List<String> deckIdsList = new ArrayList<>();
        for (JsonNode childNode : jsonNode) {
            deckIdsList.add(childNode.get("Card").get("Id").asText());
        }
        assertThat(toSetDeckIds.containsAll(deckIdsList)).isTrue();
    }

    private void getStackAndAddFirst4ForPotentialDeckCards(ObjectMapper mapper, String kienboecToken, String altenhofToken, List<String> kienboecDeckIds, List<String> altenhofDeckIds) throws IOException, InterruptedException {
        JsonNode jsonNode;
        HttpResponse<String> response;
        JsonNode responseObj;
        HttpRequest request;
        List<String> fieldNames;
        request = getHttpGetRequestSecurityToken("cards", kienboecToken);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), "", response.body());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotEmpty();
        responseObj = mapper.readTree(response.body());
        fieldNames = this.namesIteratorToList(responseObj.fieldNames());
        assertThat(fieldNames.containsAll(GAME_GET_STACK_JSON_NAMES_LIST)).isTrue();
        jsonNode = responseObj.get("Stack");
        assertThat(jsonNode.size()).isGreaterThanOrEqualTo(5);
        for (JsonNode objNode : jsonNode) {
            String cardId = objNode.get("Card").get("Id").asText();
            if (isNotUsedInTradeIntegrationTest(cardId)) {
                kienboecDeckIds.add(cardId);
            }
            if (kienboecDeckIds.size() == 4) break;
        }

        request = getHttpGetRequestSecurityToken("cards", altenhofToken);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), "", response.body());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotEmpty();
        responseObj = mapper.readTree(response.body());
        fieldNames = this.namesIteratorToList(responseObj.fieldNames());
        assertThat(fieldNames.containsAll(GAME_GET_STACK_JSON_NAMES_LIST)).isTrue();
        jsonNode = responseObj.get("Stack");
        assertThat(jsonNode.size()).isGreaterThanOrEqualTo(5);
        for (JsonNode objNode : jsonNode) {
            String cardId = objNode.get("Card").get("Id").asText();
            if (isNotUsedInTradeIntegrationTest(cardId)) {
                altenhofDeckIds.add(cardId);
            }
            if (altenhofDeckIds.size() == 4) break;
        }
    }

    /**
     * Those two ids are used in the Trade integration test, so the cards should not be used in the deck
     */
    private boolean isNotUsedInTradeIntegrationTest(String cardId) {
        return !cardId.equals("51441205-5966-4c27-9944-5ba43cb25eb9") && !cardId.equals("f4bc8718-0cfa-4156-b2f6-598e634a5b61");
    }

    private void buyAdminPackages(ObjectMapper mapper, String kienboecToken, String altenhofToken) throws IOException, InterruptedException {
        HttpRequest request;
        HttpResponse<String> response;
        JsonNode responseObj;
        JsonNode jsonNode;
        List<String> fieldNames;
        request = getHttpPostRequest("packages?buy_admin_package=true", List.of("Content-Type", "application/json", "Authorization", kienboecToken), "");
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), "", response.body());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotEmpty();
        responseObj = mapper.readTree(response.body());
        fieldNames = this.namesIteratorToList(responseObj.fieldNames());
        assertThat(fieldNames.containsAll(GAME_ADD_OR_BUY_ADMIN_PACKAGE_JSON_NAMES_LIST)).isTrue();
        jsonNode = responseObj.get("Package");
        assertThat(jsonNode.isArray()).isTrue();
        assertThat(jsonNode.size()).isEqualTo(5);


        request = getHttpPostRequest("packages?buy_admin_package=true", List.of("Content-Type", "application/json", "Authorization", altenhofToken), "");
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), "", response.body());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotEmpty();
        responseObj = mapper.readTree(response.body());
        fieldNames = this.namesIteratorToList(responseObj.fieldNames());
        assertThat(fieldNames.containsAll(GAME_ADD_OR_BUY_ADMIN_PACKAGE_JSON_NAMES_LIST)).isTrue();
        jsonNode = responseObj.get("Package");
        assertThat(jsonNode.isArray()).isTrue();
        assertThat(jsonNode.size()).isEqualTo(5);
    }

    private void createAdminPackages(ObjectMapper mapper) throws IOException, InterruptedException {
        HttpRequest request;
        HttpResponse<String> response;
        JsonNode responseObj;
        JsonNode jsonNode;
        String requestBody;
        List<String> fieldNames;
        requestBody = "[{\"Id\":\"" + UUID.randomUUID() + "\", \"Name\":\"Grey Goblin\", \"Damage\": 10.0}, " +
                "{\"Id\":\"" + UUID.randomUUID() + "\", \"Name\":\"Dragon\", \"Damage\": 50.0}, " +
                "{\"Id\":\"" + UUID.randomUUID() + "\", \"Name\":\"Water Spell\", \"Damage\": 20.0}, " +
                "{\"Id\":\"" + UUID.randomUUID() + "\", \"Name\":\"Orc\", \"Damage\": 45.0}, " +
                "{\"Id\":\"" + UUID.randomUUID() + "\", \"Name\":\"Fire Spell\", \"Damage\": 25.0}]";
        request = getHttpPostRequest("packages?add_admin_package=true", List.of("Content-Type", "application/json", "Authorization", "admin-token-1234"),
                requestBody);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), requestBody, response.body());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotEmpty();
        responseObj = mapper.readTree(response.body());
        fieldNames = this.namesIteratorToList(responseObj.fieldNames());
        assertThat(fieldNames.containsAll(GAME_ADD_OR_BUY_ADMIN_PACKAGE_JSON_NAMES_LIST)).isTrue();
        jsonNode = responseObj.get("Package");
        assertThat(jsonNode.isArray()).isTrue();
        assertThat(jsonNode.size()).isEqualTo(5);


        requestBody = "[{\"Id\":\"" + UUID.randomUUID() + "\", \"Name\":\"Kraken\", \"Damage\": 10.0}, " +
                "{\"Id\":\"" + UUID.randomUUID() + "\", \"Name\":\"Orc\", \"Damage\": 50.0}, " +
                "{\"Id\":\"" + UUID.randomUUID() + "\", \"Name\":\"Water Spell\", \"Damage\": 20.0}, " +
                "{\"Id\":\"" + UUID.randomUUID() + "\", \"Name\":\"Speed Spell\", \"Damage\": 45.0}, " +
                "{\"Id\":\"" + UUID.randomUUID() + "\", \"Name\":\"Dark Spell\", \"Damage\": 25.0}]";
        request = getHttpPostRequest("packages?add_admin_package=true", List.of("Content-Type", "application/json", "Authorization", "admin-token-1234"),
                requestBody);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), requestBody, response.body());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotEmpty();
        responseObj = mapper.readTree(response.body());
        fieldNames = this.namesIteratorToList(responseObj.fieldNames());
        assertThat(fieldNames.containsAll(GAME_ADD_OR_BUY_ADMIN_PACKAGE_JSON_NAMES_LIST)).isTrue();
        jsonNode = responseObj.get("Package");
        assertThat(jsonNode.isArray()).isTrue();
        assertThat(jsonNode.size()).isEqualTo(5);
    }
}
