public class Httpc {

    // TODO: Handle get|post command (2 different method maybe?)
    // TODO: FileReader -> to body
    // TODO: Inline-Data -> body
    // TODO: Check for application argument library

    public void parseArgs(String args[]) {
        RequestData requestData = new RequestData();
        requestData.method = args[0];
        requestData.url = args[1];

    }

    public String createRequest(RequestData requestData) {
        StringBuilder requestBuilder = new StringBuilder();

        if (requestData.method.equals("POST")) {
            requestBuilder.append("POST /post ");
        } else if (requestData.method.equals("GET")) {
            requestBuilder.append("GET /get ");
        } else {
            return "";
        }
        requestBuilder.append(requestData.httpVersion).append("\r\n");

        for (String header : requestData.headers) {
            requestBuilder.append(header).append("\r\n");
        }

        requestBuilder.append("Content-Length: ").append(requestData.body.length()).append("\r\n");
        requestBuilder.append("\r\n");
        requestBuilder.append(requestData.body);

        return requestBuilder.toString();

    }

    public void sendRequest() {

    }
}
