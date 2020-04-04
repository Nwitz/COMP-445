package HttpLib.protocol.UDP;

import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

class PacketScheduler {
    // Will hold all PacketSender and demultiplex ACK seq# received to manage them

    private final long TIMEOUT_DELAY_MS = 10;

    private final DatagramSocket _socket;
    private final ExecutorService _pool;
    private final ExecutorService _queuer;
    private ReentrantLock _queuingLock = new ReentrantLock();
    private HashMap<Integer, Future<?>> _runningSenders = new HashMap<>();

    public PacketScheduler(DatagramSocket socket) {
        _socket = socket;

        int numOfCores = Runtime.getRuntime().availableProcessors();
        int blockingCoeff = 22;
        _pool = Executors.newFixedThreadPool(numOfCores * blockingCoeff);
        _queuer = Executors.newSingleThreadExecutor();
    }

    /**
     * Schedule multiple packets to be sent in order.
     * Non-Blocking
     * @param packets
     * @param seqNumReg
     */
    public void queuePackets(PseudoTCPPacket[] packets, SelectiveRepeatRegistry seqNumReg) {
        Runnable sequentialSendProcedure = () -> {
            _queuingLock.lock();
            // Queue all packets to be sent
            for (int i = 0; i < packets.length; i++)
                internalQueuePacket(packets[i], seqNumReg);

            _queuingLock.unlock();
        };

        _queuer.submit(sequentialSendProcedure);
    }

    public void queuePacket(PseudoTCPPacket packet, SelectiveRepeatRegistry seqNumReg) {
        _queuingLock.lock();
        internalQueuePacket(packet, seqNumReg);
        _queuingLock.unlock();
    }

    private void internalQueuePacket(PseudoTCPPacket packet, SelectiveRepeatRegistry seqNumReg) {
        // Wait until we can get a valid sequence number
        int seqNum = -1;
        do {
            seqNum = seqNumReg.requestNext();
        }
        while (seqNum < 0);

        packet.setSequenceNumber(seqNum);
        // TODO: Check PacketType to determine if we need retry or not
        PacketSender sender = new PacketSender(_socket, packet, packet, 9797, TIMEOUT_DELAY_MS);
        Future<?> senderTask = _pool.submit(sender);

        _runningSenders.put(seqNum, senderTask);
    }

    public synchronized boolean acknowledge(int seqNum) {
        if (seqNum < 0 || !_runningSenders.containsKey(seqNum)) return false;

        // Stop runner
        _runningSenders.get(seqNum).cancel(true);

        return true;
    }

}
