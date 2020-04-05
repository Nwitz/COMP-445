package HttpLib.protocol.UDP;

import HttpLib.ByteArrayUtils;
import HttpLib.HttpRequest;
import HttpLib.IRequestCallback;
import HttpLib.protocol.IProtocol;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.util.Set;

public class PseudoTCP implements IProtocol {

    @Override
    public String send(HttpRequest httpRequest, int destinationPort) throws IOException {
        SelectiveRepeatRegistry sequenceNumberRegistry = new SelectiveRepeatRegistry();

        // Open Socket
        InetAddress destinationIp = InetAddress.getByName(httpRequest.getUrl().getHost());
        DatagramSocket socket = new DatagramSocket();   // Will be given an ephemeral port

        // Setup sending scheduler
        PacketScheduler scheduler = new PacketScheduler(socket);

        PseudoTCPMessage requestPacketMessage = new PseudoTCPMessage(
                ByteArrayUtils.bytesToStringIP(destinationIp.getAddress()),
                destinationPort,
                httpRequest.toString().getBytes());

        // TODO: (Handshaking) Establish reliable connection with peer

        // TODO: Start a receiver thread of some sort to receive the ACK. (In Scheduler)

        // Schedule the message's packets to be sent
        scheduler.queuePackets(requestPacketMessage.getPackets(), sequenceNumberRegistry);

        // TODO: Once message sent entirely, listen for response message

        // TODO: Close connection

        // TODO: Return reconstructed HTTPResponse( string )
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
