package HttpLib;

public class HttpResponse {

    private HttpStatusCode _status;
    private String _original;

    public HttpResponse(String responseString) {
        // TODO: Parse entire response received, and build corresponding object

        // SHOULD BE REMOVED | Was only for toString to work for now
        this._original = responseString;
    }

    public HttpResponse(HttpStatusCode _status) {
        this._status = _status;
    }

    @Override
    public String toString() {
        return _original;
    }
}
