package HttpLib;


import HttpLib.Exceptions.InvalidRequestException;
import HttpLib.Exceptions.InvalidResponseException;
import HttpLib.protocol.IProtocol;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Handler to ease sending and receiving HTTP Requests & Responses.
 * It uses a TCP Socket for data transportation.
 */
public class HttpRequestHandler {

    private final int PORT = 80;
    private int nb_redirect = 0;
    private IProtocol protocol;

    public HttpRequestHandler(IProtocol protocol) {
        this.protocol = protocol;
    }

    /**
     * Given a valid HTTPRequest, it sends the requests and build a HTTPResponse out of the received data.
     * @param request
     * @return
     * @throws InvalidRequestException
     * @throws InvalidResponseException
     * @throws IOException
     */
    public HttpResponse send(HttpRequest request) throws InvalidRequestException, InvalidResponseException, IOException {

        if (!request.isValid())
            throw new InvalidRequestException();

        int port = request.getUrl().getPort();
        if (port < 0) {
            port = PORT;
        }
        String res = protocol.send(request, port);

        HttpResponse response = new HttpResponse(res);
        // Recurse for redirection, max 5 times
        if (response.statusCode.getValue() > 300 && response.statusCode.getValue() < 400) {
            if(nb_redirect < 5){
                nb_redirect++;

                URL url = null;
                try {
                    url = new URL(response.header.getEntries().get("Location"));
                } catch (MalformedURLException e) {
                    throw new InvalidResponseException("Cannot redirect to invalid URL destination: " + response.header.getEntries().get("Location"));
                }

                request.url = new HttpMessageUrl(url);
                return this.send(request);
            }else{
                throw new InvalidRequestException("Trying to redirect more than 5 times.");
            }
        }

        return response;
    }

    /**
     * Listens to a port for receiving a http request.
     * @param port
     * @throws IOException
     */
    public void listen(int port, IRequestCallback callback) throws IOException {
        protocol.listen(port, callback);
    }


}
