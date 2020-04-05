package HttpLib.protocol.UDP;

import HttpLib.ByteArrayUtils;
import HttpLib.HttpRequest;
import HttpLib.IRequestCallback;
import HttpLib.protocol.IProtocol;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class PseudoTCP implements IProtocol {

    @Override
    public String send(HttpRequest httpRequest, int destinationPort) throws IOException {
        SelectiveRepeatRegistry sequenceNumberRegistry = new SelectiveRepeatRegistry();

        // Open Socket
        InetAddress destinationIp = InetAddress.getByName(httpRequest.getUrl().getHost());
        DatagramSocket socket = new DatagramSocket();   // Will be given an ephemeral port

        // Setup sending scheduler
        PacketScheduler scheduler = new PacketScheduler(socket, sequenceNumberRegistry);

        PseudoTCPMessage requestPacketMessage = new PseudoTCPMessage(
                ByteArrayUtils.bytesToStringIP(destinationIp.getAddress()),
                destinationPort,
                httpRequest.toString().getBytes());

        // Message receiver/constructor object
        MessageReceiver messageReceiver = new MessageReceiver(sequenceNumberRegistry);


        // ===== Start a receiver thread & logic

        // Receiving logic
        IPacketReceiverListener receiverListener = new IPacketReceiverListener() {

            @Override
            public void onPacketReceived(PseudoTCPPacket packet, PacketReceiver receiver) {
                switch (packet.getType()){
                    case TER:
                        // Close connection ... who handles it ?
                        break;
                }
            }
        };

        PacketReceiver receiver = new PacketReceiver(socket, scheduler, sequenceNumberRegistry);
        receiver.addListener(receiverListener);
        receiver.addListener(scheduler);            // EX: Scheduler listens for ACK
        receiver.addListener(messageReceiver);      // EX: Listens for DATA etc..

        // All listeners ready, we can start receiving and scheduling packets
        receiver.startReceiving();

        // =====
        // Schedule the message's packets to be sent
        scheduler.queuePackets(requestPacketMessage.getPackets());

        // TODO: Have a messageReceiverListener to receive to Response when ready, and return it as HTTPResponse.
        // TODO: Return reconstructed HTTPResponse( string )


        // TODO: Close connection
        return null;
    }

    @Override
    public void listen(int port, IRequestCallback callback) throws IOException {
        SelectiveRepeatRegistry sequenceNumberRegistry = new SelectiveRepeatRegistry();

        DatagramSocket socket = new DatagramSocket();
        DatagramChannel channel = socket.getChannel();
        byte[] bytes = new byte[PseudoTCPMessage.PACKET_MAX_LENGTH];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        // read from channel
        channel.read(buffer);
        // fill packet
        PseudoTCPPacket packet = new PseudoTCPPacket(buffer.array());
        // if packet is SYN,
        sequenceNumberRegistry.sync(6);



        // TODO: Detect that it's a SYN packet type

    }


}
