package HttpLib.protocol.UDP;

import HttpLib.ByteArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;

public class PseudoTCPPacket {
    private byte[] _payload;
    private PacketType _type;
    private int _sequenceNumber;
    private byte[] _peerAddress;
    private byte[] _peerPort;

    public PseudoTCPPacket() {}

    public PseudoTCPPacket(byte[] peerAddress, byte[] peerPort, byte[] payload, PacketType type) {
        _peerAddress = peerAddress;
        _peerPort = peerPort;
        _payload = payload;
        _type = type;
    }

    public PseudoTCPPacket(byte[] peerAddress, byte[] peerPort, PacketType type, int sequenceNumber) {
        this(peerAddress, peerPort, new byte[0], type);
        _sequenceNumber = sequenceNumber;
    }

    public PseudoTCPPacket(byte[] receivedPacket) {
        _type = PacketType.fromChar((char)receivedPacket[0]);
        _sequenceNumber = ByteArrayUtils.bytesToInt(Arrays.copyOfRange(receivedPacket, 1, 5));
        _peerAddress = Arrays.copyOfRange(receivedPacket, 5, 9);
        _peerPort = Arrays.copyOfRange(receivedPacket, 9, 11);
        _payload = Arrays.copyOfRange(receivedPacket, 11, receivedPacket.length);
    }

    public void setSequenceNumber(int sequenceNumber) {
        this._sequenceNumber = sequenceNumber;
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

    public DatagramPacket asDatagramPacket() throws IOException {
        byte[] buf = serialize();
        return new DatagramPacket(buf, buf.length);
    }

    // helper method for quick payload extraction without caring about other content of packet.
    public static byte[] extractPayload(byte[] packet) {
        if (packet.length < 11) {
            return new byte[0];
        }
        return Arrays.copyOfRange(packet, 11, packet.length);
    }

    public byte[] getPayload() {
        return _payload;
    }

    public PacketType getType() {
        return _type;
    }

    public int getSequenceNumber() {
        return _sequenceNumber;
    }

    public byte[] getPeerAddress() {
        return _peerAddress;
    }

    public byte[] getPeerPort() {
        return _peerPort;
    }
}
