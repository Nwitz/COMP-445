package HttpLib.protocol.UDP;

public interface IPacketReceiverListener {

    void onPacketReceived(PseudoTCPPacket packet, PacketReceiver receiver);

}
