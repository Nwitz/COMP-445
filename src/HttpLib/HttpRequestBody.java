package HttpLib;

/**
 * A glorified string wrapper for the HTTP message body.
 * Counts message length
 */
public class HttpRequestBody {

    private int length = 0;
    private String body = "";

    public HttpRequestBody() {
    }

    public HttpRequestBody(String body) {
        this.body = body;
        this.length = body.length();
    }

    public String toString() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
        length = body.length();
    }

    public int getLength() {
        return body.length();
    }

    public boolean isValid() {
        return body.length() > 0;
    }
}
