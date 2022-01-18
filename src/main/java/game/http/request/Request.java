package game.http.request;


import game.http.HttpMethod;
import game.http.models.HttpModel;
import game.http.url.Url;

import java.util.Map;

public interface Request {
    /**
     * @return Returns the request methode.
     */
    HttpMethod getMethod();

    /**
     * @return Returns the model with data from the request body mapped to it
     */
    HttpModel getModel();

    /**
     * @return Returns a URL object of the request
     */
    Url getUrl();

    /**
     * @return Returns the request header. All keys must be
     * lowercase.
     */
    Map<String, String> getHeaders();

    /**
     * @return Returns the Authorization token if present, else an empty string
     */
    String getAuthorizationToken();

    /**
     * @return Returns the parsed content length request header.
     */
    int getContentLength();

    /**
     * @return Returns the parsed content type request header.
     */
    String getContentType();

    /**
     * @return Returns the request content (body) as s string or null if empty
     * no content.
     */
    String getContent();
}
