package HttpLib.protocol;

import HttpLib.Exceptions.InvalidRequestException;
import HttpLib.Exceptions.InvalidResponseException;
import HttpLib.HttpRequest;
import HttpLib.HttpResponse;
import HttpLib.HttpStatusCode;
import HttpLib.IRequestCallback;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCP implements IProtocol {
    @Override
    public String send(HttpRequest request, int port) throws InvalidRequestException, InvalidResponseException, IOException {
        // Open Socket
        InetAddress addressIp = InetAddress.getByName(request.getUrl().getHost());
        Socket socket = new Socket(addressIp, port);

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

        return res.toString();
    }

    @Override
    public void listen(int port, IRequestCallback callback) throws IOException {
        InetSocketAddress bindAddress = new InetSocketAddress("127.0.0.1", port);
        ServerSocket socket = new ServerSocket();
        socket.bind(bindAddress, 10);

        while(true) {
            Socket caller = socket.accept();
            Thread processRequestTask = new ProcessRequestTask(caller, callback);
            processRequestTask.start();
        }
    }

    private static class ProcessRequestTask extends Thread{
        private Socket caller;
        private IRequestCallback callback;
        public ProcessRequestTask(Socket socket, IRequestCallback callback) {
            caller = socket;
            this.callback = callback;
        }

        public void run() {
            try {
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
                    System.out.println(e.getMessage());
                    System.out.println();
                    System.out.println(sb.toString());
                    response = new HttpResponse(HttpStatusCode.BadRequest);

                    out.write(response.toString());
                    out.flush();
                    out.close();
                    reader.close();
                    return;
                }

                response = callback.onRequestReceived(request);

                out.write(response.toString());
                out.flush();

                out.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
