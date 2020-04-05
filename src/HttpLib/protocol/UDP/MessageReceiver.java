package HttpLib.protocol.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;

public class MessageReceiver implements IPacketReceiverListener {

    private SelectiveRepeatRegistry seqReg;
    private HashMap<Integer, PseudoTCPPacket> receivedPackets = new HashMap<Integer, PseudoTCPPacket>();
    private PseudoTCPMessage message;
    int terminationPacketNum = -1;

    MessageReceiver(DatagramSocket socket, SelectiveRepeatRegistry repeatRegistry) {
        seqReg = repeatRegistry;
        seqReg.addListener(eventListener);
        message = new PseudoTCPMessage();
    }

    private void addEntry(int index, PseudoTCPPacket packet) {
        receivedPackets.put(index, packet);
    }

    IPacketEventListener eventListener = new IPacketEventListener() {
        @Override
        public void onWindowShift(int previousBase, int newBase) {
            int i = previousBase;
            while (i != newBase){
                message.addPacket(receivedPackets.get(i));
                i = seqReg.unsignedWrap(i + 1);

                // Message is entirely received
                if (newBase == terminationPacketNum)
                    return;
            }
        }

        @Override
        public void onBaseSync(int newBase) {
        }
    };

    @Override
    public void onPacketReceived(PseudoTCPPacket packet, PacketReceiver receiver) {
        int seqNum = packet.getSequenceNumber();
        switch (packet.getType()){
            case DATA:
                // Buffer message packet
                addEntry(seqNum, packet);
                break;
            case FIN:
                // Start message conclusion
                terminationPacketNum = seqNum;
                break;
            case SYN:
                // Potentially reset message, if midway, since resync
                message = new PseudoTCPMessage(packet.getPeerAddress(), packet.getPeerPort());
                receivedPackets = new HashMap<>();
                break;
        }
    }
}
