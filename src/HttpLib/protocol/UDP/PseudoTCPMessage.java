package HttpLib.protocol.UDP;

import HttpLib.ByteArrayUtils;

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
    private ArrayList<PseudoTCPPacket> _packets = new ArrayList<PseudoTCPPacket>();

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

    public PseudoTCPPacket[] getPackets() {
        return _packets.toArray(new PseudoTCPPacket[_packets.size()]);
    }
}
