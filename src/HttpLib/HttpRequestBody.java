package HttpLib;


// TODO: Should receive string,
public class HttpRequestBody {

    int length = 0;
    String body;

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
