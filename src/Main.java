import HttpLib.*;
import HttpLib.Exceptions.HttpFormatException;
import HttpLib.Exceptions.InvalidRequestException;

import java.io.IOException;


public class Main {
    public static void main(String args[]) throws HttpFormatException, InvalidRequestException, IOException {
        HttpRequest request = new HttpRequest(
                "postman-echo.com",
                HttpRequestMethod.GET,
                new HttpMessageHeader(),
                new HttpRequestBody("")
        );

        HttpResponse response = new HttpRequestHandler().send(request);
        System.out.println(response);
    }
}
