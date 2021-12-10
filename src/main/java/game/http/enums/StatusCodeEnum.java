package game.http.enums;

public enum StatusCodeEnum {
    SC_100("Continue"),
    SC_101("Switching Protocols"),
    SC_200("OK"),
    SC_201("Created"),
    SC_202("Accepted"),
    SC_203("Non-authoritative Information"),
    SC_204("No Content"),
    SC_205("Reset Content"),
    SC_206("Partial Content"),
    SC_300("Multiple Choices"),
    SC_301("Moved Permanently"),
    SC_302("Found"),
    SC_303("See Other"),
    SC_304("Not Modified"),
    SC_305("Use Proxy"),
    SC_306("Unused"),
    SC_307("Temporary Redirect"),
    SC_400("Bad Request"),
    SC_401("Unauthorized"),
    SC_402("Payment Required"),
    SC_403("Forbidden"),
    SC_404("Not Found"),
    SC_405("Method Not Allowed"),
    SC_406("Not Acceptable"),
    SC_407("Proxy Authentication Required"),
    SC_408("Request Timeout"),
    SC_409("Conflict"),
    SC_410("Gone"),
    SC_411("Length Required"),
    SC_412("Precondition Failed"),
    SC_413("Request Entity Too Large"),
    SC_414("Request-url Too Long"),
    SC_415("Unsupported Media Type"),
    SC_416("Requested Range Not Satisfiable"),
    SC_417("Expectation Failed"),
    SC_500("Internal Server Error");


    private String desc;

    StatusCodeEnum(String desc) {
        this.desc = desc;
    }

    private String getString() {
        return this.name().substring(3);
    }

    public int toInt() {
        return Integer.parseInt(this.getString());
    }

    @Override
    public String toString() {
        return this.getString() + " " + this.desc;
    }
}
