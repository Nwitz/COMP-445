package HttpLib.Test;

import HttpLib.protocol.UDP.PacketType;
import HttpLib.protocol.UDP.PseudoTCPMessage;
import HttpLib.protocol.UDP.PseudoTCPPacket;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

public class PseudoTCPMessageTest {

    Random random = new Random();

    @Test
    public void test_getPackets() {
        // Arrange
        int numPackets = PseudoTCPMessage.PAYLOAD_MAX_LENGTH * 15 + 5; // will create 16 packets
        String address = "192.168.11.135";
        int port = 5604;
        byte[] bytes = new byte[numPackets];
        random.nextBytes(bytes);
        byte[] payload2 = Arrays.copyOfRange(bytes,
                PseudoTCPMessage.PAYLOAD_MAX_LENGTH, PseudoTCPMessage.PAYLOAD_MAX_LENGTH * 2);

        // Act
        PseudoTCPMessage message = new PseudoTCPMessage(address, port, bytes);
        PseudoTCPPacket[] packets = message.getPackets();
        PseudoTCPPacket packet2 = packets[1];

        // Assert
        assert(packets.length == 16);
        assert(Arrays.hashCode(payload2) == Arrays.hashCode(packet2.getPayload()));
    }

    @Test
    public void test_addPacket() {
        // Arrange
        byte[] address = new byte[4];
        byte[] port = new byte[2];
        PseudoTCPMessage message = new PseudoTCPMessage(address, port);
        byte[] payload1 = new byte[546];
        byte[] payload2 = new byte[784];
        byte[] payload3 = new byte[699];
        random.nextBytes(payload1);
        random.nextBytes(payload2);
        random.nextBytes(payload3);

        PseudoTCPPacket packet1 = new PseudoTCPPacket(address, port, payload1, PacketType.DATA);
        packet1.setSequenceNumber(1);
        PseudoTCPPacket packet2 = new PseudoTCPPacket(address, port, payload2, PacketType.DATA);
        packet1.setSequenceNumber(2);
        PseudoTCPPacket packet3 = new PseudoTCPPacket(address, port, payload3, PacketType.DATA);
        packet1.setSequenceNumber(3);

        // Act
        message.addPacket(packet3, 2);
        message.addPacket(packet1, 0);
        message.addPacket(packet2, 1);
        PseudoTCPPacket[] packets = message.getPackets();

        // Assert
        assert (Arrays.hashCode(packet1.getPayload()) == Arrays.hashCode(packets[0].getPayload()));

    }
}
