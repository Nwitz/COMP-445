package HttpLib.Test;

import static org.junit.jupiter.api.Assertions.*;

import HttpLib.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

class PseudoTCPTest {

    @Test
    void manualTesting() throws IOException, InterruptedException {
//        PseudoTCP protocol = new PseudoTCP();
//
//        HttpRequest req;
//        try {
//            req = new HttpRequest(new URL("http://localhost/"), HttpRequestMethod.GET, new HttpMessageHeader());
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            return;
//        }
//
//        IRequestCallback callback = request -> new HttpResponse();
//
//        Runnable listenTask = () -> {
//            try {
//                protocol.listen(9797, callback);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        };
//
//        Thread t = new Thread(listenTask);
//        t.start();
//
//        protocol.send(req, 9797);
//
//        t.join();
    }

}