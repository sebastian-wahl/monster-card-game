package game.http.response;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class ConcreteResponse implements Response {
    @Override
    public Map<String, String> getHeaders() {
        return null;
    }

    @Override
    public int getContentLength() {
        return 0;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public void setContentType(String contentType) {

    }

    @Override
    public int getStatusCode() {
        return 0;
    }

    @Override
    public void setStatusCode(int status) {

    }

    @Override
    public String getStatus() {
        return null;
    }

    @Override
    public void addHeader(String header, String value) {

    }

    @Override
    public String getServerHeader() {
        return null;
    }

    @Override
    public void setServerHeader(String server) {

    }

    @Override
    public void setContent(String content) {

    }

    @Override
    public void setContent(byte[] content) {

    }

    @Override
    public void setContent(InputStream stream) {

    }

    @Override
    public void send(OutputStream network) {

    }
}
