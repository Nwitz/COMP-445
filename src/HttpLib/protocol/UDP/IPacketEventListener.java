package HttpLib.protocol.UDP;

interface IPacketEventListener {

    void onCanRequest();
    void onBaseSync(int newBase);
    void onSendTimeout();

}
