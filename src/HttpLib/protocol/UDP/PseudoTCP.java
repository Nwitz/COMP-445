package HttpLib.protocol.UDP;

import HttpLib.*;
import HttpLib.Exceptions.InvalidRequestException;
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
                        // TODO: Close connection ... who handles it ?
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

        // Open Socket
        InetSocketAddress bindAddress = new InetSocketAddress("127.0.0.1", port);
        DatagramSocket socket = new DatagramSocket();
        socket.bind(bindAddress);

        // Message receiver/constructor object
        MessageReceiver messageReceiver = new MessageReceiver(sequenceNumberRegistry);

        // Setup sending scheduler
        PacketScheduler scheduler = new PacketScheduler(socket, sequenceNumberRegistry);

        IMessageReceiverListener messageListener = new IMessageReceiverListener() {

            @Override
            public void onMessageReceived(PseudoTCPMessage message) {
                byte[] buf = message.getPayload();
                String rawRequest = new String(buf, 0, buf.length);

                HttpRequest request = null;
                HttpResponse response = null;

                try {
                    request = new HttpRequest(rawRequest);
                } catch (InvalidRequestException e) {
                    System.out.println("Received an invalid HttpRequest.");
                    System.out.println(e.getMessage());
                    System.out.println();
                    System.out.println(rawRequest);
                    response = new HttpResponse(HttpStatusCode.BadRequest);

                    PseudoTCPMessage reponseMessage = new PseudoTCPMessage(
                            message.getPeerAddress(),
                            message.getPeerPort(),
                            response.toString().getBytes());

                    scheduler.queuePackets(reponseMessage.getPackets());
                    return;
                }

                callback.onRequestReceived(request);
            }
        };
        messageReceiver.addListener(messageListener);

        // Start receiver thread
        PacketReceiver receiver = new PacketReceiver(socket, scheduler, sequenceNumberRegistry);
        receiver.addListener(scheduler);

        receiver.startReceiving();


        // TODO: Detect that it's a SYN packet type

        // Block indefinitely
    }


}
