package game.http.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import game.http.HttpMethod;
import game.http.url.ConcreteUrl;
import game.http.url.Url;
import game.objects.exceptions.http.UnsupportedHttpMethod;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ConcreteRequest implements Request {

    @Setter
    @Getter
    private HttpMethod method;
    @Getter
    private Url url;
    @Getter
    private Map<String, String> headers;
    private Map<String, String> body;


    public ConcreteRequest() {
        headers = new HashMap<>();
    }


    public ConcreteRequest(InputStream inputStream) {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));
            String firstLine = input.readLine();
            method = readHttpMethod(firstLine).orElse(HttpMethod.EMPTY);
            url = readUrl(firstLine);
            headers = readHttpHeader(input);
            body = readBody(input, getContentLength());
        } catch (IOException ex) {
            System.out.println("Error when reading the input stream");
            System.out.println(ex.getMessage());
        }
    }

    private Map<String, String> readHttpHeader(BufferedReader streamReader) {
        String line;
        Map<String, String> headersOut = new HashMap<>();
        try {
            while ((line = streamReader.readLine()) != null) {
                if (line.isBlank()) break;//Stop loop when end of header is reached

                String[] headerSegments = line.split(":");
                // .toLowerCase() for simplicity
                headersOut.put(headerSegments[0].toLowerCase(), headerSegments[1].trim());
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

    private Map<String, String> readBody(BufferedReader streamReader, int contentLength) {
        if (contentLength == 0)
            return Map.of();

        StringBuilder bodyString = new StringBuilder();
        char[] content = new char[contentLength];
        try {
            if (streamReader.read(content) != -1) {
                // succ
                bodyString.append(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this.getStringAsMap(bodyString.toString());
    }

    @Override
    public boolean isValid() {
        return this.method != HttpMethod.EMPTY && url != null;
    }

    @Override
    public int getHeaderCount() {
        return this.headers != null ? this.headers.size() : 0;
    }

    @Override
    public String getUserAgent() {
        return headers.getOrDefault(USERNAME_KEY, null);
    }

    @Override
    public int getContentLength() {
        if (headers.containsKey(CONTENT_LENGTH_KEY))
            return Integer.parseInt(headers.get(CONTENT_LENGTH_KEY));
        return 0;
    }

    @Override
    public String getContentType() {
        return headers.getOrDefault(CONTENT_TYPE_KEY, null);
    }

    @Override
    public InputStream getContentStream() {
        return new ByteArrayInputStream(this.getContentBytes());
    }

    @Override
    public Map<String, String> getContent() {
        return this.body;
    }

    private String getMapAsString(Map<String, String> map) {
        ObjectMapper objectMapper = new ObjectMapper();

        String mapAsString = "";
        try {
            mapAsString = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return mapAsString;
    }

    private Map<String, String> getStringAsMap(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> map;
        try {
            // convert JSON string to Map
            map = objectMapper.readValue(jsonString, new TypeReference<Map<String, String>>() {
            });
        } catch (IOException e) {
            // Did not work, content no json -> map should stay empty
            map = Map.of();
        }
        return map;
    }

    @Override
    public byte[] getContentBytes() {
        return this.getMapAsString(this.body).getBytes(StandardCharsets.UTF_8);
    }
}
