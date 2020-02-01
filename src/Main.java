import HttpLib.*;
import HttpLib.Exceptions.HttpFormatException;
import HttpLib.Exceptions.InvalidRequestException;

import java.io.IOException;
import java.net.URL;


public class Main {
    public static void main(String args[]) throws HttpFormatException, InvalidRequestException, IOException {

        URL url = new URL("http://postman-echo.com/get?key1=value1");
        HttpRequest request = new HttpRequest(
                url,
                HttpRequestMethod.GET,
                new HttpMessageHeader(),
                new HttpRequestBody("")
        );

        HttpResponse response = new HttpRequestHandler().send(request);
        System.out.println(response);
    }
}
