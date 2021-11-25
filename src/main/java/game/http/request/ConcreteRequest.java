package game.http.request;

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
    private String body;


    public ConcreteRequest() {
        headers = new HashMap<>();
    }


    public ConcreteRequest(InputStream inputStream) {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(inputStream))) {
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
        Map<String, String> headers = new HashMap<>();
        try {
            while ((line = streamReader.readLine()) != null) {
                if (line.isBlank()) break;//Stop loop when end of header is reached
                if (!line.matches(".+:.+")) {
                    System.out.println(line);
                    continue;//Skip this Line if it doesnt match the regex
                }
                String[] headerSegments = line.split(":");
                headers.put(headerSegments[0].toLowerCase(), headerSegments[1].trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return headers;
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
        StringBuilder body = new StringBuilder(10000);
        char[] buffer = new char[1024];
        int length = 0;
        int bufferLength;
        try {
            while ((bufferLength = streamReader.read(buffer)) != -1) {
                body.append(buffer, 0, bufferLength);
                length += bufferLength;
                if (length >= contentLength)
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return body.toString();
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
        return headers.getOrDefault("user-agent", null);
    }

    @Override
    public int getContentLength() {
        if (headers.containsKey("content-length"))
            return Integer.parseInt(headers.get("content-length"));
        return 0;
    }

    @Override
    public String getContentType() {
        return headers.getOrDefault("content-type", null);
    }

    @Override
    public InputStream getContentStream() {
        return new ByteArrayInputStream(getContentString().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String getContentString() {
        return this.body;
    }

    @Override
    public byte[] getContentBytes() {
        return this.body.getBytes(StandardCharsets.UTF_8);
    }

    public void addToHeader(String name, String value) {
        this.headers.put(name, value);
    }
}
