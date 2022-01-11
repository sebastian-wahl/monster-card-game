package game.http.url;

import java.util.List;
import java.util.Map;

public interface Url {
    /**
     * @return Returns the raw url.
     */
    String getRawUrl();

    /**
     * @return Returns the path of the url, without parameter.
     */
    PathEnum getUrlPath();

    /**
     * @return Returns a dictionary with the parameter of the url. Never returns
     * null.
     */
    Map<String, String> getUrlParameters();

    /**
     * @return Returns the segments of the url path. A segment is divided by '/'
     * chars. Never returns null.
     */
    List<String> getUrlSegments();
}
