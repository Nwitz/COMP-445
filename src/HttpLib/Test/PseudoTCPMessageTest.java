package HttpLib.Test;

import HttpLib.protocol.UDP.PseudoTCPMessage;
import HttpLib.protocol.UDP.PseudoTCPPacket;
import org.junit.jupiter.api.Test;

public class PseudoTCPMessageTest {

    @Test
    public void test_getPackets() {
        // Arrange
        int numPackets = PseudoTCPMessage.PAYLOAD_MAX_LENGTH * 15 + 5; // will create 16 packets
        String address = "192.168.11.135";
        int port = 5604;
        byte[] bytes = new byte[numPackets];

        // Act
        PseudoTCPMessage message = new PseudoTCPMessage(address, port, bytes);

        // Assert
        PseudoTCPPacket[] packets = message.getPackets();
        assert(packets.length == 16);
    }
}
