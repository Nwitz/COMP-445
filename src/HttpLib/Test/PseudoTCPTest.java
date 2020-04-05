package HttpLib.Test;

import static org.junit.jupiter.api.Assertions.*;

import HttpLib.*;
import HttpLib.protocol.UDP.PseudoTCP;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

class PseudoTCPTest {

    @Test
    void manualTesting() throws IOException, InterruptedException {
        PseudoTCP protocol = new PseudoTCP();

        HttpRequest req;
        try {
            req = new HttpRequest(new URL("http://localhost/"), HttpRequestMethod.GET, new HttpMessageHeader(),
                    new HttpRequestBody("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi commodo vulputate lobortis. Sed in dapibus justo. Phasellus sapien sem, laoreet sed placerat id, sodales et risus. Vestibulum aliquam lorem nec vestibulum pretium. Praesent elementum eget leo sed pharetra. Suspendisse eu odio faucibus, rhoncus ex vel, varius metus. Donec imperdiet pharetra risus, ac elementum sem lacinia ac. Sed ipsum purus, malesuada sed mi a, mattis rutrum nisl. Duis ornare sed enim ac hendrerit.\n" +
                            "\n" +
                            "Aliquam semper et lorem in tincidunt. Maecenas maximus in lorem nec placerat. Cras aliquam ornare euismod. Aliquam ullamcorper, quam vel fringilla placerat, ante magna bibendum justo, eu rutrum orci magna et eros. Nulla non porta ex. Aenean elementum mauris vitae mi hendrerit accumsan. Aenean tempor blandit mi sit amet imperdiet. Vivamus vitae hendrerit tortor, vitae dapibus ligula. Fusce facilisis ac turpis eget tincidunt. Pellentesque urna augue, commodo ut nisi id, vehicula ultrices leo. Nulla commodo vehicula mattis. Praesent metus metus, pharetra sed tellus ut, malesuada euismod erat. Sed vulputate tristique mi, quis vehicula orci posuere at. Duis tincidunt purus sit amet orci tincidunt congue. Aliquam erat volutpat. Nunc in urna sed orci malesuada finibus.\n" +
                            "\n" +
                            "Interdum et malesuada fames ac ante ipsum primis in faucibus. Suspendisse sed mollis mauris. Proin porttitor, metus et suscipit viverra, dolor erat accumsan mi, ut scelerisque libero neque ac elit. Proin tristique nisi hendrerit, feugiat magna at, gravida nibh. Cras ac efficitur ligula, non placerat purus. Proin maximus ipsum finibus risus facilisis, in iaculis lorem tincidunt. Sed cursus sagittis libero ut accumsan. Fusce sapien tortor, mattis non dui nec, malesuada pellentesque nisi. Lorem ipsum dolor sit amet, consectetur adipiscing elit.\n" +
                            "\n" +
                            "In efficitur imperdiet nisi maximus consectetur. Integer aliquet consectetur ex, vitae auctor velit ornare sed. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Aliquam in lectus ac massa consequat pulvinar. Aliquam malesuada ipsum molestie elit ultricies aliquet id non ex. Vivamus faucibus sem in pulvinar aliquam. Morbi fringilla rhoncus urna a ultricies. Ut sed quam massa. Proin vel viverra dolor, eu ultrices nulla. Maecenas vestibulum mi sit amet elementum volutpat. Aliquam aliquam facilisis orci vitae tincidunt. Sed at lacus rhoncus, venenatis lectus ac, egestas libero. Cras finibus tempor risus, fringilla ultricies dui laoreet ac. Sed volutpat augue augue, vitae tincidunt sapien volutpat volutpat."));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }

        IRequestCallback callback = request -> {
            System.out.println("Received request:");
            System.out.println(request.toString());
            return new HttpResponse(HttpStatusCode.OK);
        };

        Runnable listenTask = () -> {
            try {
                protocol.listen(9797, callback);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        Thread t = new Thread(listenTask);
        t.start();

        protocol.send(req, 9797);

        t.join();
    }

}