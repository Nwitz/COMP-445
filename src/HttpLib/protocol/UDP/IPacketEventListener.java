package HttpLib.protocol.UDP;

interface IPacketEventListener {

    void onWindowShift(int previousBase, int newBase);
    void onBaseSync(int newBase);

}
