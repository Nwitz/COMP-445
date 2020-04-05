package HttpLib.protocol.UDP;

import HttpLib.ByteArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class PseudoTCPMessage {
    public static final int PAYLOAD_MAX_LENGTH = 1013;
    public static final int PACKET_MAX_LENGTH = 1024;

    private String _peerAddress;
    private int _peerPort;
    private byte[] _peerAddressBytes;
    private byte[] _peerPortBytes;
    private byte[] _payload;
    private ArrayList<PseudoTCPPacket> _packets = new ArrayList<PseudoTCPPacket>(10);

    /**
     *
     * @param peerAddress string in form 'xxx.xxx.xxx.xxx'
     * @param peerPort positive short
     * @param payload byte array of payload
     */
    public PseudoTCPMessage(String peerAddress, int peerPort, byte[] payload) {
        _peerAddress = peerAddress;
        _peerPort = peerPort;

        byte[] port = ByteArrayUtils.intToBytes(peerPort);
        _peerPortBytes = Arrays.copyOfRange(port, 2,4); // take two least significant bytes of port integer.
        _peerAddressBytes = ByteArrayUtils.stringIPToBytes(peerAddress);
        _payload = payload;
        createPackets();
    }

    public PseudoTCPMessage(byte[] address, byte[] port) {
        _peerPort = ByteArrayUtils.bytesToFakeShort(port);
        _peerAddress = ByteArrayUtils.bytesToStringIP(address);
        _peerAddressBytes = address;
        _peerPortBytes = port;
    }

    public PseudoTCPMessage() { }

    private void createPackets() {
        boolean validLength = true;
        int index = 0;
        int end;
        byte[] bytes;
        while (validLength) {

            if (_payload.length - index < PAYLOAD_MAX_LENGTH) {
                validLength = false;
                end = _payload.length;
            } else {
                end = index + PAYLOAD_MAX_LENGTH;
            }
            bytes = Arrays.copyOfRange(_payload, index, end);
            index = end;

            _packets.add(new PseudoTCPPacket(_peerAddressBytes, _peerPortBytes, bytes, PacketType.DATA));
        }
    }

    public void buildPayloadFromPackets() throws IOException {
        _packets.trimToSize();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        for (PseudoTCPPacket packet : _packets) {
            os.write(packet.getPayload());
        }
        _payload = os.toByteArray();
    }

    public void addPacket(PseudoTCPPacket packet) {
        _packets.add(packet);
    }

    public void addPacketAt(PseudoTCPPacket packet, int index) {
        growPackets(index);
        _packets.set(index, packet);
    }

    private void growPackets(int index) {
        while (_packets.size() <= index)
            _packets.add(new PseudoTCPPacket());

    }

    public PseudoTCPPacket[] getPackets() {
        return _packets.toArray(new PseudoTCPPacket[0]);
    }

    public String getPeerAddress() {
        return _peerAddress;
    }

    public int getPeerPort() {
        return _peerPort;
    }

    public byte[] getPeerAddressBytes() {
        return _peerAddressBytes;
    }

    public byte[] getPeerPortBytes() {
        return _peerPortBytes;
    }

    public byte[] getPayload() {
        try {
            buildPayloadFromPackets();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return _payload;
    }
}
