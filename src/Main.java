import HttpLib.Exceptions.HttpFormatException;
import HttpLib.Exceptions.InvalidRequestException;
import HttpLib.*;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.URL;


public class Main {
    public static void main(String[] args) throws HttpFormatException, InvalidRequestException, IOException {


//        String post = "POST";
//        HttpRequestMethod method = HttpRequestMethod.valueOf(post);
//
//        String garbageString = "garbage";
//        HttpRequestMethod garbage = HttpRequestMethod.valueOf(garbageString);
//
//        System.out.println(method);
//        System.out.println(garbage);


        System.out.println(args[0]);
        if (args.length == 0) {
            args = new String[]{"POST", "-h", "key1:value1", "-h", "key2:value2", "-d", "{String: Hello how's it going?}", "http://postman-echo.com/post?key1=vaelue1"};
        }
        Httpc httpc = new Httpc();
        httpc.parseArgs(args);



//        URL url = new URL("http://postman-echo.com/get?key1=value1");
//        HttpRequest request = new HttpRequest(
//                url,
//                HttpRequestMethod.GET,
//                new HttpMessageHeader(),
//                new HttpRequestBody("")
//        );
//
//        HttpResponse response = new HttpRequestHandler().send(request);
//        System.out.println(response);
    }
}
