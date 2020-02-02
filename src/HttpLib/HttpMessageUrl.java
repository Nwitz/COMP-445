package HttpLib;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

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
        StringBuilder fileAndQuerySB = new StringBuilder(" ");
        boolean trailingSpaceNeeded = false;
        if(_url.getFile() != null && !_url.getFile().isEmpty()) {
            fileAndQuerySB.append(_url.getFile());
            trailingSpaceNeeded = true;
        }

        if (_url.getQuery() != null) {
            fileAndQuerySB.append("?").append(_url.getQuery());
            trailingSpaceNeeded = true;
        }

        if (trailingSpaceNeeded) {
            fileAndQuerySB.append(" ");
        }

        return fileAndQuerySB.toString();
    }

    String getHost() {
        return _url.getHost();
    }
}
