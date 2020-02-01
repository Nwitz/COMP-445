package HttpLib;

import HttpLib.Exceptions.HttpFormatException;
import java.net.URL;

public class HttpRequest {

    // TODO: Create URL class -> Using Java.net.URL instead -> ON HOLD

    URL url;
    HttpRequestMethod requestMethod;
    HttpMessageHeader messageHeader = new HttpMessageHeader();
    HttpRequestBody body = new HttpRequestBody("");

    // TODO: IsValid method
    // TODO: Getters

    public HttpRequest(URL url, HttpRequestMethod requestMethod, HttpMessageHeader messageHeader, HttpRequestBody body) {
        this.url = url;
        this.requestMethod = requestMethod;
        this.messageHeader = messageHeader;
        this.body = body;
    }


    public String toString() {
        String request = requestMethod.toString() + " " + url.getFile() + "?" + url.getQuery() + " " + "HTTP/1.0\r\n"
                        + messageHeader.toString()
                        + body.getLengthString()
                        + "\r\n"
                        + body;
        return request;
    }

    public boolean isValid(){
        // TODO: all composite objects should be valid
        return true;
    }

    public byte[] toBytes(){
        return this.toString().getBytes();
    }


}
