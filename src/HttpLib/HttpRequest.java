package HttpLib;
import java.net.URL;

/**
 * The entire request object that can be either send and received.
 * Can check for its own validity.
 */
public class HttpRequest {

    HttpMessageUrl url;
    HttpRequestMethod requestMethod;
    HttpMessageHeader messageHeader;
    HttpRequestBody body;

    public HttpRequest(URL url, HttpRequestMethod requestMethod, HttpMessageHeader messageHeader, HttpRequestBody body) {
        this.url = new HttpMessageUrl(url);
        this.requestMethod = requestMethod;
        this.messageHeader = messageHeader;
        this.body = body;
    }


    public String toString() {
        String request = requestMethod.toString() + url.getFileAndQuery() + "HTTP/1.0\r\n"
                        + messageHeader.toString()
                        + body.getLengthString()
                        + "\r\n"
                        + body;
        return request;
    }

    public boolean isValid() {
        if(requestMethod == null) {
            return false;
        }
        if (requestMethod.equals(HttpRequestMethod.POST)) {
            return body.isValid();
        }
        if (!messageHeader.isValid()) {
            return false;
        }
        return true;
    }

    public byte[] toBytes(){
        return this.toString().getBytes();
    }


}
