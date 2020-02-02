package HttpLib;

/**
 * A glorified string wrapper for the HTTP message body.
 * Counts message length
 */
public class HttpRequestBody {

    int length = 0;
    String body = "";

    public HttpRequestBody() {
    }

    public HttpRequestBody(String body) {
        this.body = body;
        this.length = body.length();
    }

    public String toString() {
        return body;
    }

    public String getLengthString() {
        return "Content-Length: " + body.length() + "\r\n";
    }

    public boolean isValid() {
        return body.length() > 0;
    }
}
