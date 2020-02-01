package HttpLib;


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

    public int getLength() {
        return length;
    }

}
