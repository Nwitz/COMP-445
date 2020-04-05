package HttpLib.protocol.UDP;

import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;

public class MessageReceiver implements IPacketReceiverListener {

    private SelectiveRepeatRegistry seqReg;
    private HashMap<Integer, PseudoTCPPacket> receivedPackets = new HashMap<Integer, PseudoTCPPacket>();
    private ArrayList<IMessageReceiverListener> _listeners = new ArrayList<>();
    private PseudoTCPMessage message;
    int terminationPacketNum = -1;
    boolean isActive = true;

    MessageReceiver(SelectiveRepeatRegistry repeatRegistry) {
        seqReg = repeatRegistry;
        seqReg.addListener(eventListener);
        message = new PseudoTCPMessage();
    }

    public void addListener(IMessageReceiverListener listener) {
        _listeners.add(listener);
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
                if (newBase == terminationPacketNum){
                    isActive = false;

                    // Notify that message reception is complete
                    for (IMessageReceiverListener listener : _listeners)
                        listener.onMessageReceived(message);
                }
            }
        }

        @Override
        public void onBaseSync(int newBase) {
        }
    };

    @Override
    public void onPacketReceived(PseudoTCPPacket packet, PacketReceiver receiver) {
        // Listen for reception only until the message is complete
        if(!isActive) return;

        int seqNum = packet.getSequenceNumber();
        switch (packet.getType()){
            case DATA:
                // Buffer message packet
                if(seqReg.inWindow(seqNum))
                    addEntry(seqNum, packet);

                // TODO: Release number + Send ACK to scheduler (Meaning we need a reference to it)
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
