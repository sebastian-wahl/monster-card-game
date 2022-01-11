package game.http.url;

import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public class ConcreteUrl implements Url {
    private String url;
    @Getter
    private PathEnum urlPath;
    @Getter
    private List<String> urlSegments;
    @Getter
    private Map<String, String> urlParameters;

    public ConcreteUrl(String url) {
        this.url = url;
        this.urlParameters = new HashMap<>();
        splitUrl(url);
    }

    public void splitUrl(String url) {
        if (url.contains("?")) {
            String[] urlPathParams = url.split("\\?");
            readUrlSegments(urlPathParams[0]);
            urlPath = this.getPathEnumFromString(this.urlSegments.get(0));
            readUrlParams(urlPathParams[1]);
        } else {
            readUrlSegments(url);
            urlPath = this.getPathEnumFromString(this.urlSegments.get(0));
        }
    }

    private PathEnum getPathEnumFromString(String path) {
        try {
            return PathEnum.valueOf(path.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return PathEnum.NOMATCH;
        }
    }

    public void readUrlSegments(String urlPath) {
        List<String> segmentsWithEmpty = new ArrayList<>(Arrays.asList(urlPath.split("/")));
        urlSegments = segmentsWithEmpty.stream().filter(segment -> !segment.isEmpty()).collect(Collectors.toList());
    }

    public void readUrlParams(String params) {
        String[] urlSegments = params.split("&");
        for (String urlSegment : urlSegments) {
            String[] paramNameValuePair = urlSegment.split("=");
            urlParameters.put(paramNameValuePair[0].toLowerCase(), paramNameValuePair[1].trim());
        }
    }

    @Override
    public String getRawUrl() {
        return this.url;
    }
}
