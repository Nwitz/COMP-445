package HttpLib.protocol.UDP;

import HttpLib.ByteArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class PseudoTCPPacket {
    private byte[] _payload;
    private PacketType _type;
    private int _sequenceNumber;
    private byte[] _peerAddress;
    private byte[] _peerPort;

    public PseudoTCPPacket(byte[] peerAddress, byte[] peerPort, byte[] payload, PacketType type) {
        _peerAddress = peerAddress;
        _peerPort = peerPort;
        _payload = payload;
        _type = type;
    }

    public PseudoTCPPacket(byte[] peerAddress, byte[] peerPort, PacketType type) {
        this(peerAddress, peerPort, new byte[0], type);
    }

    public void setSequenceNumber(int sequenceNumber) {
        _sequenceNumber = sequenceNumber;
    }

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(_type.asChar());
        outputStream.write(ByteArrayUtils.intToBytes(_sequenceNumber));
        outputStream.write(_peerAddress);
        outputStream.write(_peerPort);
        outputStream.write(_payload);
        return outputStream.toByteArray();
    }

    // helper method for quick payload extraction without caring about other content of packet.
    public static byte[] extractPayload(byte[] packet) {
        if (packet.length < 11) {
            return new byte[0];
        }
        return Arrays.copyOfRange(packet, 11, packet.length);
    }
}
