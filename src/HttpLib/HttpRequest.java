package HttpLib;

import HttpLib.Exceptions.InvalidRequestException;

import java.net.URL;

public class HttpRequest {

    // TODO: Create URL class -> Using Java.net.URL instead -> ON HOLD

    HttpMessageUrl url;
    HttpRequestMethod requestMethod;
    HttpMessageHeader messageHeader;
    HttpRequestBody body;

    // TODO: IsValid method
    // TODO: Getters

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

    public boolean isValid() throws InvalidRequestException{
        if(requestMethod == null) {
            throw new InvalidRequestException("Http method required");
        }
        if (requestMethod.equals(HttpRequestMethod.POST)) {
            throw new InvalidRequestException("POST method requires body");
        }
        if (!messageHeader.isValid()) {
            throw new InvalidRequestException("Headers are invalid");
        }

        return true;
    }

    public byte[] toBytes(){
        return this.toString().getBytes();
    }


}
