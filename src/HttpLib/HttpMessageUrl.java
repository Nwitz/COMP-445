package HttpLib;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class HttpMessageUrl {
    private URL _url;
    private HashMap<String, String> _queries = new HashMap<>();

    public HttpMessageUrl(String urlString) throws MalformedURLException {
        _url = new URL(urlString);
        parseQueries();
    }

    private void parseQueries() {
        //TODO:
    }

    String getHost() {
        return _url.getHost();
    }
}
