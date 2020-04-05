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
    private SelectiveRepeatRegistry seqReg;
    private final ExecutorService receptorExecutor;
    private Future<?> receptorFuture;
    private ArrayList<IPacketReceiverListener> _listeners = new ArrayList<>();

    public PacketReceiver(DatagramSocket socket, SelectiveRepeatRegistry repeatRegistry) {
        this.socket = socket;
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

            // Notify
            for (IPacketReceiverListener listener : _listeners)
                listener.onPacketReceived(packet, parentReceiver);

        }
    }

}
