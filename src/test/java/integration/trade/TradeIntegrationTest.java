package integration.trade;

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

public class TradeIntegrationTest extends MonsterCardIntegrationTest {

    private static final List<String> TRADE_TRADE_CREATED_JSON_NAMES_LIST = List.of("Id", "TradeFrom", "Trading", "TradingForCard", "TradingForCoins", "TradeFinished");
    private static final List<String> TRADE_TRADE_FINISHED_JSON_NAMES_LIST = List.of("Id", "TradeFrom", "Trading", "TradingForCard", "TradingForCoins", "TradeFinished", "TradedTo", "TradedFor", "TradedAt");

    @Override
    @Test
    public void integrationTest() {
        ObjectMapper mapper = new ObjectMapper();
        HttpResponse<String> response;
        JsonNode responseObj;
        List<String> fieldNames;
        String requestBody;
        HttpRequest request;
        String tradeId;
        try {
            String kienboecToken = getSecurityTokenForUserAndPassword("kienboec", "daniel");
            String altenhofToken = getSecurityTokenForUserAndPassword("altenhof", "markus");
            // check trades
            checkTrades(kienboecToken, altenhofToken);

            // create and finish trade
            createAndFinishTrade(mapper, kienboecToken, altenhofToken);

            // create and remove trade
            createAndRemoveTrade(mapper, kienboecToken, altenhofToken);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail("Exception occurred during test runtime.");
        }
    }

    private void createAndRemoveTrade(ObjectMapper mapper, String kienboecToken, String altenhofToken) throws IOException, InterruptedException {
        List<String> fieldNames;
        String tradeId;
        HttpRequest request;
        HttpResponse<String> response;
        String requestBody;
        JsonNode responseObj;
        // create trade
        requestBody = "{\"CardToTrade\": \"f4bc8718-0cfa-4156-b2f6-598e634a5b61\", \"DesiredCardName\": \"ork\", \"DesiredCoins\": 2}";
        request = getHttpPostRequest("tradings", List.of("Content-Type", "application/json", "Authorization", kienboecToken),
                requestBody);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), requestBody, response.body());
        responseObj = mapper.readTree(response.body());
        tradeId = responseObj.get("Trade").get("Id").asText();
        assertThat(response.statusCode()).isEqualTo(200);
        fieldNames = this.namesIteratorToList(responseObj.get("Trade").fieldNames());
        assertThat(fieldNames.containsAll(TRADE_TRADE_CREATED_JSON_NAMES_LIST)).isTrue();

        // delete trade wrong token -> error
        request = getHttpDeleteRequest("tradings/" + tradeId, List.of("Content-Type", "application/json", "Authorization", altenhofToken));
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), requestBody, response.body());
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.body()).isEqualTo("You can only remove your own trades.");

        // delete trade
        request = getHttpDeleteRequest("tradings/" + tradeId, List.of("Content-Type", "application/json", "Authorization", kienboecToken));
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), requestBody, response.body());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("Trade removed successfully");
    }

    private void createAndFinishTrade(ObjectMapper mapper, String kienboecToken, String altenhofToken) throws IOException, InterruptedException {
        HttpResponse<String> response;
        JsonNode responseObj;
        List<String> fieldNames;
        String requestBody;
        HttpRequest request;
        String tradeId;
        // create trade
        requestBody = "{\"CardToTrade\": \"f4bc8718-0cfa-4156-b2f6-598e634a5b61\", \"DesiredCardName\": \"ork\", \"DesiredCoins\": 2}";
        request = getHttpPostRequest("tradings", List.of("Content-Type", "application/json", "Authorization", altenhofToken),
                requestBody);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), requestBody, response.body());
        responseObj = mapper.readTree(response.body());
        tradeId = responseObj.get("Trade").get("Id").asText();
        assertThat(response.statusCode()).isEqualTo(200);
        fieldNames = this.namesIteratorToList(responseObj.get("Trade").fieldNames());
        assertThat(fieldNames.containsAll(TRADE_TRADE_CREATED_JSON_NAMES_LIST)).isTrue();

        // finish trade with yourself - error
        requestBody = "\"1d94ced0-bfb1-4a5b-8e0f-a6bb23555e4a\"";
        request = getHttpPostRequest("tradings/" + tradeId, List.of("Content-Type", "application/json", "Authorization", altenhofToken),
                requestBody);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), requestBody, response.body());
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.body()).isEqualTo("It is not possible to trade with yourself!");

        // finish wrong card
        requestBody = "\"2874b445-1ee1-4878-9509-658c32102942\"";
        request = getHttpPostRequest("tradings/" + tradeId, List.of("Content-Type", "application/json", "Authorization", kienboecToken),
                requestBody);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), requestBody, response.body());
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.body()).isEqualTo("The selected card is not suited for this trade. Either the card is involved in another trade or is selected as deck card or does not match the desired card from this trade.");


        // finish trade
        requestBody = "\"51441205-5966-4c27-9944-5ba43cb25eb9\"";
        request = getHttpPostRequest("tradings/" + tradeId, List.of("Content-Type", "application/json", "Authorization", kienboecToken),
                requestBody);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), requestBody, response.body());
        assertThat(response.statusCode()).isEqualTo(200);
        responseObj = mapper.readTree(response.body());
        fieldNames = this.namesIteratorToList(responseObj.get("Trade").fieldNames());
        assertThat(fieldNames.containsAll(TRADE_TRADE_FINISHED_JSON_NAMES_LIST)).isTrue();

        // create trade
        requestBody = "{\"CardToTrade\": \"f4bc8718-0cfa-4156-b2f6-598e634a5b61\", \"DesiredCardName\": \"ork\", \"DesiredCoins\": 2}";
        request = getHttpPostRequest("tradings", List.of("Content-Type", "application/json", "Authorization", kienboecToken),
                requestBody);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), requestBody, response.body());
        responseObj = mapper.readTree(response.body());
        tradeId = responseObj.get("Trade").get("Id").asText();
        assertThat(response.statusCode()).isEqualTo(200);
        fieldNames = this.namesIteratorToList(responseObj.get("Trade").fieldNames());
        assertThat(fieldNames.containsAll(TRADE_TRADE_CREATED_JSON_NAMES_LIST)).isTrue();

        // finish trade
        requestBody = "\"51441205-5966-4c27-9944-5ba43cb25eb9\"";
        request = getHttpPostRequest("tradings/" + tradeId, List.of("Content-Type", "application/json", "Authorization", altenhofToken),
                requestBody);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), requestBody, response.body());
        assertThat(response.statusCode()).isEqualTo(200);
        responseObj = mapper.readTree(response.body());
        fieldNames = this.namesIteratorToList(responseObj.get("Trade").fieldNames());
        assertThat(fieldNames.containsAll(TRADE_TRADE_FINISHED_JSON_NAMES_LIST)).isTrue();
    }

    private void checkTrades(String kienboecToken, String altenhofToken) throws IOException, InterruptedException {
        HttpResponse<String> response;
        HttpRequest request;
        request = getHttpGetRequestSecurityToken("tradings", kienboecToken);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), "", response.body());
        String body1 = response.body();
        assertThat(response.statusCode()).isEqualTo(200);

        request = getHttpGetRequestSecurityToken("tradings", altenhofToken);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        printRequestAndResponseToConsole(request.toString(), request.headers().toString(), "", response.body());
        String body2 = response.body();
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(body1).isEqualTo(body2);
    }
}


