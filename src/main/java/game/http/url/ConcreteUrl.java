package game.http.url;

import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public class ConcreteUrl implements Url {
    @Getter
    private String url;
    @Getter
    private String urlPath;
    @Getter
    private List<String> urlSegments;
    @Getter
    private Map<String, String> urlParameters;
    @Getter
    private String fileName;
    @Getter
    private String fileExtension;
    @Getter
    private String fragment;

    public ConcreteUrl(String url) {
        this.url = url;
        this.urlParameters = new HashMap<>();
        splitUrl(url);

        // set defaults
        this.fileExtension = "";
        this.fileName = "";
    }

    public void splitUrl(String url) {
        if (url.contains("?")) {
            String[] urlPathParams = url.split("\\?");
            urlPath = urlPathParams[0];
            readUrlSegments(urlPathParams[0]);
            readUrlParams(urlPathParams[1]);
        } else {
            urlPath = url;
            readUrlSegments(url);
        }
        readUrlFileName(urlSegments);
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

    public void readUrlFileName(List<String> urlSegments) {
        String lastSegment = urlSegments.get(urlSegments.size() - 1);
        if (lastSegment.contains(".")) {
            fileName = lastSegment;
            fileExtension = lastSegment.split("\\.")[1];
        }
    }

    @Override
    public String getRawUrl() {
        return null;
    }


    @Override
    public int getParameterCount() {
        return this.urlParameters != null ? this.urlParameters.size() : 0;
    }

}
