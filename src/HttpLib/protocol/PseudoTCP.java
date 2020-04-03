package HttpLib.protocol;

import HttpLib.Exceptions.InvalidRequestException;
import HttpLib.Exceptions.InvalidResponseException;
import HttpLib.HttpRequest;
import HttpLib.IRequestCallback;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class PseudoTCP implements IProtocol {

    private SelectiveRepeatRegistry sequenceNumberRegistry = new SelectiveRepeatRegistry();

    @Override
    public String send(HttpRequest httpRequest, int destinationPort) throws IOException {
        // Open Socket
        InetAddress destinationIp = InetAddress.getByName(httpRequest.getUrl().getHost());
        // UDP udpSocket = new UDP(addressIp, port);        => Useful ?
        DatagramSocket socket = new DatagramSocket();   // Will be given an ephemeral port

        byte[] buffer = new byte[1024];

        // Send test packet
        buffer = "Hello listener!".getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, destinationIp, destinationPort);
        socket.send(packet);

        // TODO: Handshaking to peer + sync sequence number

        DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
        socket.receive(receivedPacket);

        String receivedMessage = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
        System.out.println(receivedMessage);

        return null;
    }

    @Override
    public void listen(int port, IRequestCallback callback) throws IOException {
        DatagramSocket socket = new DatagramSocket(port);

        byte[] buffer = new byte[1024];
        DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
        socket.receive(receivedPacket);

        // TODO: Reconstruct our Packet object from receivedPacket.getData()
        String receivedMessage = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
        System.out.println(receivedMessage);

        // Answer back
        InetAddress incomingAddress = receivedPacket.getAddress();
        int incomingPort = receivedPacket.getPort();


        buffer = "Hey hello there!".getBytes();
        receivedPacket = new DatagramPacket(buffer, buffer.length, incomingAddress, incomingPort);
        socket.send(receivedPacket);

    }

}
