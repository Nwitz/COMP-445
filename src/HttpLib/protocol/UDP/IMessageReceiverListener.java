package HttpLib.protocol.UDP;

public interface IMessageReceiverListener {

    void onMessageReceived(PseudoTCPMessage message);

}
