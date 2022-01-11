package game.http.response;

import game.http.enums.StatusCodeEnum;

import java.io.InputStream;
import java.io.OutputStream;

public interface Response {
    /**
     * @return Gets the content type of the response.
     */
    String getContentType();

    /**
     * @return Returns the content
     */
    String getContent();

    /**
     * @param contentType Sets the content type of the response.
     * @throws IllegalStateException A specialized implementation may throw a
     *                               InvalidOperationException when the content type is set by the
     *                               implementation.
     */
    void setContentType(String contentType);

    /**
     * @return Gets the current status code
     */
    int getStatusCode();

    /**
     * @param status Sets the current status code.
     */
    void setStatusCode(int status);

    /**
     * @param status Sets the current status code.
     */
    void setStatus(StatusCodeEnum status);

    /**
     * @return Returns the status code as string. (200 OK)
     */
    StatusCodeEnum getStatus();

    /**
     * Adds or replaces a response header in the headers map
     *
     * @param header
     * @param value
     */
    void addHeader(String header, String value);

    /**
     * @param content Sets a string content. The content will be encoded in UTF-8.
     */
    void setContent(String content);

    /**
     * @param content Sets a byte[] as content.
     */
    void setContent(byte[] content);

    /**
     * @param stream Sets the stream as content.
     */
    void setContent(InputStream stream);

    /**
     * @param outputStream Sends the response to the stream.
     */
    void send(OutputStream outputStream);
}
