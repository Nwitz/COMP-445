package HttpLib;

import HttpLib.Exceptions.HttpFormatException;

public class HttpResponse {

    HttpStatusCode _status;
    HttpRequestMethod method;
    HttpMessageHeader header;
    HttpRequestBody body;

    private String _original;

    public HttpResponse(String responseString) throws HttpFormatException {
        this._original = responseString;

        // Divide body from entire response
        String[] resParts = responseString.split("[\\r\\n]{2}");

        if(resParts.length < 2)
            throw new HttpFormatException("Http response not well formatted.");

        String[] beforeBody = resParts[0].split("[\\r\\n]+");

        // Handle statusline
        String[] statusLine = beforeBody[0].trim().split("\\s+");
        if(statusLine.length != 3)
            throw new HttpFormatException("Http response not well formatted.");

        // TODO: Store version & Phrase string
        // TODO: Convert string to Enum method
        // method = HttpRequestMethod.GET;

        // Create header
        for(int i=1; i<beforeBody.length; i++)
            header.parseLine(beforeBody[i]);

        // Make sure body is in one piece
        StringBuilder bodyBuilder = new StringBuilder();
        for (int i=1; i<resParts.length; i++)
            bodyBuilder.append(resParts[i]);

        body = new HttpRequestBody(bodyBuilder.toString());
    }

    @Override
    public String toString() {
        // TODO: Send reconstructed from composite objects (Method, status, body..)
        return _original;
    }
}
