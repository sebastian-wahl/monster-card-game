package game.http.response;

import game.http.StatusCodeHelper;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static game.server.Server.DEFAUlT_SERVER_NAME;

public class ConcreteResponse implements Response {

    @Getter
    private Map<String, String> headers;
    @Getter
    private String status;
    @Getter
    @Setter
    private int statusCode;
    @Getter
    private String content;

    public ConcreteResponse() {
        headers = new HashMap<>();
    }


    @Override
    public int getContentLength() {
        if (headers.containsKey(CONTENT_LENGTH_KEY))
            return Integer.parseInt(headers.get(CONTENT_LENGTH_KEY));
        else
            return 0;
    }

    @Override
    public String getContentType() {
        return headers.getOrDefault(CONTENT_TYPE_KEY, null);
    }


    @Override
    public void setContentType(String contentType) {
        headers.put(CONTENT_TYPE_KEY, contentType);
    }

    @Override
    public void addHeader(String header, String value) {
        this.headers.put(header, value);
    }

    @Override
    public String getServerHeader() {
        return headers.getOrDefault(SERVER_NAME_KEY, DEFAUlT_SERVER_NAME);
    }


    @Override
    public void setServerHeader(String server) {
        headers.put(SERVER_NAME_KEY, server);
    }

    @Override
    public void setContent(String content) {
        this.content = content;
        if ((content.startsWith("{") && content.endsWith("}")) || (content.startsWith("[") && content.endsWith("]")))
            setContentType(CONTENT_TYPE_APPLICATION_JSON);
        else if (!this.content.equals("")) {
            setContentType(CONTENT_TYPE_TEXT_PLAIN);
        }
        if (this.content.equals(""))
            setStatus(204);
    }

    @Override
    public void setContent(byte[] content) {
        this.content = new String(content);
        if ((this.content.startsWith("{") && this.content.endsWith("}")) || (this.content.startsWith("[") && this.content.endsWith("]")))
            setContentType(CONTENT_TYPE_APPLICATION_JSON);
        else if (!this.content.equals("")) {
            setContentType(CONTENT_TYPE_TEXT_PLAIN);
        }
        if (this.content.equals(""))
            setStatus(204);
    }

    @Override
    public void setContent(InputStream stream) {
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader(stream, Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
            content = textBuilder.toString();
            if ((content.startsWith("{") && content.endsWith("}")) || (content.startsWith("[") && content.endsWith("]")))
                setContentType(CONTENT_TYPE_APPLICATION_JSON);
            else if (!content.equals("")) {
                setContentType(CONTENT_TYPE_TEXT_PLAIN);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (content.equals(""))
            setStatus(204);
    }

    public void setStatus(int statusCode) {
        String statusString = StatusCodeHelper.getStringStatusFromCode(statusCode);
        status = statusCode + " " + statusString;
    }

    @Override
    public void send(OutputStream outputStream) {
        try {
            String responseString = this.buildResponse();
            outputStream.write(responseString.getBytes());
            System.out.println("Response send: \n" + responseString);
        } catch (IOException e) {
            System.out.println("Error when sending the response.");
            e.printStackTrace();
        }
    }

    /**
     * Builds the response
     *
     * @return returns this object as a string
     */
    public String buildResponse() {
        StringBuilder response = new StringBuilder();
        response.append("HTTP-Version = HTTP/1.1 ").append(status).append("\n");
        if (!headers.isEmpty())
            headers.forEach((k, v) -> response.append(k).append(": ").append(v).append("\n"));
        response.append("\n");
        response.append(content);
        return response.toString();
    }
}
