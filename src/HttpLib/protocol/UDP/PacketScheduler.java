package HttpLib.protocol.UDP;

import HttpLib.ByteArrayUtils;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

class PacketScheduler implements IPacketReceiverListener {
    // Will hold all PacketSender and demultiplex ACK seq# received to manage them

    private final long TIMEOUT_DELAY_MS = 10;

    private final SelectiveRepeatRegistry _seqNumReg;
    private final DatagramSocket _socket;
    private final ExecutorService _pool;
    private final ExecutorService _queuer;
    private ReentrantLock _queuingLock = new ReentrantLock();
    private HashMap<Integer, Future<?>> _runningSenders = new HashMap<>();

    public PacketScheduler(DatagramSocket socket, SelectiveRepeatRegistry seqNumReg) {
        _socket = socket;
        _seqNumReg = seqNumReg;

        int numOfCores = Runtime.getRuntime().availableProcessors();
        int blockingCoeff = 22;
        _pool = Executors.newFixedThreadPool(numOfCores * blockingCoeff);
        _queuer = Executors.newSingleThreadExecutor();
    }

    /**
     * Schedule multiple packets to be sent in order.
     * Non-Blocking
     *
     * @param packets
     */
    public void queuePackets(PseudoTCPPacket[] packets) {
        Runnable sequentialSendProcedure = () -> {
            _queuingLock.lock();
            // Queue all packets to be sent
            for (int i = 0; i < packets.length; i++)
                internalQueuePacket(packets[i]);

            _queuingLock.unlock();
        };

        _queuer.submit(sequentialSendProcedure);
    }

    public void queuePacket(PseudoTCPPacket packet) {
        _queuingLock.lock();
        internalQueuePacket(packet);
        _queuingLock.unlock();
    }

    private void sendAck(PseudoTCPPacket packet) {
        PseudoTCPPacket ack = new PseudoTCPPacket(
                packet.getPeerAddress(),
                packet.getPeerPort(),
                PacketType.ACK,
                packet.getSequenceNumber()
        );
        internalQueuePacket(packet);
    }

    private void sendSynAck(PseudoTCPPacket packet) {
        PseudoTCPPacket ack = new PseudoTCPPacket(
                packet.getPeerAddress(),
                packet.getPeerPort(),
                PacketType.SYNACK,
                packet.getSequenceNumber()
        );
        internalQueuePacket(packet);
    }

    private void internalQueuePacket(PseudoTCPPacket packet) {
        int seqNum = -1;

        // Assuming the ACK packet already has the sequence number to which it acknowledge
        if (!isACK(packet)) {
            // Wait until we can get a valid sequence number
            do {
                seqNum = _seqNumReg.requestNext();
            }
            while (seqNum < 0);
            packet.setSequenceNumber(seqNum);
        }

        int timeout = (isACK(packet) ? (int) TIMEOUT_DELAY_MS : -1);
        InetAddress address = null;
        try {
            address = InetAddress.getByAddress(packet.getPeerAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not get scheduled packet destination ip address from byte array.");
        }

        int port = ByteArrayUtils.bytesToInt(packet.getPeerPort());
        PacketSender sender = new PacketSender(_socket, packet, address, port, timeout);
        Future<?> senderTask = _pool.submit(sender);

        if (!isACK(packet))
            _runningSenders.put(seqNum, senderTask);
    }

    public synchronized boolean acknowledge(int seqNum) {
        if (seqNum < 0 || !_runningSenders.containsKey(seqNum)) return false;

        // Stop runner
        _runningSenders.get(seqNum).cancel(true);
        _runningSenders.remove(seqNum);

        return true;
    }

    private boolean isACK(PseudoTCPPacket packet) {
        return (packet.getType() != PacketType.ACK);
    }

    private void ackReceived(PseudoTCPPacket packet) {

    }

    @Override
    public void onPacketReceived(PseudoTCPPacket packet, PacketReceiver receiver) {
        int seqNum = packet.getSequenceNumber();
        // Receiving ACK, releasing that number since that packet reached his destination
        // TODO: manage acks
        if (!isACK(packet))
            _seqNumReg.release(seqNum);

        switch (packet.getType()) {

            case FIN:
            case DATA:
                sendAck(packet);
                break;
            case SYN:
                sendSynAck(packet);
                break;
            case SYNACK:
                sendAck(packet);
            case ACK:
                _seqNumReg.release(seqNum);
                acknowledge(seqNum);
                break;
        }
    }
}
