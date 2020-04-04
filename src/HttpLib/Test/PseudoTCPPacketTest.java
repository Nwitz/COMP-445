package HttpLib.Test;

import HttpLib.ByteArrayUtils;
import HttpLib.protocol.UDP.PacketType;
import HttpLib.protocol.UDP.PseudoTCPPacket;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class PseudoTCPPacketTest {

    Random random = new Random();

    @Test
    public void test_serialize(){
        // Arrange
        byte[] address = new byte[4];
        byte[] port = new byte[2];
        byte[] payload = new byte[45];
        PacketType type = PacketType.DATA;
        int sequenceNumber = 15;
        random.nextBytes(address);
        random.nextBytes(port);
        random.nextBytes(payload);

        // Act
        PseudoTCPPacket packet = new PseudoTCPPacket(address, port, payload, type);
        packet.setSequenceNumber(sequenceNumber);
        byte[] bytes;
        try {
            bytes = packet.serialize();
        } catch (IOException e){
            System.out.println(e.getMessage());
            return;
        }
        char receivedChar = (char)bytes[0];
        int receivedSequence = ByteArrayUtils.bytesToInt(Arrays.copyOfRange(bytes, 1, 5));
        byte[] receivedAddress = Arrays.copyOfRange(bytes, 5, 9);
        byte[] receivedPort = Arrays.copyOfRange(bytes, 9, 11);
        byte[] receivedPayload = Arrays.copyOfRange(bytes, 11, bytes.length);

        // Assert
        assert(receivedSequence == sequenceNumber);
        assert(Arrays.hashCode(address) == Arrays.hashCode(receivedAddress));
        assert(Arrays.hashCode(port) == Arrays.hashCode(receivedPort));
        assert(Arrays.hashCode(payload) == Arrays.hashCode(receivedPayload));
        assert(type.asChar() == receivedChar);
    }

    @Test
    public void test_PseudoTCPPacket_fromPacket() throws IOException {
        // Arrange
        byte[] address = new byte[4];
        byte[] port = new byte[2];
        byte[] payload = new byte[45];
        PacketType type = PacketType.DATA;
        int sequenceNumber = 15;
        random.nextBytes(address);
        random.nextBytes(port);
        random.nextBytes(payload);
        PseudoTCPPacket packet = new PseudoTCPPacket(address, port, payload, type);
        packet.setSequenceNumber(sequenceNumber);
        byte[] serialized = packet.serialize();

        // Act
        PseudoTCPPacket created = new PseudoTCPPacket(serialized);

        assert(packet.getSequenceNumber() == created.getSequenceNumber());
        assert(packet.getType() == created.getType());
        assert(Arrays.hashCode(packet.getPeerAddress()) == Arrays.hashCode(created.getPeerAddress()));
        assert(Arrays.hashCode(packet.getPeerPort()) == Arrays.hashCode(created.getPeerPort()));
        assert(Arrays.hashCode(packet.getPayload()) == Arrays.hashCode(created.getPayload()));
    }
}

