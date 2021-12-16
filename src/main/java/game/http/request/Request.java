package game.http.request;


import game.http.HttpMethod;
import game.http.HttpReady;
import game.http.models.HttpModel;
import game.http.url.Url;

import java.io.InputStream;
import java.util.Map;

public interface Request extends HttpReady {
    /**
     * @return Returns true if the request is valid. A request is valid, if
     * method and url could be parsed. A header is not necessary.
     */
    boolean isValid();

    /**
     * @return Returns the request methode.
     */
    HttpMethod getMethod();

    /**
     * @return Returns the model with data from the request body mapped to it
     */
    HttpModel getModel();

    /**
     * @return Returns a URL object of the request. Never returns null.
     */
    Url getUrl();

    /**
     * @return Returns the request header. Never returns null. All keys must be
     * lower case.
     */
    Map<String, String> getHeaders();

    /**
     * @return Returns the number of header or 0, if no header where found.
     */
    int getHeaderCount();

    /**
     * @return Returns the parsed content length request header. Never returns
     * null.
     */
    int getContentLength();

    /**
     * @return Returns the parsed content type request header. Never returns
     * null.
     */
    String getContentType();

    /**
     * @return Returns the request content (body) stream or null if there is no
     * content stream.
     */
    InputStream getContentStream();

    /**
     * @return Returns the request content (body) as s string or null if empty
     * no content.
     */
    String getContent();

    /**
     * @return Returns the request content (body) as byte[] or null if there is
     * no content.
     */
    byte[] getContentBytes();
}
