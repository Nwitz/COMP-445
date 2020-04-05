package HttpLib.protocol.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;

public class MessageReceiver {
    private SelectiveRepeatRegistry seqReg;
    private HashMap<Integer, PseudoTCPPacket> receivedPackets = new HashMap<Integer, PseudoTCPPacket>();
    private PseudoTCPMessage message;
    int terminationPacket = -1;
    private boolean monitorSocket;
    private DatagramSocket socket;

    MessageReceiver(DatagramSocket socket, SelectiveRepeatRegistry repeatRegistry) {
        seqReg = repeatRegistry;
        seqReg.addListener(eventListener);
        message = new PseudoTCPMessage();
        this.socket = socket;
    }

    public void receiveMessage() throws SocketException {
        monitorSocket = true;
        DatagramPacket packet;
        byte[] buffer;
        PacketReceivedTask packetReceivedTask;
        while (monitorSocket) {
            try {
                buffer = new byte[1024];
                packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet); // blocking
                packetReceivedTask = new PacketReceivedTask(packet);
                packetReceivedTask.start();
                // create new thread to process
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addEntry(int index, PseudoTCPPacket packet) {
        receivedPackets.put(index, packet);
    }

    IPacketEventListener eventListener = new IPacketEventListener() {
        @Override
        public void onWindowShift(int previousBase, int newBase) {
            int i = previousBase;
            while (i != newBase){
                if (newBase == terminationPacket) {
                    monitorSocket = false;
                    return;
                }
                message.addPacket(receivedPackets.get(i));
                i = seqReg.unsignedWrap(i + 1);
            }
        }

        @Override
        public void onBaseSync(int newBase) {
        }

        @Override
        public void onSendTimeout() {
        }
    };

    private class PacketReceivedTask extends Thread {
        DatagramPacket datagramPacket;
        PacketReceivedTask(DatagramPacket packet) {
            this.datagramPacket = packet;
        }

        @Override
        public void start() {
            // make packet from data
            PseudoTCPPacket packet = new PseudoTCPPacket(datagramPacket.getData());
            int sequence = packet.getSequenceNumber();

            // TODO: check if this is termination packet

            // send ack regardless of in sequence or not incase of drop
            switch (packet.getType()) {
                case DATA:
                    if (seqReg.inWindow(sequence)) {
                        addEntry(sequence, packet);
                        seqReg.release(sequence);
                    }
                    // TODO send ack
                    break;
                case SYN:
                    if (message == null) { // prevent multiple syns;
                        message = new PseudoTCPMessage(packet.getPeerAddress(), packet.getPeerPort());
                        seqReg.sync(sequence);
                        seqReg.release(sequence);
                        // TODO send ack
                    }
                case ACK:
                    break;
                    // TODO termination case.
            }
        }
    }
}
