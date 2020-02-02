package HttpLib;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

import HttpLib.Exceptions.HttpFormatException;
import HttpLib.Exceptions.InvalidRequestException;
import HttpLib.Exceptions.InvalidResponseException;

import java.net.InetAddress;

public class HttpRequestHandler {

    private final int PORT = 80;

    public HttpRequestHandler() {
    }

    public HttpResponse send(HttpRequest request) throws InvalidRequestException, InvalidResponseException, IOException {
        if (!request.isValid())
            throw new InvalidRequestException();

        // Open Socket
        InetAddress addressIp = InetAddress.getByName(request.url);
        Socket socket = new Socket(addressIp, PORT);

        PrintWriter out = new PrintWriter(socket.getOutputStream());
        InputStream in = socket.getInputStream();

        // Send request
        out.write(request.toString());
        out.flush();

        // Read entire answer
        StringBuilder res = new StringBuilder();
        int data = in.read();
        while(data != -1) {
            res.append((char) data);
            data = in.read();
        }

        out.close();
        in.close();
        socket.close();

        return new HttpResponse(res.toString());
    }

    // For Asg2:
    // public void Listen(Port port, Function<HttpRequest> callback){}
}
