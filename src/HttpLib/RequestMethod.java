package HttpLib;

public class RequestMethod {

    private boolean requiresBody;
    private String requestMethodString;

    public void parseLine(String methodLine) throws HttpFormatException{
        if (methodLine.equalsIgnoreCase("post")) {
            requestMethodString = "POST";
            requiresBody = true;
        } else if (methodLine.equalsIgnoreCase("get")){
            requestMethodString = "GET";
            requiresBody = false;
        } else {
            throw new HttpFormatException("Method entry not well formatted");
        }
    }


    // GET, POST...
}
