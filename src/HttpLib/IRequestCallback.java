package HttpLib;

public interface IRequestCallback {
    public HttpResponse onRequestReceived(HttpRequest request);
}
