package HttpLib.protocol.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class PacketReceiver {

    private DatagramSocket socket;
    private PacketScheduler scheduler;
    private SelectiveRepeatRegistry seqReg;
    private final ExecutorService receptorExecutor;
    private Future<?> receptorFuture;
    private HashMap<Integer, PseudoTCPPacket> receivedPackets = new HashMap<Integer, PseudoTCPPacket>();
    private ArrayList<IPacketReceiverListener> _listeners = new ArrayList<>();

    public PacketReceiver(DatagramSocket socket, PacketScheduler scheduler, SelectiveRepeatRegistry repeatRegistry) {
        this.socket = socket;
        this.scheduler = scheduler;
        seqReg = repeatRegistry;
        receptorExecutor = Executors.newSingleThreadExecutor();
    }

    public void addListener(IPacketReceiverListener listener) {
        _listeners.add(listener);
    }

    public void startReceiving() {
        // If previous one not stopped, stop it first to restart it
        stopReceiving();

        Runnable receptorTask = () -> {
            byte[] buffer;
            DatagramPacket packet;
            PacketReceivedTask packetReceivedTask;
            while (true) {
                try {
                    buffer = new byte[PseudoTCPMessage.PACKET_MAX_LENGTH];
                    packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet); // blocking

                    // Create new thread to process
                    packetReceivedTask = new PacketReceivedTask(packet, this);
                    packetReceivedTask.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        receptorFuture = receptorExecutor.submit(receptorTask);
    }

    public void stopReceiving() {
        if(receptorFuture != null && !receptorFuture.isDone())
            receptorFuture.cancel(true);
    }

    public PseudoTCPPacket flush(int i){
        return receivedPackets.remove(i);
    }

    private class PacketReceivedTask extends Thread {
        DatagramPacket datagramPacket;
        PacketReceiver parentReceiver;

        PacketReceivedTask(DatagramPacket packet, PacketReceiver receiver) {
            this.datagramPacket = packet;
            parentReceiver = receiver;
        }

        @Override
        public void start() {
            // Make packet from data
            PseudoTCPPacket packet = new PseudoTCPPacket(datagramPacket.getData());
            int sequenceNumber = packet.getSequenceNumber();

            // Handle receiving handshaking
            // Packet type that should not be changing the data sequence numbers
            switch (packet.getType()) {
                case SYN:
                    seqReg.sync(sequenceNumber);
                    // send SYN-ACK with packet.getSequenceNumber()
                    // Raise flag to wait for last ACK
                    break;
                case SYNACK:
                    // Forward to scheduler ?
                    break;
                case ACK:
                    // Check for end of 3-way (in waiting) : Should not bubble up in that case
                    // Raise HandshakingDone event ?
                    // else, let go up in event
                    break;
                default:
                    if (seqReg.inWindow(sequenceNumber))
                        receivedPackets.put(sequenceNumber, packet);
            }

            // Notify
            for (IPacketReceiverListener listener : _listeners)
                listener.onPacketReceived(packet, parentReceiver);

        }
    }

}
