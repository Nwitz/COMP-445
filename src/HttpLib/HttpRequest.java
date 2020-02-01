package HttpLib;

public class HttpRequest {

    // TODO: Create URL class
    String url;
    HttpRequestMethod requestMethod = new HttpRequestMethod();
    HttpMessageHeader messageHeader = new HttpMessageHeader();
    HttpRequestBody body = new HttpRequestBody();

    // TODO: IsValid method
    // TODO: Getters

    public HttpRequest(String url, HttpRequestMethod requestMethod, HttpMessageHeader messageHeader, HttpRequestBody body) {
        this.url = url;
        this.requestMethod = requestMethod;
        this.messageHeader = messageHeader;
        this.body = body;
    }

    public HttpRequest(String[] parameters) throws HttpFormatException {
        requestMethod.parseLine(parameters[0]);
        int i = 1;
        while (i < parameters.length - 1) {
            if (parameters[i].equalsIgnoreCase(HttpFlag.HEADER.getValue())) {
                messageHeader.ParseLine(parameters[i + 1]);
                i += 2;
                continue;
            }

            // Add similar parsing to data field.
        }
    }


    public String toString() {
        // TODO: todo
        return "";
    }

//    public Byte[] toBytes(){
//
//    }


}
