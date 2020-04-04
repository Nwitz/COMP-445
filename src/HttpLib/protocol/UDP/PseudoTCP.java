package HttpLib.protocol.UDP;

import HttpLib.HttpRequest;
import HttpLib.IRequestCallback;
import HttpLib.protocol.IProtocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.*;

public class PseudoTCP implements IProtocol {

    private final long TIMEOUT_DELAY_MS = 10;

    @Override
    public String send(HttpRequest httpRequest, int destinationPort) throws IOException {
        SelectiveRepeatRegistry sequenceNumberRegistry = new SelectiveRepeatRegistry();

        // Open Socket
        InetAddress destinationIp = InetAddress.getByName(httpRequest.getUrl().getHost());
        // UDP udpSocket = new UDP(addressIp, port);        => Useful ?
        DatagramSocket socket = new DatagramSocket();   // Will be given an ephemeral port


        // TODO: Handshaking to peer + sync sequence number
        handshakeInit(socket, sequenceNumberRegistry, destinationIp, destinationPort);


        return null;
    }

    @Override
    public void listen(int port, IRequestCallback callback) throws IOException {
        SelectiveRepeatRegistry sequenceNumberRegistry = new SelectiveRepeatRegistry();
        DatagramSocket socket = new DatagramSocket(port);

        byte[] buffer = new byte[1024];
        DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
        socket.receive(receivedPacket);

        // TODO: Detect that it's a SYN packet type

        handshakeReception(socket, receivedPacket, sequenceNumberRegistry);

    }

    private void handshakeInit(DatagramSocket socket, SelectiveRepeatRegistry seqReg, InetAddress ip, int port) {
        // PseudoTCPPacket packet = new PseudoTCPPacket();

        // TODO: These Future Task should be generic for any packet
        Runnable synTask = () -> {
            seqReg.sync(13);    // For test

            // TODO: Replace with Packet class
            byte[] buffer = new byte[1024];
            buffer[0] = (byte) seqReg.requestNext();    // Should be 13 for this test
            DatagramPacket dtPacket = new DatagramPacket(buffer, buffer.length, ip, port);
            try {
                socket.send(dtPacket);
            } catch (IOException e) {
                // TODO
                e.printStackTrace();
            }

            // Wait for SYN-ACK
            DatagramPacket synackPacket = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(synackPacket);
            } catch (IOException e) {
                // TODO
                e.printStackTrace();
            }

            // For testing
            System.out.println("Received SYN-ACK value: " + synackPacket.getData()[0]);

            // TODO: If so, send ack to conclude handshaking init

        };

        Future synFuture = _pool.submit(synTask);

        //
        try {
            synFuture.get(TIMEOUT_DELAY_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            // TODO
            e.printStackTrace();
        } catch (TimeoutException e) {
            // TODO: HANDSHAKING FAILURE
        }
    }

    private void handshakeReception(DatagramSocket socket, DatagramPacket receivedSynPacket, SelectiveRepeatRegistry seqReg) throws IOException {
        // TODO: Clean + Replace with proper class

        // TEST
        byte receivedByte = receivedSynPacket.getData()[0];
        System.out.println("Received SYN value: " + receivedByte);

        seqReg.sync(receivedByte);

        // Answer back after sync
        InetAddress incomingAddress = receivedSynPacket.getAddress();
        int incomingPort = receivedSynPacket.getPort();

        byte[] buffer = new byte[1024];
        buffer[0] = (byte) seqReg.requestNext();    // Should be the received seq number
        receivedSynPacket = new DatagramPacket(buffer, buffer.length, incomingAddress, incomingPort);
        socket.send(receivedSynPacket);

        // TODO: Receive ACK for the SYN-ACK (In a Future)

    }

}
