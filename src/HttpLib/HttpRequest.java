package HttpLib;

public class HttpRequest {
    MessageHeader messageHeader = new MessageHeader();
    RequestMethod requestMethod = new RequestMethod();

    public HttpRequest(String[] parameters) throws HttpFormatException {
        requestMethod.parseLine(parameters[0]);
        int i = 1;
        while (i < parameters.length - 1) {
            if (parameters[i].equalsIgnoreCase(HttpFlag.HEADER.getValue())) {
                messageHeader.ParseLine(parameters[i+1]);
                i+= 2;
                continue;
            }

            // Add similar parsing to data field.
        }
    }



//    public String toString() {
//        return "";
//    }

//    public Byte[] toBytes(){
//
//    }


}
