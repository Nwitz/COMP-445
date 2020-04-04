package HttpLib.protocol.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Will send a packet to destination indefinitely with a given timeout internal.
 */
class PacketSender implements Runnable {

    private final DatagramSocket _socket;
    private final PseudoTCPPacket _packet;
    private final InetAddress _destinationIp;
    private final int _destinationPort;
    private final long _timeout;

    PacketSender(DatagramSocket socket, PseudoTCPPacket packet, InetAddress destIp, int destPort, long timeout) {
        _socket = socket;
        _packet = packet;
        _destinationIp = destIp;
        _destinationPort = destPort;
        _timeout = timeout;
    }


    @Override
    public void run() {
        DatagramPacket dtPacket = null;
        try {
            dtPacket = _packet.asDatagramPacket();
            dtPacket.setAddress(_destinationIp);
            dtPacket.setPort(_destinationPort);

        } catch (IOException e) {
            e.printStackTrace();
            // TODO: How to handle ???
            return;
        }

        // Start send procedure
        while (true) {
            try {
                _socket.send(dtPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Sleep until acknowledged (Interrupted) or timeout & retry
            try {
                Thread.sleep(_timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
