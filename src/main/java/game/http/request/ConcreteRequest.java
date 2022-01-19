package game.http.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import game.http.HttpMethod;
import game.http.models.*;
import game.http.url.ConcreteUrl;
import game.http.url.Url;
import game.objects.exceptions.http.UnsupportedHttpMethod;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static game.http.HttpReady.*;

public class ConcreteRequest implements Request {

    @Setter
    @Getter
    private HttpMethod method;
    @Getter
    private Url url;
    @Getter
    private Map<String, String> headers;
    private String body;

    @Getter
    private HttpModel model;


    private ObjectMapper o = new ObjectMapper();

    public ConcreteRequest() {
        this.headers = new HashMap<>();
    }


    public ConcreteRequest(InputStream inputStream) {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));
            String firstLine = input.readLine();
            method = readHttpMethod(firstLine).orElse(HttpMethod.EMPTY);
            url = readUrl(firstLine);
            headers = readHttpHeader(input);
            body = readBody(input, getContentLength());
            this.setHttpModelBasedOnRoute();
        } catch (IOException ex) {
            System.out.println("Error when reading the input stream");
            ex.printStackTrace();
        }
    }

    private void setHttpModelBasedOnRoute() throws JsonProcessingException {
        switch (this.url.getUrlPath()) {
            case USERS, SESSIONS:
                if (this.method == HttpMethod.POST || this.method == HttpMethod.PUT) {
                    this.model = o.readValue(this.body, UserModel.class);
                }
                break;
            case PACKAGES:
                // create package
                if (this.method == HttpMethod.POST && !this.body.isEmpty()) {
                    List<CardModel> packageCards = o.readValue(this.body, new TypeReference<List<CardModel>>() {
                    });
                    this.model = PackageModel.builder().packageCards(packageCards).build();
                }
                break;
            case DECK:
                if (this.method == HttpMethod.PUT) {
                    this.model = DeckModel.builder().ids(o.readValue(this.body, String[].class)).build();
                }
                break;
            case TRADINGS:
                if (this.method == HttpMethod.POST) {
                    if (this.url.getUrlSegments().size() == 1) {
                        this.model = o.readValue(this.body, AddTradeModel.class);
                    }
                    if (this.url.getUrlSegments().size() == 2) {
                        this.model = o.readValue(this.body, FinishTradeModel.class);
                    }
                }
                break;
            default:
                this.model = null;
        }
    }

    private Map<String, String> readHttpHeader(BufferedReader streamReader) {
        String line;
        Map<String, String> headersOut = new HashMap<>();
        try {
            while ((line = streamReader.readLine()) != null) {
                if (line.isBlank()) break;//Stop loop when end of header is reached

                String[] headerSegments = line.split(":", 2);
                // .toLowerCase() for simplicity
                headersOut.put(headerSegments[0], headerSegments[1].trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return headersOut;
    }

    private Optional<HttpMethod> readHttpMethod(String line) {
        if (line != null) {
            int splitIndex = line.indexOf(' ');
            try {
                return Optional.of(HttpMethod.valueOf(line.substring(0, splitIndex)));
            } catch (IllegalArgumentException e) {
                throw new UnsupportedHttpMethod(line.substring(0, splitIndex));
            }
        }
        return Optional.empty();
    }

    private Url readUrl(String line) {
        if (line != null) {
            int splitIndexBegin = line.indexOf(' ');
            int splitIndexEnd = line.indexOf(' ', line.indexOf(' ') + 1);
            return new ConcreteUrl(line.substring(splitIndexBegin, splitIndexEnd).trim());
        }
        return null;
    }

    private String readBody(BufferedReader streamReader, int contentLength) {
        if (contentLength == 0)
            return "";

        StringBuilder bodyString = new StringBuilder();
        char[] content = new char[contentLength];
        try {
            if (streamReader.read(content) != -1) {
                // success
                bodyString.append(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bodyString.toString();
    }

    @Override
    public String getAuthorizationToken() {
        return Objects.requireNonNullElse(this.headers.get(AUTHORIZATION_KEY.toString()), "");
    }

    @Override
    public int getContentLength() {
        if (headers.containsKey(CONTENT_LENGTH_KEY.toString()))
            return Integer.parseInt(headers.get(CONTENT_LENGTH_KEY.toString()));
        return 0;
    }

    @Override
    public String getContentType() {
        return headers.getOrDefault(CONTENT_TYPE_KEY.toString(), null);
    }

    @Override
    public String getContent() {
        return this.body;
    }
}
