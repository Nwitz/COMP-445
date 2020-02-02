package HttpLib;

import HttpLib.Exceptions.HttpFormatException;

public class HttpResponse {

    String version;
    String phrase = "";
    HttpStatusCode statusCode;
    HttpMessageHeader header = new HttpMessageHeader();
    HttpRequestBody body = new HttpRequestBody();

    public HttpResponse(){ }

    public HttpResponse(String responseString) throws HttpFormatException {
        // Divide body from entire response
        String[] resParts = responseString.split("(\\r\\n){2}");

        if(resParts.length < 2)
            throw new HttpFormatException("Http response not well formatted.");

        String[] beforeBody = resParts[0].split("(\\r\\n)+");

        // Handle statusline
        String[] statusLine = beforeBody[0].trim().split("\\s+");
        if(statusLine.length < 2 || statusLine.length > 3)
            throw new HttpFormatException("Http response not well formatted.");

        version = statusLine[0];
        statusCode = HttpStatusCode.get(Integer.parseInt(statusLine[1].trim()));
        if(statusLine.length == 3)
            phrase = statusLine[2];

        // Create header
        for(int i=1; i<beforeBody.length; i++)
            header.parseLine(beforeBody[i]);

        // Make sure body is in one piece
        StringBuilder bodyBuilder = new StringBuilder();
        for (int i=1; i<resParts.length; i++)
            bodyBuilder.append(resParts[i]);

        body = new HttpRequestBody(bodyBuilder.toString());
    }

    public boolean isValid(){
        return (statusCode != null && phrase != null && header.isValid());
    }

    @Override
    public String toString() {
        String rn = "\r\n";

        return version + " " + (statusCode!=null?statusCode.getValue()+" ":"") + phrase + rn
                + header.toString() + rn
                + body;
    }
}
