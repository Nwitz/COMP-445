package HttpLib;

import HttpLib.Exceptions.HttpFormatException;

public class HttpRequest {

    // TODO: Create URL class
    String url;
    HttpRequestMethod requestMethod;
    HttpMessageHeader messageHeader = new HttpMessageHeader();
    HttpRequestBody body = new HttpRequestBody("");

    // TODO: IsValid method
    // TODO: Getters

    public HttpRequest(String url, HttpRequestMethod requestMethod, HttpMessageHeader messageHeader, HttpRequestBody body) {
        this.url = url;
        this.requestMethod = requestMethod;
        this.messageHeader = messageHeader;
        this.body = body;
    }


    public String toString() {
        // TODO: todo
        return "";
    }

    public boolean isValid(){
        // TODO: all composite objects should be valid
        return true;
    }

//    public Byte[] toBytes(){
//
//    }


}
