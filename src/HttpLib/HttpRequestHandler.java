package HttpLib;


import java.io.*;
import java.net.*;

import HttpLib.Exceptions.InvalidRequestException;
import HttpLib.Exceptions.InvalidResponseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Handler to ease sending and receiving HTTP Requests & Responses.
 * It uses a TCP Socket for data transportation.
 */
public class HttpRequestHandler {

    private final int PORT = 80;
    private int nb_redirect = 0;

    public HttpRequestHandler() {
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

        // Open Socket
        InetAddress addressIp = InetAddress.getByName(request.url.getHost());
        Socket socket = new Socket(addressIp, PORT);

        PrintWriter out = new PrintWriter(socket.getOutputStream());
        InputStream in = socket.getInputStream();

        // Send request
        out.write(request.toString());
        out.flush();

        // Read entire answer
        StringBuilder res = new StringBuilder();
        int data = in.read();
        while (data != -1) {
            res.append((char) data);
            data = in.read();
        }

        out.close();
        in.close();
        socket.close();

        HttpResponse response = new HttpResponse(res.toString());
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
    public void listen(int port) throws IOException {
        InetSocketAddress bindAddress = new InetSocketAddress("127.0.0.1", port);
        ServerSocket socket = new ServerSocket();
        socket.bind(bindAddress, 10);

        Socket caller = socket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(caller.getInputStream()));
        PrintWriter out = new PrintWriter(caller.getOutputStream());

        // Read incoming request
        StringBuilder sb = new StringBuilder();
        int data;
        do {
            data = reader.read();
            sb.append((char) data);
        } while (reader.ready());

        HttpRequest request = null;
        HttpResponse response = null;

        try {
            request = new HttpRequest(sb.toString());
        } catch (InvalidRequestException e) {
            System.out.println("Received an invalid HttpRequest.");
            response = new HttpResponse(HttpStatusCode.BadRequest);
        }

        System.out.println(request);
        System.out.println(request.header.getEntries());

        // TODO: Send HttpRequest in callback
        // TODO: Callback should return HttpResponse
        // TODO: Send HttpResponse to outputStream & close everything

        // Send default answer
        if(response == null)
            response = new HttpResponse(HttpStatusCode.OK);

        out.write(response.toString());
        out.flush();

        out.close();
        reader.close();
        socket.close();
    }
}
