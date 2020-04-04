package HttpLib.protocol.UDP;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class PacketSenderRegistry {
    // Will hold all PacketSender and demultiplex ACK seq# received to manage them

    private final ExecutorService _pool;
    // This needs to hold Futures
    private HashMap<Integer, Future> _runningSenders = new HashMap<>();

    public PacketSenderRegistry() {
        int numOfCores = Runtime.getRuntime().availableProcessors();
        int blockingCoeff = 22;
        _pool = Executors.newFixedThreadPool(numOfCores * blockingCoeff);
    }

    public void queuePacket(PseudoTCPPacket packet, SelectiveRepeatRegistry seqNumReg){
        int seqNum = -1;
        do {
            seqNum = seqNumReg.requestNext();
        }
        while(seqNum < 0);

        packet.setSequenceNumber(seqNum);
        PacketSender sender = new PacketSender(packet);

        Future senderTask = _pool.submit(sender);

        _runningSenders.put(seqNum, senderTask);
    }

    public boolean acknowledge(int seqNum){
        if(seqNum < 0 || !_runningSenders.containsKey(seqNum)) return false;

        _runningSenders.get(seqNum).cancel();

        return true;
    }

}
