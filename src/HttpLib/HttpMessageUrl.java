package HttpLib;

import java.net.URL;
import java.util.HashMap;

/**
 * A java.net.URL wrapper to allow for query arguments parsing and others.
 */
public class HttpMessageUrl {
    private URL _url;
    private HashMap<String, String> _queries = new HashMap<>();

    public HttpMessageUrl(URL url){
        _url = url;
        parseQueries();
    }

    private void parseQueries() {
        //TODO:
    }

    public String getFileAndQuery() {
        StringBuilder fileAndQuerySB = new StringBuilder();
        if(_url.getFile() != null && !_url.getFile().isEmpty()) {
            fileAndQuerySB.append(_url.getFile());
        }

        if (_url.getQuery() != null) {
            fileAndQuerySB.append("?").append(_url.getQuery());
        }

        return fileAndQuerySB.toString();
    }

    public String getPath() {
        return _url.getPath();
    }

    public String getHost() {
        return _url.getHost();
    }
}
