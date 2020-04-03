package HttpLib.protocol.UDP;

import HttpLib.ByteArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class PseudoTCPMessage {
    private byte[] _peerAddress;
    private byte[] _peerPort;
    private byte[] _payload;
    private ArrayList<PseudoTCPPacket> _packets;

    /**
     *
     * @param peerAddress string in form 'xxx.xxx.xxx.xxx'
     * @param peerPort positive short
     * @param payload byte array of payload
     */
    public PseudoTCPMessage(String peerAddress, short peerPort, byte[] payload) {
        _peerPort = ByteArrayUtils.shortToBytes(peerPort);
        _peerAddress = ByteArrayUtils.stringIPToBytes(peerAddress);
        _payload = payload;
        createPackets();
    }

    private void createPackets() {
        boolean validLength = true;
        int index = 0;
        int end;
        byte[] bytes;
        while (validLength) {

            if (_payload.length - index < 1013) {
                validLength = false;
                end = _payload.length;
            } else {
                end = index + 1013;
            }
            bytes = Arrays.copyOfRange(_payload, index, end);
            index = end;

            _packets.add(new PseudoTCPPacket(_peerAddress, _peerPort, bytes, PacketType.DATA));
        }
    }



}
