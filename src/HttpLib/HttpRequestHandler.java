package HttpLib;

public class HttpRequestHandler {

    private HttpRequest _request;

    public HttpRequestHandler(HttpRequest request) {
        _request = request;
    }

    public HttpResponse Send(){
        // Send, wait for answer and return response
        return new HttpResponse();
    }
}
