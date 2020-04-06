package HttpLib.protocol.UDP;

import HttpLib.*;
import HttpLib.Exceptions.InvalidRequestException;
import HttpLib.protocol.IProtocol;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class PseudoTCP implements IProtocol{
    boolean sendMessageReceived = false;

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

        // Create MessageListener
        IMessageReceiverListener messageReceiverListener = new IMessageReceiverListener() {

            @Override
            public void onMessageReceived(PseudoTCPMessage receivedMessage) {
                sendMessageReceived = true;
            }
        };

        // ===== Start a receiver thread & logic
        // Receiving logic
        messageReceiver.addListener(messageReceiverListener);

        PacketReceiver receiver = new PacketReceiver(socket, sequenceNumberRegistry);
        receiver.addListener(messageReceiver);      // EX: Listens for DATA etc..
        receiver.addListener(scheduler);            // EX: Scheduler listens for ACK

        // All listeners ready, we can start receiving and scheduling packets
        receiver.startReceiving();

        // =====
        // Perform Handshake
        scheduler.handshake(requestPacketMessage.getPeerAddressBytes(), requestPacketMessage.getPeerPortBytes());
        // Schedule the message's packets to be sent
        scheduler.queuePackets(requestPacketMessage.getPackets());

        while (!sendMessageReceived) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                return null;
            }
        }

        // Message filled, convert its packets to single payload and read
        PseudoTCPMessage message = messageReceiver.getMessage();
        message.buildPayloadFromPackets();
        byte[] payload = message.getPayload();

        return new String(payload, StandardCharsets.UTF_8);
    }

    @Override
    public void listen(int port, IRequestCallback callback) throws IOException {
        SelectiveRepeatRegistry sequenceNumberRegistry = new SelectiveRepeatRegistry();
        port = 9797;
        System.out.println("listening on port " + port);
        // Open Socket
        InetSocketAddress bindAddress = new InetSocketAddress("127.0.0.1", port);
        DatagramSocket socket = new DatagramSocket(bindAddress);

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

                response = callback.onRequestReceived(request);

                PseudoTCPMessage reponseMessage = new PseudoTCPMessage(
                        message.getPeerAddress(),
                        message.getPeerPort(),
                        response.toString().getBytes());

                scheduler.queuePackets(reponseMessage.getPackets());
                return;
            }
        };
        messageReceiver.addListener(messageListener);

        // Start receiver thread
        PacketReceiver receiver = new PacketReceiver(socket, sequenceNumberRegistry);
        receiver.addListener(messageReceiver);
        receiver.addListener(scheduler);

        receiver.startReceiving();

        // Block since we need to listen indefinitely
        while(true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



}
